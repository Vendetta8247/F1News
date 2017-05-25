package ua.com.vendetta8247.f1news;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DriverListActivity extends ActionBarActivity {
    String locale = "";
    Document doc;
    DriverListAdapter adapter;
    RelativeLayout rl;
    RecyclerView recyclerView;

    ListView mDrawerList;
    DrawerLayout mDrawerLayout;



    class AsyncDriverListLoader extends AsyncTask<Void,Void,Void>
    {
        List<DriverCard> list = new ArrayList<>();
        String url = "";

        @Override
        protected Void doInBackground(Void... params) {
            if(locale.equals("ru") || locale.equals("uk"))
            {
                url = "http://ru.motorsport.com/f1/drivers?y=" + Calendar.getInstance().get(Calendar.YEAR);
            }

            else url = "http://motorsport.com/f1/drivers?y=" + Calendar.getInstance().get(Calendar.YEAR);

            try {
                doc = Jsoup.connect(url).get();
                Element element = doc.getElementById("drivers_list");
                element.children().get(0).remove();
                element.children().get(0).remove();

                Element el = element.getElementsByClass("flexGrid").first();
                Elements elements = el.getElementsByClass("item");
                System.out.println("Elements size == " + elements.size());

                for(Element element1 : elements)
                {
                    String mainImageURL = element1.getElementsByTag("img").first().attr("src");
                    String fullBioLink = "";

                    if(locale.equals("ru") || locale.equals("uk"))
                    {
                        fullBioLink = "http://ru.motorsport.com";
                    }

                    else fullBioLink = "http://motorsport.com";

                    fullBioLink = new String( fullBioLink +  element1.getElementsByTag("a").first().attr("href") + "/bio");

                    String name = element1.getElementsByTag("h3").text();
                    String flagURL = element1.getElementsByClass("cflag").first().getElementsByTag("img").first().attr("src");

                    element1.getElementsByClass("label").remove();
                    String team = element1.getElementsByClass("ainfo").get(0).text();

                    String age = element1.getElementsByClass("ainfo").get(1).text();
                    char[] cs = age.toCharArray();
                    age = new String(Character.toString(cs[cs.length-3]) + cs[cs.length-2] );


                    String nationality = element1.getElementsByClass("ainfo").get(2).text();


                    File driverFolder = new File(ContextGetter.getAppContext().getFilesDir().toString() + "/Drivers");
                    if(!driverFolder.exists()) driverFolder.mkdir();
//
//                    File driverPic = new File(ContextGetter.getAppContext().getFilesDir().toString() + "/Drivers/" + name + ".png");
//                    Bitmap bmp = null;
//
//
//
//                    if(!driverPic.exists()) {
//                        System.out.println("FILE" + driverPic + " DOESN'T EXIST");
//                        URL url = new URL(mainImageURL);
//
//                        bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                        OutputStream out = null;
//                        try {
//                            out = new BufferedOutputStream(new FileOutputStream(driverPic));
//                            bmp.compress(Bitmap.CompressFormat.PNG, 50, out);
//                        } finally {
//                            if (out != null) {
//                                out.close();
//                            }
//                        }
//                    }
//                    else
//                    {
//                        System.out.println("FILE" + driverPic + " EXISTS");
//                        bmp = BitmapFactory.decodeFile(ContextGetter.getAppContext().getFilesDir().toString() + "/Drivers/" + name + ".png");
//                    }
//
//                    Drawable drawable = new BitmapDrawable(getResources(),bmp);
//


                    File driverFlagPic = new File(ContextGetter.getAppContext().getFilesDir().toString() + "/Drivers/" + name + " flag.png");

                    Bitmap bmp1 = null;



                    if(!driverFlagPic.exists()) {
                        System.out.println("FILE" + driverFlagPic + " DOESN'T EXIST");
                        URL url = new URL(flagURL);

                        bmp1 = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        OutputStream out = null;
                        try {
                            out = new BufferedOutputStream(new FileOutputStream(driverFlagPic));
                            bmp1.compress(Bitmap.CompressFormat.PNG, 100, out);
                        } finally {
                            if (out != null) {
                                out.close();
                            }
                        }
                    }
                    else
                    {
                        System.out.println("FILE" + driverFlagPic + " EXISTS");
                        bmp1 = BitmapFactory.decodeFile(ContextGetter.getAppContext().getFilesDir().toString() + "/Drivers/" + name + " flag.png");
                    }






                    Drawable drawable1 = new BitmapDrawable(getResources(),bmp1);


                    list.add(new DriverCard(name, team, age, nationality, drawable1, fullBioLink));

                }





                //System.out.println(element.html());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //adapter.addCard(new DriverCard("Niko Rosberg", "What is your name?", "Nice to meet you", "Thank you", null));
            for(DriverCard card : list)
            {
                adapter.addCard(card);
            }
            adapter.notifyDataSetChanged();
            rl.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_list);

        NavDrawerItem[] drawerItems = {new NavDrawerItem(R.drawable.homeicon, getString(R.string.main_page_text)), new NavDrawerItem(R.drawable.downloadicon, getString(R.string.downloaded_articles_text)), new NavDrawerItem(R.drawable.tableicon, getString(R.string.rating_text)), new NavDrawerItem(R.drawable.racehelmet, getString(R.string.pilots_text)),new NavDrawerItem(R.drawable.settingsicon, getString(R.string.settings_text))};

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new DrawerAdapter(this,
                R.layout.drawer_list_item, drawerItems, 3));

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

                if (position == 2) {
                    Intent intent = new
                            Intent(view.getContext(), TableActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    view.getContext().startActivity(intent);


                }



                if (position == 4) {
                    SettingsActivity.activity = DriverListActivity.this;
                    Intent toStart = new Intent(getApplicationContext(), SettingsActivity.class);

                    //toStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(toStart);

                   /* Intent intent = new
                            Intent(view.getContext(), SettingsActivity.class);


                    view.getContext().startActivity(intent);*/
                }


            }
        });



        locale = Locale.getDefault().getLanguage();
        System.out.println(locale);

        rl = (RelativeLayout) findViewById(R.id.toDismiss);

        recyclerView = (RecyclerView) findViewById(R.id.driverlist);
        recyclerView.setVisibility(View.GONE);
        rl.setVisibility(View.VISIBLE);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);


        adapter = new DriverListAdapter();
        recyclerView.setAdapter(adapter);
        new AsyncDriverListLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_driver_list, menu);
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
