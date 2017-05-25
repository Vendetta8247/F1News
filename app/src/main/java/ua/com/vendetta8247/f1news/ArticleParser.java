package ua.com.vendetta8247.f1news;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Y500 on 26.04.2015.
 */
public class ArticleParser {
    String articleUrl;
    String articleName;
    TextView textView;
    TextView deleteTextView;
    Context context;

    String siteName;

    String innerHtml ="<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";

    public ArticleParser(String articleName, String articleUrl, TextView textView, TextView deleteTextView , Context context)
    {
        this.articleName = articleName;
        this.articleUrl = articleUrl;
        this.textView = textView;
        this.context = context;
        this.deleteTextView = deleteTextView;
    }

    public ArticleParser(String articleName, String articleUrl, String siteName, Context context)
    {
        this.articleName = articleName;
        this.articleUrl = articleUrl;
        this.context = context;
        this.siteName = siteName;
    }

    public ArticleParser(NewCard card, String siteName)
    {
        this.articleName = card.header;
        this.articleUrl = card.articleLink;
        this.context = ContextGetter.getAppContext();
        this.siteName = siteName;
    }


    public void start()
    {
        System.out.println("SITENAME " + siteName);
        if(siteName == "f1news")
        new AsyncF1Parser().execute();
        else if(siteName == "formulanews")
            new AsyncFormulaNewsParser().execute();
        else if(siteName == "euronews") {
            new AsyncEuronewsParser().execute();
            System.out.println("Euronews parser called");
        }
        else if(siteName == "planetf1") {
            new AsyncPlanetf1Parser().execute();
            System.out.println("PlanetF1 parser called");
        }
        else if(siteName == "crash") {
            new AsyncCrashParser().execute();
            System.out.println("Crash parser called");
        }

    }


    class AsyncCrashParser extends AsyncTask<Void, Void, Void> {
        String tmp = "";

