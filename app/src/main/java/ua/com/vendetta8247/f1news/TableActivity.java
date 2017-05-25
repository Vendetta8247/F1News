package ua.com.vendetta8247.f1news;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TableActivity extends ActionBarActivity {
    TableLayout tl;
    Document doc;
    RelativeLayout rl;

    ListView mDrawerList;
    DrawerLayout mDrawerLayout;




    class AsyncTableLoader extends AsyncTask<Void,Void,Void>
    {
        Context context;
        List<ContentToDisplay> list;
        class ContentToDisplay
        {
            String driverPosition, driver, team, points;
            public ContentToDisplay(String driverPosition, String driver, String team, String points)
            {
                this.driverPosition = driverPosition;
                this.driver = driver;
                this.team = team;
                this.points = points;
            }

            @Override
            public String toString() {
                return new String(driverPosition + " - " + driver + " - " +team + " - " + points);
            }
        }
        AsyncTableLoader(Context context)
        {
            this.context = context;
        }
        String locale;
String outputHtml = "";
        @Override
        protected Void doInBackground(Void... params) {
            try {
                locale = Locale.getDefault().getLanguage();
                if(locale.equals("ru")||locale.equals("uk")) {
                    doc = Jsoup.connect("http://www.eurosport.ru/formula-1/world-championship-1/standing.shtml").get();
                    Element element = doc.getElementById("left-col");
                    Element table = element.getElementsByTag("table").last().getElementsByTag("tbody").first();
                    Elements places = table.getElementsByTag("tr");

                    list = new ArrayList<>();
                    for (Element e : places) {
                        Element place = e.children().get(0);
                        String driver = e.children().get(1).text();
                        Element team = e.children().get(2);
                        Element points = e.children().last();

                        ContentToDisplay item = new ContentToDisplay(place.text(),
                                driver,
                                team.text(),
                                points.text());
                        list.add(item);
                    }
                }
                else
                {
                    doc = Jsoup.connect("http://www.bbc.com/sport/formula1/drivers-world-championship/standings").get();
                    Element element = doc.getElementsByClass("gel-long-primer").first();
                    Elements places = element.getElementsByTag("tr");
                    list = new ArrayList<>();
                    for (Element e : places) {
                        Element place = e.children().get(0);
                        String driver = e.children().get(1).getElementsByTag("abbr").attr("title");
                        Element team = e.children().get(2);
                        Element points = e.children().get(3);

                        ContentToDisplay item = new ContentToDisplay(place.text(), driver, team.text(), points.text());
                        list.add(item);
                    }
                }


            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            for(ContentToDisplay content: list)
            {

                TableRow tr = new TableRow(context);

                TextView first = new TextView(context);
                first.setText(content.driverPosition);
                first.setPadding(dpAsPixels(15), 0, 1, 0);
                //first.setGravity(Gravity.CENTER);
                first.setTextColor(0xff000000);
                first.setBackgroundColor(0xFFFFFFFF);

                TextView second = new TextView(context);
                second.setText(content.driver);
                second.setPadding(0, 0, 1, 0);
                second.setTextColor(0xff000000);
                second.setBackgroundColor(0xFFFFFFFF);

                TextView third = new TextView(context);
                third.setText(content.team);
                third.setPadding(0, 0, 1, 0);
                third.setTextColor(0xff000000);
                third.setBackgroundColor(0xFFFFFFFF);

                TextView fourth = new TextView(context);
                fourth.setText(content.points);
                fourth.setBackgroundColor(0xFFFFFFFF);
                fourth.setTextColor(0xff000000);


                tr.addView(first);
                tr.addView(second);
                tr.addView(third);
                tr.addView(fourth);
                tr.setPadding(dpAsPixels(1),0,dpAsPixels(1),dpAsPixels(1));
                //tr.setBackgroundColor(0xFFFFFFFF);
                tl.addView(tr);

            }

            rl.setVisibility(View.GONE);
            tl.setVisibility(View.VISIBLE);
        }
    }
int dpAsPixels(int dp)
{
    float scale = getResources().getDisplayMetrics().density;
    int dpAsPixels = (int) (dp*scale + 0.5f);
    return dpAsPixels;
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        NavDrawerItem[] drawerItems = {new NavDrawerItem(R.drawable.homeicon, getString(R.string.main_page_text)), new NavDrawerItem(R.drawable.downloadicon, getString(R.string.downloaded_articles_text)), new NavDrawerItem(R.drawable.tableicon, getString(R.string.rating_text)), new NavDrawerItem(R.drawable.racehelmet, getString(R.string.pilots_text)),new NavDrawerItem(R.drawable.settingsicon, getString(R.string.settings_text))};
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new DrawerAdapter(this,
                R.layout.drawer_list_item, drawerItems, 2));

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

                if (position == 1) {
                    Intent intent = new
                            Intent(view.getContext(), SavedSitesList.class);
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
                    SettingsActivity.activity = TableActivity.this;
                    Intent toStart = new Intent(getApplicationContext(), SettingsActivity.class);

                    //toStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(toStart);

                   /* Intent intent = new
                            Intent(view.getContext(), SettingsActivity.class);


                    view.getContext().startActivity(intent);*/
                }
            }

        });



                rl = (RelativeLayout) findViewById(R.id.toDismiss);

        tl = (TableLayout) findViewById(R.id.statistics_table_1);
        tl.setStretchAllColumns(true);

        TableRow tr = new TableRow(this);

        TextView first = new TextView(this);
        first.setText(getString(R.string.place_string));
        first.setPadding(dpAsPixels(15), 0, 1, 0);
        //first.setGravity(Gravity.CENTER);
        first.setTextColor(0xff000000);
        first.setBackgroundColor(0xFFFFFFFF);

        TextView second = new TextView(this);
        second.setText(getString(R.string.driver_string));
        second.setPadding(0, 0, 1, 0);
        second.setTextColor(0xff000000);
        second.setBackgroundColor(0xFFFFFFFF);

        TextView third = new TextView(this);
        third.setText(getString(R.string.team_string));
        third.setPadding(0, 0, 1, 0);
        third.setTextColor(0xff000000);
        third.setBackgroundColor(0xFFFFFFFF);

        TextView fourth = new TextView(this);
        fourth.setText(getString(R.string.points_string));
        fourth.setBackgroundColor(0xFFFFFFFF);
        fourth.setTextColor(0xff000000);


        tr.addView(first);
        tr.addView(second);
        tr.addView(third);
        tr.addView(fourth);
        tr.setPadding(dpAsPixels(1),0,dpAsPixels(1),dpAsPixels(1));
        //tr.setBackgroundColor(0xFFFFFFFF);
        tl.addView(tr);


        //tl.setBackgroundColor(0x000000);
        tl.setVisibility(View.GONE);

        new AsyncTableLoader(this).execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_table, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
