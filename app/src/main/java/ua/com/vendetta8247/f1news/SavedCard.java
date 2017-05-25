package ua.com.vendetta8247.f1news;

import android.graphics.drawable.Drawable;

/**
 * Created by Y500 on 08.07.2015.
 */
public class SavedCard {
    public String siteName;
    public String textViewNewsQuantity;
    public int id;

    public Drawable imageMain;
    public Drawable imageFlag;

    public SavedCard(String siteName, String textViewNewsQuantity, int id, Drawable imageMain, Drawable imageFlag)
    {
        this.id = id;
        this.siteName=siteName;
        this.textViewNewsQuantity = textViewNewsQuantity;
        this.imageMain = imageMain;
        this.imageFlag = imageFlag;
    }
}
