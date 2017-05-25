package ua.com.vendetta8247.f1news;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Y500 on 29.04.2015.
 */
public class DrawerAdapter extends ArrayAdapter<NavDrawerItem> {

    private final Context context;
    private final int layoutResourceId;
    private NavDrawerItem data[] = null;
    private int disabledPosition = 228;

    public DrawerAdapter(Context context, int layoutResourceId, NavDrawerItem [] data, int disabledPosition)
    {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
        this.disabledPosition = disabledPosition;
    }

    public DrawerAdapter(Context context, int layoutResourceId, NavDrawerItem [] data)
    {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        View v = inflater.inflate(layoutResourceId, parent, false);

        Font.FRANKLIN_GOTHIC.apply(v.getContext(),parent);

        ImageView imageView = (ImageView) v.findViewById(R.id.navDrawerImageView);
        TextView textView = (TextView) v.findViewById(R.id.navDrawerTextView);

        NavDrawerItem choice = data[position];

        imageView.setImageResource(choice.icon);
        textView.setText(choice.name);

        if(disabledPosition!=228)
        if(position==disabledPosition)
        {
            v.setEnabled(false);
            v.setBackgroundColor(0xFFDFDFDF);
        }

        return v;
    }

}
