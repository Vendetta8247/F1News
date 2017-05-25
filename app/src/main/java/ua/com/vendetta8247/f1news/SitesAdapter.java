package ua.com.vendetta8247.f1news;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SitesAdapter extends
        RecyclerView.Adapter<SitesAdapter.SitesViewHolder>  {


    private List<SiteCard> siteList;

    public SitesAdapter() {
        siteList = new ArrayList<SiteCard>();
    }

    @Override
    public int getItemCount() {
        return siteList.size();
    }


    @Override
    public void onBindViewHolder(SitesAdapter.SitesViewHolder sitesViewHolder, int i) {

        SiteCard card = siteList.get(i);
        sitesViewHolder.textViewNewsQuantity.setText(card.textViewNewsQuantity);
        sitesViewHolder.siteName.setText(card.siteName);
        sitesViewHolder.threeLastNews.setText(card.threeNews);

        sitesViewHolder.imageViewMain.setImageDrawable(card.imageMain);
        sitesViewHolder.imageViewFlag.setImageDrawable(card.imageFlag);


    }

    @Override
    public SitesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.site_card, parent, false);

        return new SitesViewHolder(itemView);
    }

    public void addItem(SiteCard card)
    {
        siteList.add(card);
    }


    public void setNewsQuantity (int position, String str)
    {
        metka:
        for(SiteCard card : siteList) {
            if(card.id == position) {

                card.textViewNewsQuantity = str;
                notifyDataSetChanged();
                break metka;
            }
        }
    }

    public void setThreeNews (int position, String str)
    {
        metka:
        for(SiteCard card : siteList) {
            if(card.id == position) {

                card.threeNews = str;
                notifyDataSetChanged();
                break metka;
            }
        }
    }

    public void removeAllItemsFromList()
    {
        siteList.removeAll(siteList);
    }




    public static class SitesViewHolder extends RecyclerView.ViewHolder {
        protected TextView siteName;
        protected TextView textViewNewsQuantity;
        protected TextView threeLastNews;

        protected ImageView imageViewMain;
        protected ImageView imageViewFlag;

        public SitesViewHolder (View v) {
            super(v);
            siteName =  (TextView) v.findViewById(R.id.siteName);
            textViewNewsQuantity = (TextView) v.findViewById(R.id.textViewNewsQuantity);
            threeLastNews = (TextView) v.findViewById(R.id.threeNewsList);

            imageViewMain = (ImageView) v.findViewById(R.id.imageViewMain);
            imageViewFlag = (ImageView) v.findViewById(R.id.imageViewFlag);

            Font.FRANKLIN_GOTHIC.apply(v.getContext(), v);


            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            CardView view = (CardView) v;
                            view.setCardBackgroundColor(0xFFF0F0F0);
                            v.invalidate();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            Intent intent = new
                                    Intent(v.getContext(), NewsPage.class);
                            intent.putExtra("site", siteName.getText());
                            v.getContext().startActivity(intent);
                        }
                        case MotionEvent.ACTION_CANCEL: {
                            CardView view = (CardView) v;
                            view.setCardBackgroundColor(0xFFFFFFFF);
                            view.invalidate();
                            break;
                        }
                    }
                    return true;
                }
            });
        }
    }
}
