package ua.com.vendetta8247.f1news;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class DriverBioActivity extends ActionBarActivity {
    String articleUrl;
    String activityName;
    WebView wv;

    RelativeLayout rl;




    class AsyncBioLoader extends AsyncTask<Void, Void, Void>
    {
        String url;
        String outerHTML;

        public AsyncBioLoader(String url)
        {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Document doc = Jsoup.connect(url).get();
                Element info = doc.getElementById("mw-content-text");
                Element toc = info.getElementById("toc");
                toc.remove();
                outerHTML = info.html();
            }
            catch (IOException ex)
            {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            wv.loadDataWithBaseURL("","<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +  outerHTML, "text/html", "en_US", outerHTML);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_bio);

        ListView mDrawerList;
        DrawerLayout mDrawerLayout;
        NavDrawerItem[] drawerItems = {new NavDrawerItem(R.drawable.homeicon, getString(R.string.main_page_text)), new NavDrawerItem(R.drawable.downloadicon, getString(R.string.downloaded_articles_text)), new NavDrawerItem(R.drawable.tableicon, getString(R.string.rating_text)), new NavDrawerItem(R.drawable.racehelmet, getString(R.string.pilots_text)),new NavDrawerItem(R.drawable.settingsicon, getString(R.string.settings_text))};



        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new DrawerAdapter(this,
                R.layout.drawer_list_item, drawerItems));

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
                    view.getContext().startActivity(intent);


                }

                if (position == 1) {
                    Intent intent = new
                            Intent(view.getContext(), SavedSitesList.class);
                    view.getContext().startActivity(intent);


                }

                if (position == 2) {
                    Intent intent = new
                            Intent(view.getContext(), TableActivity.class);
                    view.getContext().startActivity(intent);


                }

                if (position == 3) {
                    Intent intent = new
                            Intent(view.getContext(), DriverListActivity.class);
                    view.getContext().startActivity(intent);


                }

                if (position == 4) {
                    SettingsActivity.activity = DriverBioActivity.this;
                    Intent toStart = new Intent(getApplicationContext(), SettingsActivity.class);

                    //toStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(toStart);

                   /* Intent intent = new
                            Intent(view.getContext(), SettingsActivity.class);


                    view.getContext().startActivity(intent);*/
                }


            }
        });


        articleUrl = getIntent().getStringExtra("url");
        activityName = getIntent().getStringExtra("driverName");
        setTitle(activityName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        wv = (WebView) findViewById(R.id.driver_info_webview);
        rl = (RelativeLayout) findViewById(R.id.toDismiss);
        rl.setVisibility(View.VISIBLE);
        wv.setVisibility(View.INVISIBLE);
        wv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        wv.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);


        wv.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    WebView webView = (WebView) v;

                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            if (webView.canGoBack()) {
                                webView.goBack();
                                return true;
                            }
                            break;
                    }
                }

                return false;
            }
        });
        wv.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                wv.setVisibility(View.VISIBLE);
                rl.setVisibility(View.GONE);
            }
        });
        System.out.println(articleUrl);
        new AsyncBioLoader(articleUrl).execute();
        //wv.loadUrl(articleUrl);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_driver_bio, menu);
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
