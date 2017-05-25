package ua.com.vendetta8247.f1news;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
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
import org.jsoup.nodes.Entities;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class NewArticle extends ActionBarActivity {
    WebView wv;
    RelativeLayout rl;
    String articleUrl;
    String articleName;
    String htmlCode;
    String innerHtml = "<head><style>\" +\n" +
            "                        \"@font-face {\" +\n" +
            "                        \"    font-family: 'basker';\" +\n" +
            "                        \"    src: url('file:///android_asset/fonts/OpenSans-Regular.ttf');\" +\n" +
            "                        \"}\" +\n" +
            "                        \"body {font-family: 'basker';}\" +\n" +
            "                        \"</style></head><body>";
    ListView mDrawerList;
    DrawerLayout mDrawerLayout;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_article);



         mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
         mDrawerList = (ListView) findViewById(R.id.left_drawer);
         NavDrawerItem[] drawerItems = {new NavDrawerItem(R.drawable.homeicon, getString(R.string.main_page_text)), new NavDrawerItem(R.drawable.downloadicon, getString(R.string.downloaded_articles_text)), new NavDrawerItem(R.drawable.tableicon, getString(R.string.rating_text)), new NavDrawerItem(R.drawable.racehelmet, getString(R.string.pilots_text)),new NavDrawerItem(R.drawable.settingsicon, getString(R.string.settings_text))};
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
                     SettingsActivity.activity = NewArticle.this;
                     Intent toStart = new Intent(getApplicationContext(), SettingsActivity.class);

                     //toStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                     view.getContext().startActivity(toStart);

                   /* Intent intent = new
                            Intent(view.getContext(), SettingsActivity.class);


                    view.getContext().startActivity(intent);*/
                 }


             }
         });

         articleUrl = getIntent().getStringExtra("adress");
         System.out.println(articleUrl);
         articleName = getIntent().getStringExtra("title");
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        wv = (WebView) findViewById(R.id.webViewArticle);
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


             @Override
             public boolean shouldOverrideUrlLoading(WebView view, String url) {
                 view.getContext().startActivity(
                         new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                 return true;
             }

             public void onPageFinished(WebView view, String url) {
                wv.setVisibility(View.VISIBLE);
                 rl.setVisibility(View.GONE);
             }
         });

         class getInsideArticleF1News extends AsyncTask<Void, Void, Void>
         {
             String tmp = "";

             @Override
             protected Void doInBackground(Void... params) {

                 try {
                     Document doc = Jsoup.connect(articleUrl).get();
                     Document.OutputSettings settings = doc.outputSettings();

                     settings.prettyPrint(true);

                     settings.escapeMode(Entities.EscapeMode.extended);
                     settings.charset("ASCII");



                     if (articleUrl.contains("gallery"))
                     {
                         for(Element element : doc.select("a")) {

                             element.removeAttr("href");
                         }
                         Element el = doc.getElementsByClass("gallery-wrapper").first();
                         innerHtml += el.html();
                         innerHtml += new String(innerHtml + "<br><br> <a href = \"" +  articleUrl +"\"> Посмотреть галерею полностью </a>");


                     }

                     else {
                         for(Element element : doc.select("img")) {

                             if(element.attr("src").startsWith("//"))
                                 element.attr("src", "http://" + element.attr("src").substring(2));
                             element.attr("width", "100%");
                             element.attr("height", "auto");
                             Log.i("", element.attr("src"));
                         }

                         for(Element element : doc.select("iframe"))
                         {
                             if(element.attr("src").contains("youtube")) {
                                 element.replaceWith(new Element(Tag.valueOf("a"), "").attr("href",  element.attr("src")).append("Видео"));
                                 element.html("Видео");
                             }
                         }


                         for(Element link: doc.select("a"))
                         {
                             if (!link.attr("href").toLowerCase().startsWith("http"))    {
                                 link.attr("href", "http://www.f1news.ru" + link.attr("href"));
                             }
                         }

                         Elements elements = doc.getElementsByClass("post_body").first().getElementsByTag("p");
                         tmp += "<h2>" + articleName + "</h2>";
                         for (Element element : elements)

                         {
                             tmp += element.html();
                             tmp += "<br><br>";
                         }
                         innerHtml += tmp;
                         innerHtml +="<a href = \"" + articleUrl + "\">Посмотреть статью на сайте</a> </body>";
                     }

                 } catch (IOException e) {
                     e.printStackTrace();
                 }
                 catch (NullPointerException ex)
                 {
                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             wv.setWebViewClient(new WebViewClient());
                             wv.loadUrl(articleUrl);
                         }
                     });

                 }
                 return null;

             }

             protected void onPostExecute(Void params)
             {

                 wv.loadDataWithBaseURL("file:///android_asset/", "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + innerHtml, "text/html", "en_US",innerHtml);
             }
         }

         class getInsideArticleFormulaNews extends AsyncTask<Void, Void, Void>
         {
             String tmp = "";

             @Override
             protected void onCancelled() {
                 super.onCancelled();


             }
             @Override
             protected Void doInBackground(Void... params) {

                 try {
                     Document doc = Jsoup.connect(articleUrl).get();
                     Document.OutputSettings settings = doc.outputSettings();

                     settings.prettyPrint(true);

                     settings.escapeMode(Entities.EscapeMode.extended);
                     settings.charset("ASCII");

                     tmp += "<h2>" + articleName + "</h2>";

                     tmp += "<br>";
                     for(Element element : doc.select("img")) {

                         if(element.attr("src").startsWith("//"))
                             element.attr("src", "http://" + element.attr("src").substring(2));
                         element.attr("width", "100%");
                         element.attr("height", "auto");
                         Log.i("", element.attr("src"));
                     }

                     Elements collection = doc.select("div.block.article");
                     for(Element el : collection)
                     {
                         Elements paragraphs = el.getElementsByTag("p");
                         for(Element element:paragraphs)
                         {
                             if(!element.className().equals("new-data")) {
                                 Log.i("", element.className());
                                 tmp += element.html();
                                 tmp += "<br/><br/>";
                             }
                         }
                     }




                         for(Element element : doc.select("iframe"))
                         {
                             if(element.attr("src").contains("youtube")) {
                                 element.replaceWith(new Element(Tag.valueOf("a"), "").attr("href",  element.attr("src")).append("Видео"));
                                 element.html("Видео");
                             }
                         }
                     innerHtml += tmp;

                 } catch (IOException e) {
                     e.printStackTrace();
                 }
                 return null;

             }

             protected void onPostExecute(Void params)
             {

                 innerHtml += "</body>";
                 wv.loadDataWithBaseURL("file:///android_asset/", "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + innerHtml, "text/html", "en_US",innerHtml);
             }
         }


         class getInsideArticleEuronews extends AsyncTask<Void, Void, Void>
         {
             String tmp = "";

             @Override
             protected void onCancelled() {
                 super.onCancelled();


             }
             @Override
             protected Void doInBackground(Void... params) {

                 try {
                     Document doc = Jsoup.connect(articleUrl).get();
                     Document.OutputSettings settings = doc.outputSettings();

                     settings.prettyPrint(true);

                     settings.escapeMode(Entities.EscapeMode.extended);
                     settings.charset("ASCII");

                     tmp += "<h2>" + articleName + "</h2>";

                     tmp += "<br>";

                     Element articleText = doc.getElementById("articleTranscript");
                     tmp+=articleText.text();
                     innerHtml += tmp;

                 } catch (IOException e) {
                     e.printStackTrace();
                 }
                 return null;

             }

             protected void onPostExecute(Void params)
             {
                innerHtml +="</body>";
                 wv.loadDataWithBaseURL("file:///android_asset/", "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + innerHtml, "text/html", "en_US",innerHtml);
             }
         }

         class getInsideArticlePlanetF1 extends AsyncTask<Void, Void, Void>
         {
             String tmp = "";

             @Override
             protected void onCancelled() {
                 super.onCancelled();


             }
             @Override
             protected Void doInBackground(Void... params) {

                 try {
                     Document doc = Jsoup.connect(articleUrl).get();
                     Document.OutputSettings settings = doc.outputSettings();

                     settings.prettyPrint(true);

                     settings.escapeMode(Entities.EscapeMode.extended);
                     settings.charset("ASCII");

                     tmp += "<h2>" + articleName + "</h2>";

                     tmp += "<br>";

                     Element articleText = doc.getElementsByClass("article__body").first();
                     tmp+=articleText.html();
                     innerHtml += tmp;

                 } catch (IOException e) {
                     e.printStackTrace();
                 }
                 return null;

             }

             protected void onPostExecute(Void params)
             {
                 innerHtml +="</body>";
                 wv.loadDataWithBaseURL("file:///android_asset/", "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + innerHtml, "text/html", "en_US",innerHtml);
             }
         }


         class getInsideArticleCrash extends AsyncTask<Void, Void, Void>
         {
             String tmp = "";

             @Override
             protected void onCancelled() {
                 super.onCancelled();


             }
             @Override
             protected Void doInBackground(Void... params) {

                 try {
                     Document doc = Jsoup.connect(articleUrl).get();
                     Document.OutputSettings settings = doc.outputSettings();

                     settings.prettyPrint(true);

                     settings.escapeMode(Entities.EscapeMode.extended);
                     settings.charset("ASCII");

                     tmp += "<h2>" + articleName + "</h2>";

                     tmp += "<br>";

                     Element articleText = doc.getElementById("art-text");
                     tmp+=articleText.html();
                     innerHtml += tmp;

                 } catch (IOException e) {
                     e.printStackTrace();
                 }
                 return null;

             }

             protected void onPostExecute(Void params)
             {
                 innerHtml +="</body>";
                 wv.loadDataWithBaseURL("file:///android_asset/", "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + innerHtml, "text/html", "en_US",innerHtml);
             }
         }


         if(articleUrl.contains("f1news.ru"))
         {
             ContextGetter contextGetter = new ContextGetter();
             String filename = contextGetter.getAppContext().getFilesDir() + "/F1News.ru/" + articleName;
             File f = new File(filename);
             if(f.exists())
             {
                 try {
                     FileInputStream fis = new FileInputStream(f);
                     StringBuilder builder = new StringBuilder();
                     BufferedReader in = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
                     int ch;

                     while((ch = in.read()) != -1){
                         builder.append((char)ch);
                     }

                     String HTMLCode = builder.toString();
                     //
                     wv.loadDataWithBaseURL("file:///android_asset/", "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + HTMLCode, "text/html", "en_US", HTMLCode);
                 }catch (IOException ex)
                 {
                 }
             }
             if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB) {
                 new getInsideArticleF1News().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
             }
             else {
                 new getInsideArticleF1News().execute();
             }



         }
         else if(articleUrl.contains("formula-news")) {
                 new getInsideArticleFormulaNews().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
         }
         else if(articleUrl.contains("euronews"))
         {
             new getInsideArticleEuronews().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
         }
         else if(articleUrl.contains("planetf1"))
         {
             new getInsideArticlePlanetF1().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
         }
         else if(articleUrl.contains("crash"))
         {
             new getInsideArticleCrash().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
         }

         setTitle(articleName);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        String filename ="";

        if(articleUrl.contains("formula-news"))
        filename = ContextGetter.getAppContext().getFilesDir() + "/F1News.ru/" + articleName;

        else if(articleUrl.contains("formula-news")) {
            filename = ContextGetter.getAppContext().getFilesDir() + "/Formula-News.com/" + articleName;
        }
        else if(articleUrl.contains("euronews"))
        {
            filename = ContextGetter.getAppContext().getFilesDir() + "/Euronews.com/" + articleName;
        }
        else if(articleUrl.contains("planetf1"))
        {
            filename = ContextGetter.getAppContext().getFilesDir() + "/PlanetF1.com/" + articleName;
        }
        else if(articleUrl.contains("crash"))
        {
            filename = ContextGetter.getAppContext().getFilesDir() + "/Crash.net/" + articleName;
        }

        File f = new File(filename);

        if(f.exists() && !f.isDirectory())
        {
            menu.findItem(R.id.action_save).setEnabled(false);
            menu.findItem(R.id.action_save).setIcon(R.drawable.savebtndis);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_article, menu);




        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if (id==R.id.action_share)
        {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, articleName + "\n" + articleUrl);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Отправить"));
        }
        if (id== R.id.action_save)
        {
            if(articleUrl.contains("f1news.ru")) {
                ArticleParser ap = new ArticleParser(articleName, articleUrl, "f1news", ContextGetter.getAppContext());
                ap.start();
                item.setEnabled(false);
                item.setIcon(R.drawable.savebtndis);
            }
            if (articleUrl.contains("formula-news.com"))
            {
                ArticleParser ap = new ArticleParser(articleName, articleUrl, "formulanews", ContextGetter.getAppContext());
                ap.start();
                item.setEnabled(false);
                item.setIcon(R.drawable.savebtndis);
            }

            if (articleUrl.contains("euronews"))
            {
                ArticleParser ap = new ArticleParser(articleName, articleUrl, "euronews", ContextGetter.getAppContext());
                ap.start();
                item.setEnabled(false);
                item.setIcon(R.drawable.savebtndis);
            }
            if (articleUrl.contains("planetf1"))
            {
                ArticleParser ap = new ArticleParser(articleName, articleUrl, "planetf1", ContextGetter.getAppContext());
                ap.start();
                item.setEnabled(false);
                item.setIcon(R.drawable.savebtndis);
            }
            if (articleUrl.contains("crash"))
            {
                ArticleParser ap = new ArticleParser(articleName, articleUrl, "crash", ContextGetter.getAppContext());
                ap.start();
                item.setEnabled(false);
                item.setIcon(R.drawable.savebtndis);
            }

        }

        return super.onOptionsItemSelected(item);
    }
}
