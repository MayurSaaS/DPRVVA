package com.vvautotest.utils;

import android.util.Log;

public class L {

    public static void printInfo(String msg){
        Log.i("Info", msg);
    }
    public static void printError(String msg){
        Log.e("Error", msg);
    }
    public static void printWarning(String msg){
        Log.w("Warning", msg);
    }

}
