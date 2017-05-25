package ua.com.vendetta8247.f1news;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class NewsAdapter extends
        RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<NewCard> cardList;
    String imageUrl;

    public NewsAdapter() {
        cardList = new ArrayList<NewCard>();
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }


    public static Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x = null;

        //HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        HttpClient clien = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        HttpResponse response;

        try{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            response = (HttpResponse) clien.execute(request);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufferedEntity  = new BufferedHttpEntity(entity);
            InputStream inputStream = bufferedEntity.getContent();

            x = BitmapFactory.decodeStream(inputStream, null, options);

            options.inJustDecodeBounds = false;
            options.inSampleSize = calculateInSampleSize(options, 50);
            inputStream = bufferedEntity.getContent();
            x = BitmapFactory.decodeStream(inputStream, null, options);
            }catch (ClientProtocolException ex)
        {
            ex.printStackTrace();
        }catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return new BitmapDrawable(x);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth) {
        // Raw height and width of image

        final int width = options.outWidth;
        int inSampleSize = 1;

        if (width > reqWidth) {
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    public void onBindViewHolder(NewsViewHolder newsViewHolder, int i) {
        NewCard card = cardList.get(i);
        imageUrl = card.dateString;

        newsViewHolder.header.setText(card.header);
        newsViewHolder.date.setText(card.dateString);
        newsViewHolder.siteName.setText(card.siteName);


            newsViewHolder.image.setImageDrawable(card.image);

        newsViewHolder.articleUrl = card.articleLink;
        newsViewHolder.headerText = card.header;

        String filename = newsViewHolder.context.getFilesDir() + "/F1News.ru/" + (card.header);
        File f = new File(filename);
/*
        //newsViewHolder.downloadButton.setEnabled(true);
        textViewSetEnabled(newsViewHolder.downloadButton);
        newsViewHolder.downloadButton.setText("Скачать");
        //newsViewHolder.deleteButton.setEnabled(false);
        textViewSetDisabled(newsViewHolder.deleteButton);

        if(f.exists() && !f.isDirectory())
        {
            textViewSetEnabled(newsViewHolder.deleteButton);
            //newsViewHolder.downloadButton.setEnabled(false);
            textViewSetDisabled(newsViewHolder.downloadButton);
            newsViewHolder.downloadButton.setText("Скачано");
           // newsViewHolder.deleteButton.setEnabled(true);
        }
        */
    }

    void textViewSetEnabled(TextView tv)
    {
        tv.setEnabled(true);
        tv.setBackgroundColor(0xFFFFFFFF);
    }

    void textViewSetDisabled(TextView tv)
    {
        tv.setEnabled(false);
        tv.setBackgroundColor(0xFFEEEEEE);
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.article_card, viewGroup, false);

        return new NewsViewHolder(itemView, viewGroup.getContext());
    }

    public void changeList(NewCard card)
    {
       cardList.add(card);

    }

    public ArrayList<NewCard> readList()
    {
        return (ArrayList<NewCard>)cardList;
    }

    public void changePic(int pos, String URL)
    {
        NewCard card = cardList.get(pos);
        try{

        card.setImage(drawableFromUrl(URL));

        }catch (IOException ex)
        {
            ex.printStackTrace();
        }

        //notifyDataSetChanged();

    }

    public void eraseList()
    {
        int itemCount = getItemCount();
        cardList.removeAll(cardList);
        notifyItemRangeRemoved(0,itemCount);
    }

     public static class NewsViewHolder extends RecyclerView.ViewHolder {
        protected TextView header;
         protected TextView date;
         protected  TextView siteName;

        protected ImageView image;
         protected String articleUrl;
         protected String headerText;

         public Context context;
        //TextView downloadButton;
        //TextView deleteButton;
        int size;

        public NewsViewHolder (View v, Context context) {
            super(v);
            this.context = context;
            header =  (TextView) v.findViewById(R.id.textViewHeader);
            date = (TextView) v.findViewById(R.id.articleDate);
            siteName = (TextView) v.findViewById(R.id.articleSite);
            image = (ImageView) v.findViewById(R.id.cardImage);
            LinearLayout clickNew = (LinearLayout)v.findViewById(R.id.readArticle);
            Font.FRANKLIN_GOTHIC.apply(v.getContext(),v);
           // downloadButton = (TextView) v.findViewById(R.id.downloadButton);
            //deleteButton = (TextView) v.findViewById(R.id.deleteButton);


v.setOnTouchListener(new View.OnTouchListener() {
    @Override
    public boolean onTouch(View v2, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                CardView view = (CardView) v2;
                view.setCardBackgroundColor(0xFFF0F0F0);
                v2.invalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                Intent intent = new
                        Intent(v2.getContext(), NewArticle.class);

                intent.putExtra("adress", articleUrl);
                //System.out.println(articleUrl);
                intent.putExtra("title", headerText);
                v2.getContext().startActivity(intent);
            }
            case MotionEvent.ACTION_CANCEL: {
                CardView view = (CardView) v2;
                view.setCardBackgroundColor(0xFFFFFFFF);
                view.invalidate();
                break;
            }
        }
        return true;
    }
});
//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v2) {
//
//
//                }
//            });

           /* downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(downloadButton.isEnabled()) {
                        ArticleParser ap = new ArticleParser(headerText, articleUrl, downloadButton, deleteButton, v.getContext());
                        ap.start();
                    }

                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(deleteButton.isEnabled()) {
                        String filename = v.getContext().getFilesDir() + "/F1News.ru/" + (headerText);

                        File f = new File(filename);

                        if(f.exists() && !f.isDirectory())
                            f.delete();

                        deleteButton.setEnabled(false);
                        deleteButton.setBackgroundColor(0xFFEEEEEE);

                        downloadButton.setEnabled(true);
                        downloadButton.setBackgroundColor(0xFFFFFFFF);
                        downloadButton.setText("Скачать");


                    }

                }
            });*/

        }


    }

}
