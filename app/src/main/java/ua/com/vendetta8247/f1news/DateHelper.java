package ua.com.vendetta8247.f1news;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Y500 on 26.04.2015.
 */
public class DateHelper {

    private static final Map<String, String> months = new HashMap<String, String>();
    static {
        months.put("января", "01");
        months.put("февраля", "02");
        months.put("марта", "03");
        months.put("апреля", "04");
        months.put("мая", "05");
        months.put("июня", "06");
        months.put("июля", "07");
        months.put("августа", "08");
        months.put("сентября", "09");
        months.put("октября", "10");
        months.put("ноября", "11");
        months.put("декабря", "12");
    }



    public static String toStringDate(String originalDate) {
        StringBuilder sb = new StringBuilder(originalDate.length());

        originalDate = originalDate.toLowerCase();
        String newDate = new String(originalDate);
        String tmp="";
        for (int i = 0; i<originalDate.length(); i++) {
            if(Character.isLetter(originalDate.charAt(i))) {
                tmp = tmp + originalDate.substring(i, i + 1);


            }
            else sb.append(originalDate.charAt(i));
        }

        if (months.containsKey(tmp)) {
            //System.out.println(months.containsKey(tmp));
           // System.out.println(months.get(tmp));
            newDate = newDate.replaceAll(" 2015", "");
            newDate = newDate.replaceAll(" ", ".");
            newDate = newDate.replaceAll("/", ".");
            newDate = newDate.replaceAll(tmp, months.get(tmp));



            //String string = "2, 2010";




            //sb.append(months.get(tmp));
        } else {
            sb.append(tmp);
            //System.out.println(months.containsKey("мая"));
        }
        return newDate;
    }
}
