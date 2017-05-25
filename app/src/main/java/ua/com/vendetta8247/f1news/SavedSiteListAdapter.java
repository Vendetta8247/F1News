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

public class SavedSiteListAdapter extends
        RecyclerView.Adapter<SavedSiteListAdapter.SavedSitesListViewHolder>  {


    private List<SavedCard> siteList;

    public SavedSiteListAdapter() {
        siteList = new ArrayList<SavedCard>();
    }

    @Override
    public int getItemCount() {
        return siteList.size();
    }


    @Override
    public void onBindViewHolder(SavedSiteListAdapter.SavedSitesListViewHolder sitesViewHolder, int i) {

        SavedCard card = siteList.get(i);
        sitesViewHolder.textViewNewsQuantity.setText(card.textViewNewsQuantity);
        sitesViewHolder.siteName.setText(card.siteName);

        sitesViewHolder.imageViewMain.setImageDrawable(card.imageMain);
        sitesViewHolder.imageViewFlag.setImageDrawable(card.imageFlag);


    }

    @Override
    public SavedSitesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.saved_site_card, parent, false);

        return new SavedSitesListViewHolder(itemView);
    }

    public void addItem(SavedCard card)
    {
        siteList.add(card);
    }


    public void setNewsQuantity (int position, String str)
    {
        metka:
        for(SavedCard card : siteList) {
            if(card.id == position) {

                card.textViewNewsQuantity = str;
                notifyDataSetChanged();
                break metka;
            }
        }
    }


    public void removeAllItemsFromList()
    {
        int itemCount = getItemCount();
        siteList.removeAll(siteList);
        notifyItemRangeRemoved(0, itemCount);
    }




    public static class SavedSitesListViewHolder extends RecyclerView.ViewHolder {
        protected TextView siteName;
        protected TextView textViewNewsQuantity;

        protected ImageView imageViewMain;
        protected ImageView imageViewFlag;

        public SavedSitesListViewHolder (View v) {
            super(v);
            siteName =  (TextView) v.findViewById(R.id.siteName);
            textViewNewsQuantity = (TextView) v.findViewById(R.id.textViewNewsQuantity);

            imageViewMain = (ImageView) v.findViewById(R.id.imageViewMain);
            imageViewFlag = (ImageView) v.findViewById(R.id.imageViewFlag);

            Font.FRANKLIN_GOTHIC.apply(v.getContext(),v);


            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            CardView view = (CardView) v;
                            view.setCardBackgroundColor(0xFFEAEAEA);
                            v.invalidate();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            Intent intent = new
                                    Intent(v.getContext(), SavedSitesActivity.class);
                            intent.putExtra("site",siteName.getText());
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
