package com.athishworks.ccc;

// Class where global methods and constants are declared

import android.content.Context;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

public class GlobalClass {

    // Calling a toast
    public static void callAToast(Context context, String a) {
        Toast.makeText(context, a, Toast.LENGTH_SHORT).show();
    }

}
