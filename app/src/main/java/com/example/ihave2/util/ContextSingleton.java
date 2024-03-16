package com.example.ihave2.util;

import android.content.Context;

public class ContextSingleton {
    private static ContextSingleton instance;
    private final Context mContext;

    public static ContextSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new ContextSingleton(context.getApplicationContext());
        }

        return instance;
    }
    private ContextSingleton(Context context) {
        mContext = context;
    }
    public static Context getContekst(){
        return instance.mContext;
    }
}
