package ua.com.vendetta8247.f1news;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
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


public class NewsPage extends ActionBarActivity {
    int totalArticles = 0;
    RecyclerView recList;
    NewsAdapter na;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String whereAmI;
    ImageParser1 ip1 = new ImageParser1();
    ImageParser2 ip2 = new ImageParser2();
    ImageParser4 ip4 = new ImageParser4("http://planetf1.com/news/", 1, 0);

    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ContextGetter.getAppContext());
    SharedPreferences.OnSharedPreferenceChangeListener listener;

    boolean syncConnPref = sharedPref.getBoolean("pictures_checkbox", true);



    ListView mDrawerList;
    DrawerLayout mDrawerLayout;

    class NewsParser1 extends AsyncTask<Void,Void,Void> {
        Document doc;
String articleURL = "";
        @Override
        protected void onPreExecute()
        {
            setTitle("F1News.ru");
            mSwipeRefreshLayout.setEnabled(false);

        };

        @Override
        protected Void doInBackground(Void... params) {

            Calendar c = Calendar.getInstance();
            int currentMonth = c.get(c.MONTH) + 1;

            try {
                doc = Jsoup.connect("http://www.f1news.ru/news/" + c.get(c.YEAR) + "/" + currentMonth).timeout(30000).get();
                Element e = doc.getElementsByClass("widget_body").first();
                Elements es = e.getElementsByTag("li");

                String date = "";

cycle:
                for (Element el : es) {
                    synchronized (this) {
                        try {
                            wait(1);
                        } catch (InterruptedException ex) {

                        }
                    }


                    if (el.hasClass("list_head")) {

                        date = el.text();
                        date = DateHelper.toStringDate(date);

                    } else
                    {

                        Element forHeader = el.getElementsByTag("a").first();

                        String newsDate = date;
                        Date currentDate = new Date();

                        DateFormat format = new SimpleDateFormat("dd.MM");
                        Date articleDate = null;
                        try {
                            articleDate = format.parse(newsDate);
                            articleDate.setYear(116);
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }


                        long diff = currentDate.getTime() - articleDate.getTime();
                        long diffDays = TimeUnit.MILLISECONDS.toDays(diff); //diff / (24 * 60*1000) % 24;
                        if(sharedPref.getString("listPref","1").contains("1"))
                        {
                            if (diffDays>0) break cycle;
                        }
                        if(sharedPref.getString("listPref","1").contains("2"))
                        {
                            if (diffDays>1) break cycle;
                        }
                        if(sharedPref.getString("listPref","1").contains("3"))
                        {
                            if (diffDays>6) break cycle;
                        }



                    if (forHeader.attr("href").contains("http")) {
                        na.changeList(new NewCard(forHeader.text(), "F1News.ru", newsDate, forHeader.attr("href")));
                        articleURL = forHeader.attr("href");
                    } else {
                        na.changeList(new NewCard(forHeader.text(), "F1News.ru", newsDate, "http://www.f1news.ru" + forHeader.attr("href")));
                        articleURL = "http://www.f1news.ru" + forHeader.attr("href");
                    }
                    runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          na.notifyItemInserted(na.getItemCount());
                                      }
                                  }
                    );

                }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void params) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);
            if(syncConnPref) {
                if (ip1.isCancelled()) {

                    ip1 = new ImageParser1();
                }

                ip1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            //na.notifyDataSetChanged();
        }
    }
    int i = 0;


    class ImageParser1 extends AsyncTask<Void,Void,Void>
    {
        Document doc;
        int pos = 0;
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(c.MONTH) + 1;



        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                doc = Jsoup.connect("http://www.f1news.ru/news/" + c.get(c.YEAR) + "/" + currentMonth).timeout(30000).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Element e = doc.getElementsByClass("widget_body").first();
            Elements es = e.getElementsByTag("li");
            for (Element el : es) {
                if(!isCancelled()) {
                    Document tmp = null;
                    if (!el.hasClass("list_head")) {
                        try {
                            if(!el.getElementsByTag("a").first().attr("href").contains("http://"))
                            tmp = Jsoup.connect("http://www.f1news.ru" + el.getElementsByTag("a").first().attr("href")).timeout(15000).get();
                            else
                                tmp = Jsoup.connect(el.getElementsByTag("a").first().attr("href")).timeout(15000).get();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                        try {
                            Element element = tmp.getElementsByClass("post_thumbnail").first();
                            Element imagge = element.getElementsByTag("img").first();
                            if (imagge.attr("src").contains("http"))
                                na.changePic(pos, imagge.attr("src"));
                            else
                                na.changePic(pos, "http:" + imagge.attr("src"));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    na.notifyDataSetChanged();
                                }
                            });
                        } catch (NullPointerException ex) {

                        }
                        catch (IndexOutOfBoundsException ex)
                        {
                            break;
                        }
                        pos++;
                    }

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
           // na.notifyDataSetChanged();
        }
    }


    class NewsParser2 extends AsyncTask<Void,Void,Void> {
        Document doc;

        @Override
        protected void onPreExecute()
        {
            setTitle("Formula-News.com");
            mSwipeRefreshLayout.setEnabled(false);

        };

        @Override
        protected Void doInBackground(Void... params) {
//            mSwipeRefreshLayout.setEnabled(false);
            Calendar c = Calendar.getInstance();
            int currentMonth = c.get(c.MONTH) + 1;
            Date currentDate = new Date();


            try {
                doc = Jsoup.connect("http://formula-news.com/news/category/v=formula&m=" + currentMonth+ "&y=" + c.get(c.YEAR)).timeout(60000).get();
                Element e = doc.getElementsByClass("news").first();

                try{

                Elements es = e.getElementsByTag("li");

                cycle: for (Element el : es) {
                    synchronized (this) {
                        try {
                            wait(5);
                        } catch (InterruptedException ex) {

                        }
                    }if(!isCancelled()) {







                        Element tempEls = el.getElementsByClass("news-data").first();
                        Element forHeader = el.getElementsByTag("a").last();
                        String newsDate = tempEls.html();
                        newsDate = newsDate.replaceAll("/", ".");


                        DateFormat format = new SimpleDateFormat("dd.MM");
                        Date articleDate = null;
                        try {
                            articleDate = format.parse(newsDate);
                            articleDate.setYear(116);
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }


                        long diff = currentDate.getTime() - articleDate.getTime();
                        long diffDays = TimeUnit.MILLISECONDS.toDays(diff); //diff / (24 * 60*1000) % 24;
                        if(sharedPref.getString("listPref","1").contains("1"))
                        {
                            if (diffDays>0) break cycle;
                        }
                        if(sharedPref.getString("listPref","1").contains("2"))
                        {
                            if (diffDays>1) break cycle;
                        }
                        if(sharedPref.getString("listPref","1").contains("3"))
                        {
                            if (diffDays>6) break cycle;
                        }



                        //String URLforImg = el.getElementsByTag("img").first().attr("src");
                        na.changeList(new NewCard(forHeader.html(), "Formula-news.com", newsDate, forHeader.attr("href")));
                        runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              na.notifyItemInserted(na.getItemCount());

                                          }
                                      }

                        );

                    }
                }

                }catch (NullPointerException ex)
                {
                    ex.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void params) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);

            if (syncConnPref) {
                if (ip2.isCancelled()) {

                    ip2 = new ImageParser2();
                }

                ip2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                //na.notifyDataSetChanged();
            }
        }
    }


    class ImageParser2 extends AsyncTask<Void,Void,Void>
    {
        Document doc;
        int pos = 0;


        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Calendar c = Calendar.getInstance();
            int currentMonth = c.get(c.MONTH) + 1;

            try {
                doc = Jsoup.connect("http://formula-news.com/news/category/v=formula&m=" + currentMonth+ "&y=" + c.get(c.YEAR)).timeout(30000).get();
                System.out.println("Connected");
                Element e = doc.getElementsByClass("news").first();

                try {

                    Elements es = e.getElementsByTag("li");

                    for (Element el : es) {
                        if (!isCancelled()) {
                            try {
                                Document tmp = Jsoup.connect(el.getElementsByTag("a").last().attr("href")).timeout(30000).get();
                                Element article = tmp.select("div.block.article").first();
                                article.getElementsByTag("h1").remove();

                                Element imagge = article.getElementsByTag("img").first();

                                if (imagge.attr("src").contains("http"))
                                    na.changePic(pos, imagge.attr("src"));
                                else
                                    na.changePic(pos, "http:" + imagge.attr("src"));
                                pos++;

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        na.notifyDataSetChanged();
                                    }
                                });
                            }catch (IndexOutOfBoundsException ex)
                            {
                                break;
                            }
                        }


                    }
                }
                catch (NullPointerException ex){
                    ex.printStackTrace();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // na.notifyDataSetChanged();
        }
    }

    class NewsParser3 extends AsyncTask<Void,Void,Void> {
        Document doc;
        String locale = "";
        @Override
        protected void onPreExecute()
        {
            setTitle("Euronews.com");
            mSwipeRefreshLayout.setEnabled(false);
            locale = Locale.getDefault().getLanguage();

        };

        @Override
        protected Void doInBackground(Void... params) {



            try {
                String prefix = "";

                if (locale.equals("ru")) {
                    prefix = "ru.";
                }
                else if(locale.equals("uk"))
                {

                    prefix = "ua.";
                }

                    doc = Jsoup.connect("http://" + prefix + "euronews.com/sport/formula-1/").timeout(30000).get();


                Element e = doc.getElementsByClass("topStoryWrapper").first();
                Element forHeader = e.getElementsByTag("h2").first();
                //System.out.println(forHeader.toString());
                String newsDate = e.getElementsByClass("artDate").first().text();

                DateFormat format1 = new SimpleDateFormat("dd/MM");
                Date articleDate1 = null;



                try {
                    articleDate1 = format1.parse(newsDate);
                    articleDate1.setYear(116);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }

                newsDate = format1.format(articleDate1);
                newsDate = newsDate.replaceAll("/",".");

                na.changeList(new NewCard(forHeader.text(),"Euronews.com",newsDate,"http://" + prefix + "euronews.com" + forHeader.getElementsByTag("a").first().attr("href")));

                Elements es = doc.getElementsByClass("subcategoryList");
                System.out.println("Elements size: " + es.size());


                cycle:
                for (Element el : es) {
                    synchronized (this) {
                        try {
                            wait(1);
                        } catch (InterruptedException ex) {

                        }
                    }


                    Elements elements = el.getElementsByTag("li");
                    System.out.println("Elements size: " + elements.size());
                    for (Element element : elements) {


                        forHeader = element.getElementsByTag("a").first();
                        String articleName = element.getElementsByTag("a")
                                .first()
                                .attr("title");

                        newsDate = element.getElementsByClass("artDate").first().text();
                       // System.out.println("Date length :" + newsDate.length() + " Date look : " + newsDate);

                        DateFormat format = new SimpleDateFormat("dd/MM kk:mm");
                        Date articleDate = null;



                        try {
                            articleDate = format.parse(newsDate);
                            articleDate.setYear(116);
                        } catch (ParseException ex) {

                            format = new SimpleDateFormat("dd/MM -");
                            try{

                            articleDate = format.parse(newsDate);
                                articleDate.setYear(116);
                            }catch (ParseException exc){}
                        }

                        DateFormat toOutput = new SimpleDateFormat("dd.MM");
                        String outputDate = "";
                        try {
                            outputDate = toOutput.format(articleDate);
                        }catch (NullPointerException ex)
                        {

                        }
                        na.changeList(new NewCard(articleName, "Euronews.com", outputDate, "http://" + prefix + "euronews.com" + forHeader.attr("href")));


                        runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              na.notifyItemInserted(na.getItemCount());
                                          }
                                      }
                        );
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void params) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);
           /* if(syncConnPref) {
                if (ip1.isCancelled()) {

                    ip1 = new ImageParser1();
                }

                ip1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }*/
            na.notifyDataSetChanged();
        }
    }


   class NewsParser5 extends AsyncTask<Void,Void,Void> {
        Document doc;
        String locale = "";
       String link = "";
       int currentPage = 0;
       int readArticles = 0;
       boolean toContinue = true;
       Date currentDate = new Date();

       public NewsParser5(String link, int currentPage)
       {
           this.link = link;
           this.currentPage = currentPage;
       }
        @Override
        protected void onPreExecute()
        {
            setTitle("Crash.net");
            mSwipeRefreshLayout.setEnabled(false);
            locale = Locale.getDefault().getLanguage();

        };
        @Override
        protected Void doInBackground(Void... params) {
            try {

                doc = Jsoup.connect(link).timeout(30000).get();

                Element e = doc.getElementsByClass("shedule-list").first();
                Elements elements = e.getElementsByTag("li");
                cycle:
                for(Element element: elements)
                {
                Element tmp = element.getElementsByTag("span").first();

                    Element forHeader = tmp.getElementsByTag("span").last();


                String newsDate = element.getElementsByTag("b").last().text();

                newsDate = replaceDate(newsDate);

                DateFormat format1 = new SimpleDateFormat("dd MM yyyy");
                    System.out.println("NEWSDATE" + newsDate);
                Date articleDate = null;



                try {
                    //System.out.println("News Date = " + newsDate);

                    articleDate = format1.parse(newsDate);

                    articleDate.setYear(116);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }

                    format1 = new SimpleDateFormat("dd MM");

                newsDate = format1.format(articleDate);
                newsDate = newsDate.replaceAll(" ", ".");


                    long diff = currentDate.getTime() - articleDate.getTime();
                    long diffDays = TimeUnit.MILLISECONDS.toDays(diff); //diff / (24 * 60*1000) % 24;
                    if(sharedPref.getString("listPref","1").contains("1"))
                    {
                        if (diffDays>0)
                        {
                            toContinue = false;
                            break cycle;
                        }
                    }
                    if(sharedPref.getString("listPref","1").contains("2"))
                    {
                        if (diffDays>1)
                        {
                            toContinue = false;
                            break cycle;
                        }
                    }
                    if(sharedPref.getString("listPref", "1").contains("3"))
                    {
                        if (diffDays>6)
                        {
                            toContinue = false;
                            break cycle;
                        }
                    }
                    if(sharedPref.getString("listPref", "1").contains("4"))
                    {
                        if (diffDays>30)
                        {
                            toContinue = false;
                            break cycle;
                        }
                    }
                    System.out.println("LINK = " + element.getElementsByTag("a").first().attr("href"));
                    String articleURL = "";
                    if(element.getElementsByTag("a").first().attr("href").contains("crash.net"))
                        articleURL = element.getElementsByTag("a").first().attr("href");
                    else
                        articleURL = "http://www.crash.net/" + element.getElementsByTag("a").first().attr("href");
                na.changeList(new NewCard(forHeader.text(),"Crash.net",newsDate, articleURL));
                readArticles++;

                        runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              na.notifyItemInserted(readArticles);
                                          }
                                      }
                        );
                    }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void params) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);
            if(syncConnPref) {
                //if (ip4.isCancelled()) {

                    //ip5 = new ImageParser5("http://planetf1.com/news/page/" + currentPage, currentPage, 10*(currentPage-1));
               // }

                //ip5.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            if(toContinue)
            new NewsParser5("http://www.crash.net/f1/news_archive/" + (currentPage+1)  + "/content.html" , currentPage+1).execute();

            na.notifyDataSetChanged();
        }
    }



    class ImageParser4 extends AsyncTask<Void,Void,Void>
    {


        Document doc;
        int pos = 0;
        String link = "";
        int currentPage = 0;

        public ImageParser4(String link, int currentPage, int pos)
        {
            this.link = link;
            this.currentPage = currentPage;
            this.pos = pos;
        }


        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                doc = Jsoup.connect(link).timeout(30000).get();


                System.out.println("Connected");
                //Element e = doc.getElementsByClass("news").first();

                try {

                    Element e = doc.getElementsByClass("articleList__list").first();
                    Elements elements = e.getElementsByClass("articleList__item");
                    for(Element element: elements)
                    {
                        Element imageLink = element.getElementsByTag("img").first();
                        if (!isCancelled()) {
                            try {

                                    na.changePic(pos, imageLink.attr("src"));
                                pos++;

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        na.notifyDataSetChanged();
                                    }
                                });
                            }catch (IndexOutOfBoundsException ex)
                            {
                                break;
                            }
                        }


                    }
                }
                catch (NullPointerException ex){
                    ex.printStackTrace();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // na.notifyDataSetChanged();
        }
    }


    class NewsParser4 extends AsyncTask<Void,Void,Void> {
        Document doc;
        String locale = "";
        String link = "";
        int currentPage = 0;
        int readArticles = 0;
        boolean toContinue = true;
        Date currentDate = new Date();

        public NewsParser4(String link, int currentPage)
        {
            this.link = link;
            this.currentPage = currentPage;
        }
        @Override
        protected void onPreExecute()
        {
            setTitle("PlanetF1");
            mSwipeRefreshLayout.setEnabled(false);
            locale = Locale.getDefault().getLanguage();

        };
        @Override
        protected Void doInBackground(Void... params) {
            try {

                doc = Jsoup.connect(link).timeout(30000).get();

                Element e = doc.getElementsByClass("articleList__list").first();
                Elements elements = e.getElementsByClass("articleList__item");
                cycle:
                for(Element element: elements)
                {
                    Element forHeader = element.getElementsByTag("h3").first();




                    //System.out.println(forHeader.toString());
                    String newsDate = element.getElementsByTag("time").first().text();

                    newsDate = replaceDate(newsDate);

                    DateFormat format1 = new SimpleDateFormat("MM dd");
                    Date articleDate = null;



                    try {
                        //System.out.println("News Date = " + newsDate);

                        articleDate = format1.parse(newsDate);

                        articleDate.setYear(116);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }


                    format1 = new SimpleDateFormat("dd MM");
                    newsDate = format1.format(articleDate);
                    newsDate = newsDate.replaceAll(" ", ".");


                    long diff = currentDate.getTime() - articleDate.getTime();
                    long diffDays = TimeUnit.MILLISECONDS.toDays(diff); //diff / (24 * 60*1000) % 24;
                    if(sharedPref.getString("listPref","1").contains("1"))
                    {
                        if (diffDays>0)
                        {
                            toContinue = false;
                            break cycle;
                        }
                    }
                    if(sharedPref.getString("listPref","1").contains("2"))
                    {
                        if (diffDays>1)
                        {
                            toContinue = false;
                            break cycle;
                        }
                    }
                    if(sharedPref.getString("listPref","1").contains("3"))
                    {
                        if (diffDays>6)
                        {
                            toContinue = false;
                            break cycle;
                        }
                    }
                    if(sharedPref.getString("listPref","1").contains("4"))
                    {
                        if (diffDays>30)
                        {
                            toContinue = false;
                            break cycle;
                        }
                    }
                    System.out.println("LINK = " + element.getElementsByTag("a").first().attr("href"));

                    na.changeList(new NewCard(forHeader.text(),"PlanetF1.com",newsDate, element.getElementsByTag("a").first().attr("href")));
                    readArticles++;

                    runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          na.notifyItemInserted(readArticles);
                                      }
                                  }
                    );
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void params) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);
            if(syncConnPref) {
                //if (ip4.isCancelled()) {

                ip4 = new ImageParser4("http://planetf1.com/news/page/" + currentPage, currentPage, 10*(currentPage-1));
                // }

                ip4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            if(toContinue)
                new NewsParser4("http://planetf1.com/news/page/" + (currentPage+1), currentPage+1).execute();

            na.notifyDataSetChanged();
        }
    }




    void executeParsers()
    {
        if(whereAmI.equals("F1News.ru"))
        new NewsParser1().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else if(whereAmI.equals("Formula-News.com")) {
            System.out.println("WE ARE AT FORMULA NEWS");
            new NewsParser2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else if(whereAmI.equals("Euronews.com"))
        new NewsParser3().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else if (whereAmI.equals("PlanetF1.com"))
        {
            new NewsParser4("http://planetf1.com/news/", 1).execute();
        }
        else if (whereAmI.equals("Crash.net"))
        {
            System.out.println("WE ARE AT CRASH");
            new NewsParser5("http://www.crash.net/f1/news_archive/1/content.html", 1).execute();
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, SitesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        ip1.cancel(true);
        ip2.cancel(true);
        ip4.cancel(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ip1.cancel(true);
        ip2.cancel(true);
        ip4.cancel(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_page);

        NavDrawerItem[] drawerItems = {new NavDrawerItem(R.drawable.homeicon, getString(R.string.main_page_text)), new NavDrawerItem(R.drawable.downloadicon, getString(R.string.downloaded_articles_text)), new NavDrawerItem(R.drawable.tableicon, getString(R.string.rating_text)), new NavDrawerItem(R.drawable.racehelmet, getString(R.string.pilots_text)),new NavDrawerItem(R.drawable.settingsicon, getString(R.string.settings_text))};
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                synchronized (this) {
                    try {
                        wait(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                recreate();
            }
        };

        sharedPref.registerOnSharedPreferenceChangeListener(listener);

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
                    SettingsActivity.activity = NewsPage.this;
                    Intent toStart = new Intent(getApplicationContext(), SettingsActivity.class);

                    //toStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(toStart);

                   /* Intent intent = new
                            Intent(view.getContext(), SettingsActivity.class);


                    view.getContext().startActivity(intent);*/
                }


            }
        });


        whereAmI = getIntent().getStringExtra("site");
        System.out.println(whereAmI);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ip1.cancel(true);
                ip2.cancel(true);
                ip4.cancel(true);
                na.eraseList();
                executeParsers();


            }
        });
        mSwipeRefreshLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        24,
                        getResources().getDisplayMetrics()));
        mSwipeRefreshLayout.setRefreshing(true);

        mSwipeRefreshLayout.setColorScheme(R.color.color1,R.color.color2);

        recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);

        DefaultItemAnimator dia = new DefaultItemAnimator();
        dia.setAddDuration(400);
        dia.setRemoveDuration(400);
        recList.setItemAnimator(dia);
         na = new NewsAdapter();
         recList.setAdapter(na);
        executeParsers();



        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

    }
    public String replaceDate(String str)
    {
        String temp = new String(str);
        if(temp.contains("January"))
            temp = temp.replace("January", "01");
        else if(temp.contains("February"))
            temp = temp.replace("February", "02");
        else if(temp.contains("March"))
            temp = temp.replace("March", "03");
        else if(temp.contains("April"))
            temp = temp.replace("April", "04");
        else if(temp.contains("May"))
            temp = temp.replace("May", "05");
        else if(temp.contains("June"))
            temp = temp.replace("June", "06");
        else if(temp.contains("July"))
            temp = temp.replace("July", "07");
        else if(temp.contains("August"))
            temp = temp.replace("August", "08");
        else if(temp.contains("September"))
            temp = temp.replace("September", "09");
        else if(temp.contains("October"))
            temp = temp.replace("October", "10");
        else if(temp.contains("November"))
            temp = temp.replace("November", "11");
        else if(temp.contains("December"))
            temp = temp.replace("December", "12");

        return temp;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news_page, menu);
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
        if (id == R.id.action_save_all) {
            String choices[] = new String[] {getString(R.string.today_text), getString(R.string.week_text), getString(R.string.all_articles_text)};
            System.out.println("WHERE AM I = " + whereAmI + " LENGTH " + whereAmI.length());

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.save_articles_period));
            builder.setItems(choices, new DialogInterface.OnClickListener() {
                @Override
                //TODO Не работает скачка понедельно и по дням
                public void onClick(DialogInterface dialog, int which) {

                    if (which == 0) {
                        List<NewCard> list = na.readList();
                        Date currentDate = new Date();
                        cycle:
                        for (NewCard c : list) {
                            DateFormat format = new SimpleDateFormat("dd.MM");
                            Date articleDate = null;
                            totalArticles++;
                            try {
                                articleDate = format.parse(c.dateString);
                                articleDate.setYear(116);
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }


                            long diff = currentDate.getTime() - articleDate.getTime();
                            long diffDays = TimeUnit.MILLISECONDS.toDays(diff);
                            if (diffDays == 0) {

                                ArticleParser ap;
                                if(whereAmI.equals("F1News.ru")) {
                                    ap = new ArticleParser(c, "f1news");
                                    ap.start();
                                }
                                if(whereAmI.equals("Formula-News.com")) {
                                    ap = new ArticleParser(c, "formulanews");
                                    ap.start();
                                }
                                if(whereAmI.equals("Euronews.com")) {
                                    System.out.println("Euro called from saver");
                                    ap = new ArticleParser(c, "euronews");
                                    ap.start();
                                }
                                if(whereAmI.equals("PlanetF1.com")) {
                                    System.out.println("Planet called from saver");
                                    ap = new ArticleParser(c, "planetf1");
                                    ap.start();
                                }
                                if(whereAmI.equals("Crash.net")) {
                                    System.out.println("Crash called from saver");
                                    ap = new ArticleParser(c, "crash");
                                    ap.start();
                                }
                            } else break cycle;
                        }
                    }
                    if (which == 1) {
                        List<NewCard> list = na.readList();
                        Date currentDate = new Date();
                        cycle:
                        for (NewCard c : list) {
                            DateFormat format = new SimpleDateFormat("dd.MM");
                            Date articleDate = null;
                            try {
                                articleDate = format.parse(c.dateString);
                                articleDate.setYear(116);
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }


                            long diff = currentDate.getTime() - articleDate.getTime();
                            // TODO Нормальная работа с загрузкой за неделю
                            //long diffDaysForWeek = TimeUnit.MILLISECONDS.toDays(currentDate.getTime());
                            long diffDays = TimeUnit.MILLISECONDS.toDays(diff);
                            if (diffDays < 7) {
                                ArticleParser ap;
                                if(whereAmI.equals("F1News.ru")) {
                                    ap = new ArticleParser(c, "f1news");
                                    ap.start();
                                }
                                if(whereAmI.equals("Formula-News.com")) {
                                    ap = new ArticleParser(c, "formulanews");
                                    ap.start();
                                }
                                if(whereAmI.equals("Euronews.com")) {
                                    System.out.println("Euro called from saver");
                                    ap = new ArticleParser(c, "euronews");
                                    ap.start();
                                }
                                if(whereAmI.equals("PlanetF1.com")) {
                                    System.out.println("Planet called from saver");
                                    ap = new ArticleParser(c, "planetf1");
                                    ap.start();
                                }
                                if(whereAmI.equals("Crash.net")) {
                                    System.out.println("Crash called from saver");
                                    ap = new ArticleParser(c, "crash");
                                    ap.start();
                                }
                            } else break cycle;
                        }
                    }
                    if (which == 2) {
                        List<NewCard> list = na.readList();

                    for (NewCard c : list) {
                        ArticleParser ap;
                        if(whereAmI.equals("F1News.ru")) {
                            ap = new ArticleParser(c, "f1news");
                            ap.start();
                        }
                        if(whereAmI.equals("Formula-News.com")) {
                            ap = new ArticleParser(c, "formulanews");
                            ap.start();
                        }
                        if(whereAmI.equals("Euronews.com")) {
                            System.out.println("Euro called from saver");
                            ap = new ArticleParser(c, "euronews");
                            ap.start();
                        }
                        if(whereAmI.equals("PlanetF1.com")) {
                            System.out.println("Planet called from saver");
                            ap = new ArticleParser(c, "planetf1");
                            ap.start();
                        }
                        if(whereAmI.equals("Crash.net")) {
                            System.out.println("Crash called from saver");
                            ap = new ArticleParser(c, "crash");
                            ap.start();
                        }
                    }
                        //Toast.makeText(ContextGetter.getAppContext(), "Статьи загружены", Toast.LENGTH_SHORT); //
                    }
                }
            });
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
