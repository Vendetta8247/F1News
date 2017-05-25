package ua.com.vendetta8247.f1news;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Font {
    public static final Font  PROXIMA_NOVA    = new Font("ProximaNovaRegular.otf");
    public static final Font  FRANKLIN_GOTHIC = new Font("fonts/OpenSans-Regular.ttf");
    private final String      assetName;
    private volatile Typeface typeface;

    public  Font(String assetName) {
        this.assetName = assetName;
    }

    public void apply(Context context, View textView) {
       // if (typeface == null) {
            synchronized (this) {
                //if (typeface == null) {



                    if (textView instanceof ViewGroup) {
                        //System.out.println("textView instanceof ViewGroup = true" + textView.getClass() + ((ViewGroup) textView).getChildCount());
                        ViewGroup vg = (ViewGroup) textView;
                        for (int i = 0; i < vg.getChildCount(); i++) {
                            View child = vg.getChildAt(i);
                            apply(context, child);
                        }
                        typeface = Typeface.createFromAsset(context.getAssets(), assetName);
                    } else if (textView instanceof TextView ) {
                        //System.out.println("textView instanceof ViewGroup = false  " + textView.getClass());
                        typeface = Typeface.createFromAsset(context.getAssets(), assetName);
                        ((TextView) textView).setTypeface(typeface);
                    }
               // }
            }
       // }
        //((TextView)textView).setTypeface(typeface);
    }
}