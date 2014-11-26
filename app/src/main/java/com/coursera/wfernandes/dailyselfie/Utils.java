package com.coursera.wfernandes.dailyselfie;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Utils {

    private static final String TAG = "UTILS";

    protected static String getReadableSelfieName(String selfieName) {
        String[] split = selfieName.split("_");
        String date = split[1];
        String time = split[2];
        String dateTime = date + time;
        String format = "yyyyMMddHHmmss";

        try {
            return DateFormat.getDateTimeInstance().format(new SimpleDateFormat(format).parse(dateTime));
        } catch (ParseException pe) {
            Log.e(TAG, "Unable to parse date from selfie name", pe);
            return selfieName;
        }
    }
}
