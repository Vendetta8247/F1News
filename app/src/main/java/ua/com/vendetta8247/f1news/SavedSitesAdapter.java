package ua.com.vendetta8247.f1news;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SavedSitesAdapter extends
        RecyclerView.Adapter<SavedSitesAdapter.SavedSitesViewHolder> {
    private List<NewCard> cardList;

    public SavedSitesAdapter() {
        cardList = new ArrayList<NewCard>();

    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    @Override
    public void onBindViewHolder(SavedSitesViewHolder newsViewHolder, int i) {
        NewCard card = cardList.get(i);

        newsViewHolder.header.setText(card.header);
        newsViewHolder.siteName.setText(card.siteName);

        newsViewHolder.articleUrl = card.articleLink;
        newsViewHolder.headerText = card.header;

        String filename = newsViewHolder.context.getFilesDir() + "/F1News.ru/" + (card.header);
        File f = new File(filename);

        textViewSetEnabled(newsViewHolder.deleteButton);
    }

    void textViewSetEnabled(TextView tv)
    {
        tv.setEnabled(true);
        tv.setBackgroundColor(0xFFFFFFFF);
    }

    @Override
    public SavedSitesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.article_card_saved, viewGroup, false);

        return new SavedSitesViewHolder(itemView, viewGroup.getContext());
    }

    public void changeList(NewCard card)
    {
        cardList.add(card);

    }

    public void eraseList()
    {
        int itemCount = getItemCount();
        cardList.removeAll(cardList);
        notifyItemRangeRemoved(0, itemCount);
    }

    public class SavedSitesViewHolder extends RecyclerView.ViewHolder {
        protected TextView header;
        protected TextView date;
        protected  TextView siteName;

        protected ImageView image;
        protected String articleUrl;
        protected String headerText;

        public Context context;
        TextView deleteButton;

        public SavedSitesViewHolder (View v, Context context) {
            super(v);
            this.context = context;
            header =  (TextView) v.findViewById(R.id.textViewHeader);
            date = (TextView) v.findViewById(R.id.articleDate);
            siteName = (TextView) v.findViewById(R.id.articleSite);
            LinearLayout clickNew = (LinearLayout)v.findViewById(R.id.readArticle);
            deleteButton = (TextView) v.findViewById(R.id.deleteButton);

            Font.FRANKLIN_GOTHIC.apply(v.getContext(), v);

            clickNew.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            LinearLayout view = (LinearLayout) v;
                            view.setBackgroundColor(0xFFEAEAEA);
                            v.invalidate();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            Intent intent = new
                                    Intent(v.getContext(), SavedSiteReader.class);

                            String filename = v.getContext().getFilesDir() + "/" + siteName.getText() + "/" + (headerText);
                            String HTMLCode = "";
                            File f = new File(filename);
                            if (f.exists()) {
                                try {
                                    FileInputStream fis = new FileInputStream(f);
                                    StringBuilder builder = new StringBuilder();
                                    BufferedReader in = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
                                    int ch;

                                    while ((ch = in.read()) != -1) {
                                        builder.append((char) ch);
                                    }

                                    HTMLCode = builder.toString();

                                } catch (IOException ex) {
                                }
                            }

                            intent.putExtra("code", HTMLCode);
                            intent.putExtra("title", headerText);
                            v.getContext().startActivity(intent);


                        }
                        case MotionEvent.ACTION_CANCEL: {
                            LinearLayout view = (LinearLayout) v;
                            view.setBackgroundColor(0xFFFFFFFF);
                            view.invalidate();
                            break;
                        }
                    }
                    return true;
                }
            });

            deleteButton.setOnTouchListener(new View.OnTouchListener() {
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
                            String filename = v.getContext().getFilesDir() + "/" + siteName.getText() + "/" + header.getText();
                            File f = new File(filename);

                            if (f.exists() && !f.isDirectory())
                                removeAt(getPosition());
                            f.delete();

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

        public void removeAt(int position) {
            cardList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cardList.size());
        }
    }

}
