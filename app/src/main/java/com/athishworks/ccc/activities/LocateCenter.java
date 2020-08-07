package com.athishworks.ccc.activities;

// Map Activity where the location of covid centers are displayed

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
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

        if (checkLocationPermission()) {
            locationIsTurnedOn();

        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    GlobalClass.locationPermission);
        }


        mMap.setMinZoomPreference(GlobalClass.zoomPreference);

        mMap.setInfoWindowAdapter(new CustomInfoWindow(LocateCenter.this));

        if (mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.map_style)))
            Log.i("Location", "Style updated successfully");
        else Log.e("Location", "Style couldn't be updated");

        databaseReference = FirebaseDatabase.getInstance().getReference("hospitals");

        populateList();


        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
                String[] details = marker.getSnippet().split(GlobalClass.splitCondition);
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String p = "tel:" + details[2];
                intent.setData(Uri.parse(p));
                startActivity(intent);
            }
        });

    }


    // Check if location permissions are granted

    boolean checkLocationPermission() {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==GlobalClass.locationPermission) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationIsTurnedOn();
            } else {
                GlobalClass.callAToast(LocateCenter.this, "Current location is set to Bengaluru");
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.9716, 77.5946),
                        GlobalClass.zoomPreference + 4));
            }
        }
    }


    // Method which moves the camera to the center of the user's location

    private void locationIsTurnedOn() {
        if (!checkLocationPermission())
            return;

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.9716, 77.5946),
                GlobalClass.zoomPreference + 6));

        mMap.setMyLocationEnabled(true);
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location!=null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),
                    GlobalClass.zoomPreference + 6));
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
                            GlobalClass.splitCondition +
                            "Beds available : " + "<b>" + details.getAvailableBeds() + "</b>" + "/" + details.getTotalBeds() +
                            GlobalClass.splitCondition +
                            details.getPhoneNo();

                    MarkerOptions options = new MarkerOptions()
                            .position(latLng)
                            .title(details.getName())
                            .snippet(sb);

                    if (details.getAvailableBeds()==0)
                        options.icon(redDescriptor);
                    else options.icon(greenDescriptor);

                    mMap.addMarker(options);
                }
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