package ua.com.vendetta8247.f1news;

import android.graphics.drawable.Drawable;

/**
 * Created by Y500 on 10.03.2015.
 */
public class SiteCard {
    public String siteName;
    public String textViewNewsQuantity;
    public String threeNews;
    public int id;

    public Drawable imageMain;
    public Drawable imageFlag;

    public SiteCard(String siteName, String textViewNewsQuantity, String threeNews, int id, Drawable imageMain, Drawable imageFlag)
    {
        this.id = id;
        this.siteName=siteName;
        this.threeNews = threeNews;
        this.textViewNewsQuantity = textViewNewsQuantity;
        this.imageMain = imageMain;
        this.imageFlag = imageFlag;
    }
}
