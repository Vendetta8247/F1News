package ua.com.vendetta8247.f1news;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class SitesActivity extends ActionBarActivity {

    int[] numberOfNews = new int[5];
    String[] lastNews = new String[5];

    private RecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ListView mDrawerList;
    DrawerLayout mDrawerLayout;
    List<SiteCard> cardList;
    SitesAdapter sa;
    String locale;
    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ContextGetter.getAppContext());

    SharedPreferences.OnSharedPreferenceChangeListener listener;

    boolean f1newsRuCheckbox;
    boolean formulaNewsCheckbox;
    boolean euronewsCheckbox;
    boolean planetf1Checkbox;
    boolean crashCheckbox;

    Calendar c = Calendar.getInstance();
    int currentMonth = c.get(c.MONTH) + 1;


    class numberOfNews extends AsyncTask <Void,Void,Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeRefreshLayout.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            lastNews[0] = "\n";
            try {
                Document doc = Jsoup.connect("http://www.f1news.ru/news/" + c.get(c.YEAR) + "/" + currentMonth).timeout(30000).get();
                Element e = doc.getElementsByClass("widget_body").first();
                Elements es = e.getElementsByTag("li");

                int i =0;
                String date = "";

                cycle:
                for(Element element : es)
                {
                    if (element.hasClass("list_head")) {

                        date = element.text();
                        date = DateHelper.toStringDate(date);

                    } else {

                        Element forHeader = element.getElementsByTag("a").first();
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
                        long diffSeconds = diff / 1000 % 60;
                        long diffMinutes = diff / (60 * 1000) % 60;
                        long diffDays = TimeUnit.MILLISECONDS.toDays(diff); //diff / (24 * 60*1000) % 24;

                        if (sharedPref.getString("listPref", "1").contains("1")) {
                            if (diffDays > 0) break cycle;
                        }
                        if (sharedPref.getString("listPref", "1").contains("2")) {
                            if (diffDays > 1) break cycle;
                        }
                        if (sharedPref.getString("listPref", "1").contains("3")) {
                            if (diffDays > 6) break cycle;
                        }


                        if(i<3) {
                            if (i != 2)
                                lastNews[0] = new String(lastNews[0] + forHeader.text() + "\n\n");
                            else lastNews[0] = new String(lastNews[0] + forHeader.text());
                            i++;
                        }


                        numberOfNews[0]++;
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
                cancel(true);
                this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
            Calendar c = Calendar.getInstance();
            int currentMonth = c.get(c.MONTH) + 1;
            int i =0;
            lastNews[1] = "";
            try {
                Document doc = Jsoup.connect("http://formula-news.com/news/category/v=formula&m=" + currentMonth+ "&y=" + c.get(c.YEAR)).timeout(60000).get();
                Element e = doc.getElementsByClass("news").first();
                Date currentDate = new Date();

                try {
                    Elements es = e.getElementsByTag("li");

                    cycle:
                    for (Element element : es) {
                        Element date = element.getElementsByClass("news-data").first();
                        if (isCancelled()) return null;
                        DateFormat format = new SimpleDateFormat("dd/MM");
                        Date articleDate = null;
                        try {
                            articleDate = format.parse(date.text());
                            articleDate.setYear(116);
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }


                        long diff = currentDate.getTime() - articleDate.getTime();
                        long diffSeconds = diff / 1000 % 60;
                        long diffMinutes = diff / (60 * 1000) % 60;
                        long diffDays = TimeUnit.MILLISECONDS.toDays(diff); //diff / (24 * 60*1000) % 24;

                        if (sharedPref.getString("listPref", "1").contains("1")) {
                            if (diffDays > 0) break cycle;
                        }
                        if (sharedPref.getString("listPref", "1").contains("2")) {
                            if (diffDays > 1) break cycle;
                        }
                        if (sharedPref.getString("listPref", "1").contains("3")) {
                            if (diffDays > 6) break cycle;
                        }
                        if (i < 3) {
                            Element forHeader = element.getElementsByTag("a").last();
                            if (i != 2)
                                lastNews[1] = new String(lastNews[1] + forHeader.text() + "\n\n");
                            else lastNews[1] = new String(lastNews[1] + forHeader.text());
                            i++;
                        }



                        numberOfNews[1]++;
                    }
                }
                catch (NullPointerException ex)
                {
                    lastNews[1] = getString(R.string.no_news_text);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }



// Euronews
            i =0;
            lastNews[2] = "";
            try {
                String prefix = "";

                if (locale.equals("ru")) {
                    prefix = "ru.";
                }
                else if(locale.equals("uk"))
                {

                    prefix = "ua.";
                }

                Document doc = Jsoup.connect("http://" + prefix + "euronews.com/sport/formula-1/").timeout(30000).get();
                numberOfNews[2] = 1;



                Elements es = doc.getElementsByClass("subcategoryList");


                Element e = doc.getElementsByClass("topStoryWrapper").first();
                Element forHeader = e.getElementsByTag("h2").first();
                lastNews[2] = new String(lastNews[2] + forHeader.text() + "\n\n");

                try {
                    for (Element el : es) {
                        Elements elements = el.getElementsByTag("li");
                        for (Element element : elements) {
                            numberOfNews[2]++;


                            if (i < 2) {
                                Element elf = element.getElementsByClass("themeArtTitle").first();
                                String articleName = elf.getElementsByTag("a")
                                        .first()
                                        .attr("title");

                                if (i != 1)
                                    lastNews[2] = new String(lastNews[2] + articleName + "\n\n");
                                else lastNews[2] = new String(lastNews[2] + articleName);
                                i++;
                            }
                        }
                    }
                }
                catch (NullPointerException ex)
                {
                    ex.printStackTrace();
                    lastNews[2] = getString(R.string.no_news_text);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

//Planet F1

            i =0;
            lastNews[3] = "";
            try {
                Document doc = Jsoup.connect("http://planetf1.com/news/").timeout(30000).get();

                Elements es = doc.getElementsByClass("articleList__item");


                try {
                    for (Element el : es) {

                        if(i!=3) {
                            Element e = el.getElementsByTag("h3").first();
                            String articleName = e.text();


                            if(i!=2)
                                lastNews[3] = new String(lastNews[3] + articleName + "\n\n");
                            else
                                lastNews[3] = new String(lastNews[3] + articleName);
                            i++;
                        }
                        else break;
                    }
                }


                catch (NullPointerException ex)
                {
                    ex.printStackTrace();
                    lastNews[3] = getString(R.string.no_news_text);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            //Crash.net
            i =0;
            lastNews[4] = "";
            try {
                Document doc = Jsoup.connect("http://www.crash.net/f1/news_archive/1/content.html").timeout(30000).get();

                Element element = doc.getElementsByClass("content").first();
                Element listElement = element.getElementsByTag("ul").first();
                Elements listContent = listElement.getElementsByTag("li");


                try {

                    for(Element el : listContent) {
                        if (i != 3) {
                            Element e = el.getElementsByTag("span").first();
                            Element e1 = e.getElementsByTag("span").get(2);
                            String articleName = e1.text();


                            if (i != 2)
                                lastNews[4] = new String(lastNews[4] + articleName + "\n\n");
                            else
                                lastNews[4] = new String(lastNews[4] + articleName);
                            i++;
                        }
                        else break;
                    }
                }


                catch (NullPointerException ex)
                {
                    ex.printStackTrace();
                    lastNews[4] = getString(R.string.no_news_text);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();


        }

        @Override
        protected void onPostExecute(Void aVoid) {


            if(f1newsRuCheckbox) {
                sa.setNewsQuantity(0, getString(R.string.news_in_total) + " " + numberOfNews[0]);
                if(lastNews[0].equals("\n"))
                {
                    sa.setThreeNews(0, getString(R.string.no_news_text));
                }
                else
                sa.setThreeNews(0, lastNews[0]);
            }
            if(formulaNewsCheckbox) {
                sa.setNewsQuantity(1, getString(R.string.news_in_total)+ " " + numberOfNews[1]);
                if(lastNews[1].equals(""))
                {
                    sa.setThreeNews(1, getString(R.string.no_news_text));
                }
                else
                    sa.setThreeNews(1, lastNews[1]);
            }
            if(euronewsCheckbox) {
                sa.setNewsQuantity(2, getString(R.string.news_in_total)+ " " + numberOfNews[2]);
                if(lastNews[2].equals(""))
                {
                    sa.setThreeNews(2, getString(R.string.no_news_text));
                }
                else
                    sa.setThreeNews(2, lastNews[2]);
            }
            if(planetf1Checkbox) {
                if (lastNews[3].equals("")) {
                    sa.setThreeNews(3, getString(R.string.no_news_text));
                } else
                    sa.setThreeNews(3, lastNews[3]);
            }
            if(crashCheckbox) {
                if (lastNews[4].equals("")) {
                    sa.setThreeNews(4, getString(R.string.no_news_text));
                } else
                    sa.setThreeNews(4, lastNews[4]);
            }

            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sa.removeAllItemsFromList();
        sa.notifyDataSetChanged();







        for(SiteCard card: cardList) {
            sa.addItem(card);
        }

    }

    int currentPage;
    View[] mas = new View[10];
    TextView previousButton;
    TextView nextButton;
    TextView skipButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sites);
        locale = Locale.getDefault().getLanguage();
        cardList = new ArrayList<SiteCard>();
        final ActionBar ab = getSupportActionBar();
        previousButton = (TextView)findViewById(R.id.previous_button);
        nextButton = (TextView) findViewById(R.id.next_button);
        skipButton = (TextView)findViewById(R.id.skipButton);


        if (sharedPref.getBoolean("firstrun", true)) {


            currentPage = 0;
            previousButton.setEnabled(false);
            previousButton.setBackgroundColor(0xFFEAEAEA);
            Display display = getWindowManager().getDefaultDisplay();

            final int width = display.getWidth();

            findViewById(R.id.drawer_layout).setVisibility(View.GONE);
            findViewById(R.id.help_screen).setVisibility(View.VISIBLE);



            mas[0] = findViewById(R.id.content_panel1);
            mas[1] = findViewById(R.id.content_panel2);
            mas[2] = findViewById(R.id.content_panel3);
            mas[3] = findViewById(R.id.content_panel4);
            mas[4] = findViewById(R.id.content_panel5);
            mas[5] = findViewById(R.id.content_panel6);
            mas[6] = findViewById(R.id.content_panel7);
            mas[7] = findViewById(R.id.content_panel8);
            mas[8] = findViewById(R.id.content_panel9);
            mas[9] = findViewById(R.id.content_panel10);




            for(int i  = 1; i<mas.length; i++)
            {
                mas[i].setX(width);
                mas[i].setAlpha(0.0f);
            }



            final View.OnTouchListener onTouchNext = new View.OnTouchListener()
            {
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            TextView view = (TextView) v;
                            view.setBackgroundColor(0xFFEAEAEA);
                            v.invalidate();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            mas[currentPage].animate().x(-width).alpha(0.0f).setDuration(700);
                            mas[currentPage + 1].animate().x(0).alpha(1.0f).setDuration(700);
                            currentPage++;
                            if (currentPage == mas.length - 1) {
                                nextButton.setText(getString(R.string.finish_text));
                                nextButton.setOnTouchListener(new View.OnTouchListener() {
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
                                        RelativeLayout helpScreen = (RelativeLayout) findViewById(R.id.help_screen);
                                        helpScreen.animate().alpha(0.0f).setDuration(700);
                                        findViewById(R.id.sitesList).animate().alpha(1.0f).setDuration(700);
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                ab.show();
                                                findViewById(R.id.drawer_layout).setVisibility(View.VISIBLE);
                                                findViewById(R.id.help_screen).setVisibility(View.GONE);
                                            }
                                        }, 700);}
                                            case MotionEvent.ACTION_CANCEL: {
                                                TextView view = (TextView) v;
                                                view.setBackgroundColor(0xFFFFFFFF);
                                                view.invalidate();
                                                break;
                                            }}
                                        return true;
                                    }
                                });
                            }
                            previousButton.setEnabled(true);
                            previousButton.setBackgroundColor(0xFFFFFFFF);
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
            };

            ab.hide();

            nextButton.setOnTouchListener(new View.OnTouchListener() {
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
                            mas[currentPage].animate().x(-width).alpha(0.0f).setDuration(700);
                            mas[currentPage + 1].animate().x(0).alpha(1.0f).setDuration(700);
                            currentPage++;

                            if (currentPage == mas.length - 1) {
                                nextButton.setText(getString(R.string.finish_text));
                                nextButton.setOnTouchListener(new View.OnTouchListener() {
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
                                                RelativeLayout helpScreen = (RelativeLayout) findViewById(R.id.help_screen);
                                                helpScreen.animate().alpha(0.0f).setDuration(700);
                                                findViewById(R.id.sitesList).animate().alpha(1.0f).setDuration(700);
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    public void run() {
                                                        ab.show();
                                                        findViewById(R.id.drawer_layout).setVisibility(View.VISIBLE);
                                                        findViewById(R.id.help_screen).setVisibility(View.GONE);
                                                    }
                                                }, 700);}
                                            case MotionEvent.ACTION_CANCEL: {
                                                TextView view = (TextView) v;
                                                view.setBackgroundColor(0xFFFFFFFF);
                                                view.invalidate();
                                                break;
                                            }}
                                        return true;
                                    }
                                });
                            }
                            previousButton.setEnabled(true);
                            previousButton.setBackgroundColor(0xFFFFFFFF);
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




            previousButton.setOnTouchListener(new View.OnTouchListener() {
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
                            mas[currentPage - 1].animate().x(0).alpha(1.0f).setDuration(700);
                            mas[currentPage].animate().x(width).alpha(0.0f).setDuration(700);
                            if(currentPage == mas.length - 1)
                            {
                                nextButton.setText("Next");
                                nextButton.setOnTouchListener(onTouchNext);
                            }
                            currentPage--;
                            if (currentPage == 0) {
                                previousButton.setEnabled(false);
                                previousButton.setBackgroundColor(0xFFEAEAEA);
                            }



                            nextButton.setEnabled(true);
                            nextButton.setBackgroundColor(0xFFFFFFFF);
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


            skipButton.setOnTouchListener(new View.OnTouchListener() {
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
                            RelativeLayout helpScreen = (RelativeLayout) findViewById(R.id.help_screen);
                            helpScreen.animate().alpha(0.0f).setDuration(700);
                            findViewById(R.id.sitesList).animate().alpha(1.0f).setDuration(700);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    ab.show();
                                    findViewById(R.id.drawer_layout).setVisibility(View.VISIBLE);
                                    findViewById(R.id.help_screen).setVisibility(View.GONE);
                                }
                            }, 700);
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








        euronewsCheckbox = sharedPref.getBoolean("euronews_checkbox", true);
        sharedPref.edit().putBoolean("euronews_checkbox", true).apply();
        if(locale.equals("ru")) {
            f1newsRuCheckbox = sharedPref.getBoolean("f1news_ru_checkbox", true);
            formulaNewsCheckbox = sharedPref.getBoolean("formula_news_com_checkbox", true);
            planetf1Checkbox = sharedPref.getBoolean("planetf1_checkbox", false);
            crashCheckbox = sharedPref.getBoolean("crash_checkbox", false);
            sharedPref.edit().putBoolean("f1news_ru_checkbox", true).apply();
            sharedPref.edit().putBoolean("formula_news_com_checkbox", true).apply();
            sharedPref.edit().putBoolean("planetf1_checkbox", false).apply();
            sharedPref.edit().putBoolean("crash_checkbox", false).apply();


        }
        else if (locale.equals("uk")){
            f1newsRuCheckbox = sharedPref.getBoolean("f1news_ru_checkbox", true);
            formulaNewsCheckbox = sharedPref.getBoolean("formula_news_com_checkbox", true);
            planetf1Checkbox = sharedPref.getBoolean("planetf1_checkbox", false);
            crashCheckbox = sharedPref.getBoolean("crash_checkbox", false);
            sharedPref.edit().putBoolean("f1news_ru_checkbox", true).apply();
            sharedPref.edit().putBoolean("formula_news_com_checkbox", true).apply();
            sharedPref.edit().putBoolean("planetf1_checkbox", false).apply();
            sharedPref.edit().putBoolean("crash_checkbox", false).apply();
        }
        else {
            f1newsRuCheckbox = sharedPref.getBoolean("f1news_ru_checkbox", false);
            formulaNewsCheckbox = sharedPref.getBoolean("formula_news_com_checkbox", false);
            planetf1Checkbox = sharedPref.getBoolean("planetf1_checkbox", true);
            crashCheckbox = sharedPref.getBoolean("crash_checkbox", true);
            sharedPref.edit().putBoolean("f1news_ru_checkbox", false).apply();
            sharedPref.edit().putBoolean("formula_news_com_checkbox", false).apply();
            sharedPref.edit().putBoolean("planetf1_checkbox", true).apply();
            sharedPref.edit().putBoolean("crash_checkbox", true).apply();
        }
            sharedPref.edit().putBoolean("firstrun", false).apply();
        }
        else
        {
            ab.show();

            findViewById(R.id.drawer_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.help_screen).setVisibility(View.GONE);
            f1newsRuCheckbox = sharedPref.getBoolean("f1news_ru_checkbox", true);
            formulaNewsCheckbox = sharedPref.getBoolean("formula_news_com_checkbox", true);
            planetf1Checkbox = sharedPref.getBoolean("planetf1_checkbox",true);
            euronewsCheckbox = sharedPref.getBoolean("euronews_checkbox", true);
            crashCheckbox = sharedPref.getBoolean("crash_checkbox", true);
        }
       // PreferenceManager.setDefaultValues();

        if(f1newsRuCheckbox)
            cardList.add(new SiteCard("F1News.ru", getString(R.string.simple_loading_text), getString(R.string.simple_loading_text), 0, getResources().getDrawable(R.drawable.f1news), getResources().getDrawable(R.drawable.rus1)));

        if(formulaNewsCheckbox)
            cardList.add(new SiteCard("Formula-News.com",getString(R.string.simple_loading_text), getString(R.string.simple_loading_text), 1, getResources().getDrawable(R.drawable.formulanews), getResources().getDrawable(R.drawable.rus1)));

        if(euronewsCheckbox) {
            if(locale.equals("ru"))
                cardList.add(new SiteCard("Euronews.com", getString(R.string.simple_loading_text), getString(R.string.simple_loading_text), 2, getResources().getDrawable(R.drawable.euronews), getResources().getDrawable(R.drawable.rus1)));
            else if(locale.equals("uk"))
                cardList.add(new SiteCard("Euronews.com", getString(R.string.simple_loading_text), getString(R.string.simple_loading_text), 2, getResources().getDrawable(R.drawable.euronews), getResources().getDrawable(R.drawable.ukr1)));
            else cardList.add(new SiteCard("Euronews.com", getString(R.string.simple_loading_text), getString(R.string.simple_loading_text), 2, getResources().getDrawable(R.drawable.euronews), getResources().getDrawable(R.drawable.eng1)));
        }
        if(planetf1Checkbox)
        cardList.add(new SiteCard("PlanetF1.com", " ",getString(R.string.simple_loading_text), 3, getResources().getDrawable(R.drawable.planetf1), getResources().getDrawable(R.drawable.eng1)));

        if(crashCheckbox)
        cardList.add(new SiteCard("Crash.net", "", getString(R.string.simple_loading_text), 4, getResources().getDrawable(R.drawable.crashlogocut),getResources().getDrawable(R.drawable.eng1)));
       // cardList.add(new SiteCard("To Be Added", "Загрузка...", "Загрузка...", 4, getResources().getDrawable(R.drawable.f1ru), getResources().getDrawable(R.drawable.eng1)));
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                recreate();
            }
        };

        sharedPref.registerOnSharedPreferenceChangeListener(listener);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        NavDrawerItem[] drawerItems = {new NavDrawerItem(R.drawable.homeicon, getString(R.string.main_page_text)), new NavDrawerItem(R.drawable.downloadicon, getString(R.string.downloaded_articles_text)), new NavDrawerItem(R.drawable.tableicon, getString(R.string.rating_text)), new NavDrawerItem(R.drawable.racehelmet, getString(R.string.pilots_text)),new NavDrawerItem(R.drawable.settingsicon, getString(R.string.settings_text))};
        mDrawerList.setAdapter(new DrawerAdapter(this,
                R.layout.drawer_list_item, drawerItems, 0));

        ImageView img = (ImageView) findViewById(R.id.imageBar);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==1)
                {
                      Intent intent = new
                      Intent(view.getContext(), SavedSitesList.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intent);


                }

                if(position==2)
                {
                    Intent intent = new
                            Intent(view.getContext(), TableActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intent);


                }

                if(position==3)
                {
                    Intent intent = new
                            Intent(view.getContext(), DriverListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intent);


                }

                if(position==4)
                {
                    SettingsActivity.activity = SitesActivity.this;
                    Intent toStart = new Intent(getApplicationContext(), SettingsActivity.class);

                    //toStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(toStart);

                   /* Intent intent = new
                            Intent(view.getContext(), SettingsActivity.class);


                    view.getContext().startActivity(intent);*/
                }
            }
        });



        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.sitesList);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);


        sa = new SitesAdapter();
        recyclerView.setAdapter(sa);
        DefaultItemAnimator dia = new DefaultItemAnimator();
        dia.setAddDuration(4000);
        dia.setRemoveDuration(400);
        recyclerView.setItemAnimator(dia);






        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshSites);
        mSwipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sa.setThreeNews(0, getString(R.string.simple_loading_text));
                sa.setNewsQuantity(0, getString(R.string.simple_loading_text));
                sa.setThreeNews(1, getString(R.string.simple_loading_text));
                sa.setNewsQuantity(1, getString(R.string.simple_loading_text));
                sa.setThreeNews(2, getString(R.string.simple_loading_text));
                sa.setNewsQuantity(2, getString(R.string.simple_loading_text));
                sa.setThreeNews(3, getString(R.string.simple_loading_text));
                sa.setThreeNews(4, getString(R.string.simple_loading_text));
                           refreshNumbers();

            }
        });
        mSwipeRefreshLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        24,
                        getResources().getDisplayMetrics()));
        mSwipeRefreshLayout.setRefreshing(true);


        mSwipeRefreshLayout.setColorScheme(R.color.color1,R.color.color2);

        new numberOfNews().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
     }


    public void refreshNumbers()
    {
        for(int i = 0; i<numberOfNews.length; i++) {
            System.out.println(numberOfNews.length);
            numberOfNews[i] = 0;
        }

        new numberOfNews().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
