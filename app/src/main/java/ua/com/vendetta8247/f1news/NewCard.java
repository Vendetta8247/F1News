package ua.com.vendetta8247.f1news;

import android.graphics.drawable.Drawable;

/**
 * Created by Y500 on 26.01.2015.
 */
public class NewCard {
    public String header;

    public String dateString;
    public String siteName;
    public String articleLink;

    public Drawable image;

    public NewCard(String header, String siteName, String dateString, String articleLink)
    {
        this.header = header;

        this.dateString = dateString;
        this.articleLink = articleLink;
        this.siteName = siteName;
        this.image = ContextGetter.getAppContext().getResources().getDrawable(R.drawable.ph);
    }

    public void setImage(Drawable image)
    {
        this.image = image;
    }
}
