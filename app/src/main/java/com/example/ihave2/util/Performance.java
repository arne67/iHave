package com.example.ihave2.util;

import android.util.Log;

public class Performance {
    private static long mTimeLast;
    private static final String TAG = "Performance";

    public static void speed(String tag,int id) {
        long timeNow=System.currentTimeMillis();
        long timePassed=timeNow-mTimeLast;
        mTimeLast=timeNow;
        Log.d(TAG, "speed: "+tag+" timedpassed: "+id+"  "+timePassed);
    }
}
