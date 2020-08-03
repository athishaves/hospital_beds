package com.athishworks.ccc;

// Class where global methods and constants are declared

import android.content.Context;
import android.widget.Toast;

public class GlobalClass {

    // Split character for custom info window
    public static String splitCondition = "---";


    // Location permission
    public static int locationPermission = 999;


    // Locate Center (Map) activity
    public static float iconSize = 22.5f;
    public static float zoomPreference = 7f;


    // Calling a toast
    public static void callAToast(Context context, String a) {
        Toast.makeText(context, a, Toast.LENGTH_SHORT).show();
    }

}
