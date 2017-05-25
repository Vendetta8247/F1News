package ua.com.vendetta8247.f1news;

import java.io.File;
import java.util.Date;

/**
 * Created by Y500 on 29.04.2015.
 */
public class SavedArticle implements Comparable<SavedArticle> {
    File file;
    Date date;
    String siteName;

    public SavedArticle(File file,String siteName, Date date)
    {
        this.file = file;
        this.date = date;
        this.siteName = siteName;
    }

    @Override
    public int compareTo(SavedArticle another) {
        int returnable = this.date.compareTo(another.date);
        return returnable;
    }
}
