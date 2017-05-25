package ua.com.vendetta8247.f1news;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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

/**
 * Created by Y500 on 20.07.2015.
 */
public class DriverListAdapter extends RecyclerView.Adapter<DriverListAdapter.DriverViewHolder> {
    private List<DriverCard> cardList;

    public DriverListAdapter()
    {
        cardList = new ArrayList<>();
    }

    @Override
    public void onBindViewHolder(DriverListAdapter.DriverViewHolder holder, int position) {
        DriverCard card = cardList.get(position);

        holder.name.setText(card.name);
        holder.birthday.setText(card.birthday);
        holder.nationality.setText(card.nationality);
        holder.team.setText(card.team);

        holder.fullBioLink = card.fullBioLink;


        try {
            holder.flag.setImageDrawable(card.flag);
            //System.out.println("SUCCESS FLAG");
        }
        catch (NullPointerException ex)
        {
            System.out.println("NO IMAGE FOUND FLAG");
            holder.flag.setVisibility(View.GONE);
        }

    }

    public void addCard(DriverCard c)
    {
        cardList.add(c);
    }

    @Override
    public DriverListAdapter.DriverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.driver_card, parent, false);

        return new DriverViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    class DriverViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        TextView team;
        TextView birthday;
        TextView nationality;

        String fullBioLink = "";

        ImageView picture;
        ImageView flag;

        public DriverViewHolder(final View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.driverName);
            team = (TextView) itemView.findViewById(R.id.driver_team);
            nationality = (TextView) itemView.findViewById(R.id.driver_nationality);
            birthday = (TextView) itemView.findViewById(R.id.driverAge);

            flag = (ImageView) itemView.findViewById(R.id.driver_nationality_flag);

            //picture.setVisibility(View.GONE);

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
                            Intent intent = new Intent(v.getContext(), DriverBioActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            intent.putExtra("url", fullBioLink);
                            intent.putExtra("driverName", name.getText());

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
