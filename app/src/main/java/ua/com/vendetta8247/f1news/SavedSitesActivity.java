package ua.com.vendetta8247.f1news;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class SavedSitesActivity extends ActionBarActivity {
SavedSitesAdapter adapter;
    static String whereAmI;


    ListView mDrawerList;
    DrawerLayout mDrawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_sites);

        NavDrawerItem[] drawerItems = {new NavDrawerItem(R.drawable.homeicon, "Главная"), new NavDrawerItem(R.drawable.downloadicon, "Скачанные статьи"), new NavDrawerItem(R.drawable.tableicon, "Рейтинг"), new NavDrawerItem(R.drawable.racehelmet, "Пилоты"),new NavDrawerItem(R.drawable.settingsicon, "Настройки")};
        whereAmI = getIntent().getStringExtra("site");
        getSupportActionBar().setTitle(whereAmI);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                    SettingsActivity.activity = SavedSitesActivity.this;
                    Intent toStart = new Intent(getApplicationContext(), SettingsActivity.class);

                    //toStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(toStart);

                   /* Intent intent = new
                            Intent(view.getContext(), SettingsActivity.class);


                    view.getContext().startActivity(intent);*/
                }
            }

        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.savedSitesList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        adapter = new SavedSitesAdapter();
        recyclerView.setAdapter(adapter);


        File folder = new File(ContextGetter.getAppContext().getFilesDir().toString());
        List<SavedArticle> savedArticles = new ArrayList<>();

        listFilesForFolder(folder, savedArticles);

        Collections.sort(savedArticles);
        //Collections.reverse(savedArticles);
        for(SavedArticle a : savedArticles)
        {
            Date myDate = a.date;
            Calendar cal = Calendar.getInstance();
            cal.setTime(myDate);

            if(cal.get(Calendar.MONTH) + 1 < 10)
            adapter.changeList(new NewCard(a.file.getName(),a.siteName,new String (Integer.toString(cal.get(Calendar.DATE)) +".0"+ Integer.toString(cal.get(Calendar.MONTH)+1)),null));
            else
            {
                adapter.changeList(new NewCard(a.file.getName(),a.siteName,new String (Integer.toString(cal.get(Calendar.DATE)) +"."+ Integer.toString(cal.get(Calendar.MONTH)+1)),null));
            }
        }
    }

    public static void listFilesForFolder(final File folder, List<SavedArticle> list) {
        File f = new File("");
        System.out.println(whereAmI);
        f = new File(folder + "/" + whereAmI);
        System.out.println("File name is: " + f);
        if(!f.exists())f.mkdir();




        for (final File fileEntry : f.listFiles()) {
            String folderName = f.getName();
            if (fileEntry.isDirectory()) {
                folderName = f.getName();
                listFilesForFolder(fileEntry, list);
            } else {
                SavedArticle article = new SavedArticle(fileEntry, folderName, new Date(fileEntry.lastModified()));
                list.add(article);
            }
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, SavedSitesList.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_saved_sites, menu);
        return true;
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
// TODO: 29.06.2015  Change the folder name for each site

        if(id == R.id.action_delete)
        {
            openDialog();
        }

        return super.onOptionsItemSelected(item);
    }
    public void openDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_box);
        dialog.setTitle("Подтверждение");
        dialog.show();

        TextView okButton = (TextView) dialog.findViewById(R.id.okButton);

        okButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        TextView view = (TextView) v;
                        view.setBackgroundColor(0xFFEAEAEA);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.setBackgroundColor(0xCCCCCC);
                        File folder = new File(ContextGetter.getAppContext().getFilesDir().toString() + "/" + whereAmI + "/");

                        String[] fileList;
                        fileList = folder.list();
                        for (String f : fileList) {
                            File file = new File(ContextGetter.getAppContext().getFilesDir().toString() + "/" + whereAmI + "/" + f);
                            if (!file.isDirectory() && file.exists())
                                file.delete();
                        }
                        adapter.eraseList();
                        dialog.hide();
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        TextView view = (TextView) v;
                        view.setBackgroundColor(0xFFFFFFFF);
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        });



        TextView cancelButton = (TextView) dialog.findViewById(R.id.cancelButton);

        cancelButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        TextView view = (TextView) v;
                        view.setBackgroundColor(0xFFEAEAEA);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        dialog.hide();
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        TextView view = (TextView) v;
                        view.setBackgroundColor(0xFFFFFFFF);
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        });
    }
}
