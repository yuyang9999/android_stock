package com.naughtypiggy.android.stock.utility;

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
}
