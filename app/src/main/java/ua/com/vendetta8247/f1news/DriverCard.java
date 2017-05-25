package ua.com.vendetta8247.f1news;

import android.graphics.drawable.Drawable;

/**
 * Created by Y500 on 20.07.2015.
 */
public class DriverCard {
    String name, team, birthday, nationality;

    String fullBioLink;

    Drawable picture, flag;

    public DriverCard(String name, String team, String birthday, String nationality, Drawable flag, String fullBioLink)
    {
        this.name = name;
        this.team = team;
        this.birthday = birthday;
        this.nationality = nationality;

        this.fullBioLink = fullBioLink;

        this.picture = picture;
        this.flag = flag;
    }

}
