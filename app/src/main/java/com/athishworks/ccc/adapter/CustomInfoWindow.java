package com.athishworks.ccc.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.athishworks.ccc.GlobalClass;
import com.athishworks.ccc.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    Context context;

    public CustomInfoWindow(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoContents(Marker marker) {

        View myContentsView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_info_window, null, false);

        TextView tvTitle = myContentsView.findViewById(R.id.name);
        tvTitle.setText(marker.getTitle());

        String[] details = marker.getSnippet().split(GlobalClass.splitCondition);

        TextView tvAddress = myContentsView.findViewById(R.id.address);
        tvAddress.setText(details[0]);

        TextView tvBedCount = myContentsView.findViewById(R.id.bed_count);
        tvBedCount.setText(Html.fromHtml(details[1]));

        TextView tvPhone = myContentsView.findViewById(R.id.phone);
        String a = "Contact : " + details[2];
        tvPhone.setText(a);

        return myContentsView;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // Auto-generated
        return null;
    }
}