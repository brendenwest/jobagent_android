package com.brisksoft.jobagent.Classes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by brenden on 7/24/17.
 */

public class DateUtils {

    // from java.lang.String into java.util.Date
    public static Date dateFromShortString(String dateString) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        try {
            return fmt.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static Date dateFromLongString(String dateString) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
        try {
            return fmt.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static String getShortDate(Date date) {
        // from java.util.Date into java.lang.String
        return new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(date);
    }

}
