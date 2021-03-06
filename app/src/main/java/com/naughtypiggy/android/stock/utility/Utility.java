package com.naughtypiggy.android.stock.utility;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;

/**
 * Created by yangyu on 9/12/17.
 */

public class Utility {
    public static String gsonObject(Object obj) {
        Gson gson = new Gson();
        String ret = gson.toJson(obj);
        return ret;
    }

    public static Object ungsonObject(String objStr, Class cls) {
        Gson gson = new Gson();
        Object ret = gson.fromJson(objStr, cls);
        return ret;
    }

    public static void showToastText(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
