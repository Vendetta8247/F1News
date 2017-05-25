package ua.com.vendetta8247.f1news;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Y500 on 17.02.2015.
 */
public class FixedRecyclerView extends RecyclerView
{
    public FixedRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canScrollVertically(int direction) {

        return super.canScrollVertically(direction);
    }
}
