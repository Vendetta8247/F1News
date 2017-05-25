package ua.com.vendetta8247.f1news;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class SavedSitesList extends ActionBarActivity {

    int[] numberOfNews = new int[5];

    private RecyclerView.LayoutManager mLayoutManager;
    ListView mDrawerList;
    DrawerLayout mDrawerLayout;
    List<SavedCard> cardList;
    SavedSiteListAdapter sa;
    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ContextGetter.getAppContext());
String locale;
    SharedPreferences.OnSharedPreferenceChangeListener listener;

    boolean f1newsRuCheckbox = sharedPref.getBoolean("f1news_ru_checkbox", true);
    boolean formulaNewsCheckbox = sharedPref.getBoolean("formula_news_com_checkbox", true);
    boolean euronewsCheckbox = sharedPref.getBoolean("euronews_checkbox", true);
    boolean planetCheckbox = sharedPref.getBoolean("planetf1_checkbox", true);
    boolean crashCheckbox = sharedPref.getBoolean("crash_checkbox", true);

    @Override
    protected void onResume() {
        super.onResume();
        sa.removeAllItemsFromList();

        for(SavedCard card: cardList) {
            sa.addItem(card);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_sites_list);
        NavDrawerItem[] drawerItems = {new NavDrawerItem(R.drawable.homeicon, getString(R.string.main_page_text)), new NavDrawerItem(R.drawable.downloadicon, getString(R.string.downloaded_articles_text)), new NavDrawerItem(R.drawable.tableicon, getString(R.string.rating_text)), new NavDrawerItem(R.drawable.racehelmet, getString(R.string.pilots_text)),new NavDrawerItem(R.drawable.settingsicon, getString(R.string.settings_text))};
        getSupportActionBar().setTitle(getString(R.string.downloaded_articles_text));
        locale = Locale.getDefault().getLanguage();
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                recreate();
            }
        };

        File F1NewsRuFolder = new File(ContextGetter.getAppContext().getFilesDir().toString() + "/F1News.ru");
        if(!F1NewsRuFolder.exists()) F1NewsRuFolder.mkdir();
        numberOfNews[0] = F1NewsRuFolder.listFiles().length;

        File FormulaNewsFolder = new File(ContextGetter.getAppContext().getFilesDir().toString() + "/Formula-News.com");
        if(!FormulaNewsFolder.exists()) FormulaNewsFolder.mkdir();
        numberOfNews[1] = FormulaNewsFolder.listFiles().length;

        File EuronewsFolder = new File(ContextGetter.getAppContext().getFilesDir().toString() + "/Euronews.com");
        if(!EuronewsFolder.exists()) EuronewsFolder.mkdir();
        numberOfNews[2] = EuronewsFolder.listFiles().length;

        File PlanetF1Folder = new File(ContextGetter.getAppContext().getFilesDir().toString() + "/PlanetF1.com");
        if(!PlanetF1Folder.exists()) PlanetF1Folder.mkdir();
        numberOfNews[3] = PlanetF1Folder.listFiles().length;

        File CrashFolder = new File(ContextGetter.getAppContext().getFilesDir().toString() + "/Crash.net");
        if(!CrashFolder.exists()) CrashFolder.mkdir();
        numberOfNews[4] = CrashFolder.listFiles().length;


        sharedPref.registerOnSharedPreferenceChangeListener(listener);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new DrawerAdapter(this,
                R.layout.drawer_list_item, drawerItems, 1));

        ImageView img = (ImageView) findViewById(R.id.imageBar);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new
                            Intent(view.getContext(), SitesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intent);
                }

                if (position == 2) {
                    Intent intent = new
                            Intent(view.getContext(), TableActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intent);


                }

                if (position == 3) {
                    Intent intent = new
                            Intent(view.getContext(), DriverListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intent);


                }


                if (position == 4) {
                    SettingsActivity.activity = SavedSitesList.this;
                    Intent toStart = new Intent(getApplicationContext(), SettingsActivity.class);

                    //toStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(toStart);

                   /* Intent intent = new
                            Intent(view.getContext(), SettingsActivity.class);


                    view.getContext().startActivity(intent);*/
                }
            }

        });

        cardList = new ArrayList<SavedCard>();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.savedSitesNames);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);


        sa = new SavedSiteListAdapter();
        recyclerView.setAdapter(sa);






        if(f1newsRuCheckbox)
            cardList.add(new SavedCard("F1News.ru", Integer.toString(numberOfNews[0]) + " " + getString(R.string.downloaded_articles_text_small), 0, getResources().getDrawable(R.drawable.f1news), getResources().getDrawable(R.drawable.rus1)));
        if(formulaNewsCheckbox)
            cardList.add(new SavedCard("Formula-News.com", Integer.toString(numberOfNews[1]) +" " + getString(R.string.downloaded_articles_text_small), 1, getResources().getDrawable(R.drawable.formulanews), getResources().getDrawable(R.drawable.rus1)));
        if(euronewsCheckbox) {
            if (locale.equals("ru"))
                cardList.add(new SavedCard("Euronews.com",Integer.toString(numberOfNews[2]) +" " + getString(R.string.downloaded_articles_text_small), 2,  getResources().getDrawable(R.drawable.euronews), getResources().getDrawable(R.drawable.rus1)));
            else if (locale.equals("uk"))
                cardList.add(new SavedCard("Euronews.com",Integer.toString(numberOfNews[2]) +" " + getString(R.string.downloaded_articles_text_small), 2,  getResources().getDrawable(R.drawable.euronews), getResources().getDrawable(R.drawable.ukr1)));
            else
                cardList.add(new SavedCard("Euronews.com",Integer.toString(numberOfNews[2]) +" " + getString(R.string.downloaded_articles_text_small), 2,  getResources().getDrawable(R.drawable.euronews), getResources().getDrawable(R.drawable.eng1)));
        }
        if(planetCheckbox)
        cardList.add(new SavedCard("PlanetF1.com", Integer.toString(numberOfNews[3]) +" " + getString(R.string.downloaded_articles_text_small), 3, getResources().getDrawable(R.drawable.planetf1), getResources().getDrawable(R.drawable.eng1)));

        if(crashCheckbox)
            cardList.add(new SavedCard("Crash.net", Integer.toString(numberOfNews[4]) +" " + getString(R.string.downloaded_articles_text_small), 4, getResources().getDrawable(R.drawable.crashlogocut), getResources().getDrawable(R.drawable.eng1)));


        sa.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sites, menu);
        return true;
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