        @Override
        protected Void doInBackground(Void... params) {
            try {
                System.out.println("Crash article URL = " + articleUrl);
                Document doc = Jsoup.connect(articleUrl).get();
                Document.OutputSettings settings = doc.outputSettings();

                settings.prettyPrint(true);

                settings.escapeMode(Entities.EscapeMode.extended);
                settings.charset("ASCII");

                tmp += "<h2>" + articleName + "</h2>";

                tmp += "<br>";
                Elements toRemove = doc.select("img");
                toRemove.remove();
                toRemove = doc.select("iframe");
                toRemove.remove();

                Element articleText = doc.getElementById("art-text");
                tmp += articleText.html();
                innerHtml = tmp;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }
        @Override
        protected void onPostExecute(Void aVoid) {
            String filename = context.getFilesDir() + "/Crash.net/" + (articleName);
            System.out.println(filename);
            File f = new File(context.getFilesDir() + "/Crash.net");

            if(!f.exists() || !f.isDirectory()) {
                System.out.println(f + " не существует");
                f.mkdir();
            }


            FileOutputStream outputStream;

            try {

                File file = new File(filename);
                outputStream = new FileOutputStream(file);
                outputStream.write(innerHtml.getBytes());
                outputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class AsyncPlanetf1Parser extends AsyncTask<Void, Void, Void> {
        String tmp = "";

        @Override
        protected Void doInBackground(Void... params) {
            try {
                System.out.println("PlanetF1 article URL = " + articleUrl);
                Document doc = Jsoup.connect(articleUrl).get();
                Document.OutputSettings settings = doc.outputSettings();

                settings.prettyPrint(true);

                settings.escapeMode(Entities.EscapeMode.extended);
                settings.charset("ASCII");

                tmp += "<h2>" + articleName + "</h2>";

                tmp += "<br>";
                Elements toRemove = doc.select("img");
                toRemove.remove();
                toRemove = doc.select("iframe");
                toRemove.remove();

                Element articleText = doc.getElementsByClass("article__body").first();
                tmp += articleText.html();
                innerHtml = tmp;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }
        @Override
        protected void onPostExecute(Void aVoid) {
            String filename = context.getFilesDir() + "/PlanetF1.com/" + (articleName);
            System.out.println(filename);
            File f = new File(context.getFilesDir() + "/PlanetF1.com");

            if(!f.exists() || !f.isDirectory()) {
                System.out.println(f + " не существует");
                f.mkdir();
            }


            FileOutputStream outputStream;

            try {

                File file = new File(filename);
                outputStream = new FileOutputStream(file);
                outputStream.write(innerHtml.getBytes());
                outputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    class AsyncEuronewsParser extends AsyncTask<Void, Void, Void>
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

                tmp += "<h2>" + articleName + "</h2>";

                tmp += "<br>";
                Elements toRemove = doc.select("img");
                toRemove.remove();
                toRemove = doc.select("iframe");
                toRemove.remove();

                Element articleText = doc.getElementById("articleTranscript");
                tmp+=articleText.text();
                innerHtml = tmp;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            String filename = context.getFilesDir() + "/Euronews.com/" + (articleName);
            System.out.println(filename);
            File f = new File(context.getFilesDir() + "/Euronews.com");

            if(!f.exists() || !f.isDirectory()) {
                System.out.println(f + " не существует");
                f.mkdir();
            }


            //String filename = "/storage/emulated/0/F1News/" + TextHelper.toTranslit(articleName) + ".html";
            FileOutputStream outputStream;

            try {

                File file = new File(filename);
                outputStream = new FileOutputStream(file);
                outputStream.write(innerHtml.getBytes());
                outputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class AsyncFormulaNewsParser extends AsyncTask<Void, Void, Void>
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

                tmp += "<h2>" + articleName + "</h2>";

                tmp += "<br>";
                Elements toRemove = doc.select("img");
                toRemove.remove();
                toRemove = doc.select("iframe");
                toRemove.remove();

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
                innerHtml = tmp;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            String filename = context.getFilesDir() + "/Formula-News.com/" + (articleName);
            System.out.println(filename);
            File f = new File(context.getFilesDir() + "/Formula-News.com");

            if(!f.exists() || !f.isDirectory())
                System.out.println(f + " не существует");
                f.mkdir();


            //String filename = "/storage/emulated/0/F1News/" + TextHelper.toTranslit(articleName) + ".html";
            FileOutputStream outputStream;

            try {

                File file = new File(filename);
                outputStream = new FileOutputStream(file);
                outputStream.write(innerHtml.getBytes());
                outputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

            class AsyncF1Parser extends AsyncTask<Void, Void, Void>
        {
            String tmp = "";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    Document doc = Jsoup.connect(articleUrl).get();
                    Document.OutputSettings settings = doc.outputSettings();

                    settings.prettyPrint(true);

                    settings.escapeMode(Entities.EscapeMode.extended);
                    settings.charset("ASCII");

                    try {
                        if (articleUrl.contains("gallery")) {
                            innerHtml = new String(innerHtml + "<br><br>Загрузка изображений отключена. <a href = \"" + articleUrl + "\"> Посмотреть галерею на сайте </a>");
                        } else {
                            Elements toRemove = doc.select("img");
                            toRemove.remove();
                            toRemove = doc.select("iframe");
                            toRemove.remove();

                            for (Element link : doc.select("a")) {
                                if (!link.attr("href").toLowerCase().startsWith("http")) {
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
                            innerHtml = tmp;
                            innerHtml += "<a href = \"" + articleUrl + "\">Посмотреть статью на сайте</a>";
                        }

                    } catch (NullPointerException ex) {
                        cancel(true);
                    }
                    //tmp = elements.html();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }


            protected void onPostExecute(Void params)
        {
            if(textView != null) {
                textView.setText("Скачано");
                textView.setEnabled(false);
                textView.setBackgroundColor(0xFFEEEEEE);
            }
            //else
                //Toast.makeText(context,"Скачано", Toast.LENGTH_SHORT).show();

            if(deleteTextView != null) {
                deleteTextView.setEnabled(true);
                deleteTextView.setBackgroundColor(0xFFFFFFFF);
            }





            String filename = context.getFilesDir() + "/F1News.ru/" + (articleName);
            File f = new File(context.getFilesDir() + "/F1News.ru");

            if(!f.exists() || !f.isDirectory())
                f.mkdir();


            //String filename = "/storage/emulated/0/F1News/" + TextHelper.toTranslit(articleName) + ".html";
            FileOutputStream outputStream;

            try {

                File file = new File(filename);
                outputStream = new FileOutputStream(file);
                outputStream.write(innerHtml.getBytes());
                outputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
