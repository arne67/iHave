package com.susarne.ihave2.util;


import android.content.Context;
import android.util.DisplayMetrics;

import com.susarne.ihave2.R;
import com.susarne.ihave2.app.MyApplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Utility {
    public static String getCurrentTimestamp(){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date());
            return currentDateTime;
        } catch (Exception e){
            return null;
        }
    }
    public static String getMonthFromNumber(String monthNumber){
        switch (monthNumber){
            case "01":{
                return "Jan";
            }
            case "02":{
                return "Feb";
            }
            case "03":{
                return "Mar";
            }
            case "04":{
                return "Apr";
            }
            case "05":{
                return "May";
            }
            case "06":{
                return "Jun";
            }
            case "07":{
                return "Jul";
            }
            case "08":{
                return "Aug";
            }
            case "09":{
                return "Sep";
            }
            case "10":{
                return "Oct";
            }
            case "11":{
                return "Nov";
            }
            case "12":{
                return "Dec";
            }
            default:{
                return "Error";
            }

        }
    }
    public static String getIhaveCloudBaseUrl(){
        Context context= MyApplication.getAppContext();
        return context.getResources().getString(R.string.ihaveCloudBaseUrl);
    }

    public static String getCloudImageFolder(){
        Context context=MyApplication.getAppContext();
        return context.getResources().getString(R.string.ihaveCloudImageFolder);
    }

    public static String getUuid(){
        UUID uuid = UUID.randomUUID();

        // Konverter UUID til en streng
        String uuidString = uuid.toString();
        return uuidString;
    }

    public static int convertDpToPixel(float dp) {
        Context context=MyApplication.getAppContext();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (dp * (metrics.densityDpi / 160f));
    }
}
