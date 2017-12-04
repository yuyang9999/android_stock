package com.naughtypiggy.android.stock;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * Created by yangyu on 3/12/17.
 */

public class MyApplication extends Application {
    static private WeakReference<Context> mContext;

    public void onCreate() {
        super.onCreate();
        mContext = new WeakReference<Context>(this);
    }

    public static Context getContext() {
        return mContext.get();
    }

}
