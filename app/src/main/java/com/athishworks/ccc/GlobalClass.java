package com.athishworks.ccc;

// Class where global methods and constants are declared

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GlobalClass {

    // Locate Center (Map) activity
    public static float iconSize = 22.5f;
    public static float zoomPreference = 11f;


    // Calling a toast
    public static void callAToast(Context context, String a) {
        Toast.makeText(context, a, Toast.LENGTH_SHORT).show();
    }


    // Get location from the give address...
    // Returns -1 if latitude, longitude is not found
    public static String getLocation(Context context, String locationAddress) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = "-1";
        try {
            List<Address> addressList = geocoder.getFromLocationName(locationAddress, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                result = address.getLatitude() + " " + address.getLongitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
