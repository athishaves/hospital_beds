package com.athishworks.ccc.activities;

// Map Activity where the location of covid centers are displayed

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.athishworks.ccc.GlobalClass;
import com.athishworks.ccc.R;
import com.athishworks.ccc.adapter.CustomInfoWindow;
import com.athishworks.ccc.pojomodels.HospitalDetails;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import info.androidhive.fontawesome.FontDrawable;

public class LocateCenter extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_center);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng Bengaluru = new LatLng(12.9716, 77.5946);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Bengaluru));

        mMap.setMinZoomPreference(GlobalClass.zoomPreference);

        mMap.setInfoWindowAdapter(new CustomInfoWindow(LocateCenter.this));
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
            }
        });

        if (mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.map_style)))
            Log.i("Location", "Style updated successfully");
        else Log.e("Location", "Style couldnt be updated");

        databaseReference = FirebaseDatabase.getInstance().getReference("hospitals");

        populateList();

    }


    // Display care centers received from the database

    private void populateList() {

        FontDrawable redBed = new FontDrawable(this, R.string.fa_bed_solid, true, true);
        redBed.setTextSize(GlobalClass.iconSize);
        redBed.setTextColor(Color.RED);
        final BitmapDescriptor redDescriptor = getMarkerIconFromDrawable(redBed);


        FontDrawable greenBed = new FontDrawable(this, R.string.fa_bed_solid, true, true);
        greenBed.setTextSize(GlobalClass.iconSize);
        greenBed.setTextColor(Color.GREEN);
        final BitmapDescriptor greenDescriptor = getMarkerIconFromDrawable(greenBed);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("Data", snapshot.toString());

                for (DataSnapshot d : snapshot.getChildren()) {
                    Log.i("Data", d.toString());
                    HospitalDetails details = d.getValue(HospitalDetails.class);

                    if (details==null) {
                        Log.e("Location", "Hospital detail not found");
                        return;
                    }

                    LatLng latLng = new LatLng(details.getLatitude(), details.getLongitude());

                    String sb = details.getAddress() +
                            "\n" +
                            "Beds available : " + details.getAvailableBeds() + "/" + details.getTotalBeds();

                    MarkerOptions options = new MarkerOptions()
                            .position(latLng)
                            .title(details.getName())
                            .snippet(sb);

                    if (details.getAvailableBeds()==0)
                        options.icon(redDescriptor);
                    else options.icon(greenDescriptor);

                    mMap.addMarker(options);
                }

                GlobalClass.callAToast(LocateCenter.this, "Map updated");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    // Get icon for the care centers

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


}