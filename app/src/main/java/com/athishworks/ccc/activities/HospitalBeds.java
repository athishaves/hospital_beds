package com.athishworks.ccc.activities;

// Activity where we can update the bed counts

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.athishworks.ccc.GlobalClass;
import com.athishworks.ccc.R;
import com.athishworks.ccc.adapter.HospitalAdapter;
import com.athishworks.ccc.pojomodels.HospitalDetails;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HospitalBeds extends AppCompatActivity implements View.OnClickListener {

    RecyclerView hospitalLists;
    HospitalAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    List<HospitalDetails> input;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_beds);


        // TODO : Uncomment later !!!

        // Exits the activity if the admin is not logged in
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        if (auth.getCurrentUser()==null) {
//            finish();
//        }


        databaseReference = FirebaseDatabase.getInstance().getReference("hospitals");


        input = new ArrayList<>();
        initialiseRecyclerView();
        populateList();


        searchField();


        Button saveChanges = findViewById(R.id.save_changes);
        saveChanges.setOnClickListener(this);
        Button addCenter = findViewById(R.id.add_center);
        addCenter.setOnClickListener(this);



        editDeleteAdapter();

    }


    // Add edit and delete functions for adapter-listener interface

    private void editDeleteAdapter() {
        mAdapter.setListener(new HospitalAdapter.OnItenClickListener() {
            @Override
            public void onEditClick(int position) {
                updateDatabase(input.get(position).getKeyId());
            }


            @Override
            public void onDeleteClick(int position) {
                deleteDataRecord(input.get(position).getKeyId());
            }
        });
    }


    // Update database having known the keyId of the node

    void updateDatabase(String hospitalId) {
        String address = "1600 Pennsylvania Ave NW Washington DC 20502";
        String latlong = getLocation(address);

        LatLng latLng;

        if (latlong.equals("-1")) {
            latLng = new LatLng(20.00, 10.2);
            Log.i("Location", "couldnt get latlng");
        }

        else {
            String[] z = latlong.split(" ");
            latLng = new LatLng(Double.parseDouble(z[0]), Double.parseDouble(z[1]));
        }


        // creating hospital object

        HospitalDetails hospitalDetails = new HospitalDetails(
                "Mallya Hospital", address, 0,
                -10, latLng.latitude, latLng.longitude);


        // pushing user to 'users' node using the userId
        databaseReference.child(hospitalId).setValue(hospitalDetails);
    }


    // Delete test center details

    void deleteDataRecord(String hospitalId) {
        databaseReference.child(hospitalId).removeValue();
    }


    // Initialise search edit text

    private void searchField() {
        EditText searchView = findViewById(R.id.searchView);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Auto generated
            }

            @Override
            public void afterTextChanged(Editable s) {

                // If the edit text is empty then the whole list of hospitals will be retained
                if (s.toString().equals("")) {
                    mAdapter.updateList(input);
                    return;
                }

                // else the list will be filtered
                filter(s.toString().toLowerCase());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Auto-generated
            }
        });

    }


    // Filter the hospital list with provided search string

    private void filter(String text) {

        if (input==null || input.size()==0)
            return;

        // new array list that will hold the filtered data
        List<HospitalDetails> filterdNames = new ArrayList<>();

        // looping through existing elements
        for (HospitalDetails hospital : input) {

            // if the existing elements contains the search input
            if (hospital.getName().toLowerCase().contains(text) || hospital.getAddress().toLowerCase().contains(text)) {
                // adding the element to filtered list
                filterdNames.add(hospital);
            }
        }

        // calling a method of the adapter class and passing the filtered list
        mAdapter.updateList(filterdNames);
    }


    // declare and populate

    private void initialiseRecyclerView() {
        hospitalLists = findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        hospitalLists.setLayoutManager(layoutManager);

        // set adapter for recycler view

        mAdapter = new HospitalAdapter(input, this);
        hospitalLists.setAdapter(mAdapter);
    }


    // Populate the list

    private void populateList() {
        input = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("Data", snapshot.toString());
                if (input.size()!=0)
                    input.clear();

                for (DataSnapshot d : snapshot.getChildren()) {
                    Log.i("Data", d.toString());
                    HospitalDetails details = d.getValue(HospitalDetails.class);

                    String key = d.getKey();

                    if (details!=null)
                        details.setKeyId(key);

                    input.add(details);
                }

                mAdapter.updateList(input);
                GlobalClass.callAToast(HospitalBeds.this, "Hospitals updated");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_center:
                addHospital();
                break;

            case R.id.save_changes:
                break;
        }
    }


    // Adds a test center to the database

    private void addHospital() {

        // Creating new node, which returns the unique key value
        String hospitalId = databaseReference.push().getKey();


        // Get latitude and longitude with address provided

        String address = "1600 Pennsylvania Ave NW Washington DC 20502";
        String latlong = getLocation(address);

        LatLng latLng;

        if (latlong.equals("-1")) {
            latLng = new LatLng(20.00, 10.2);
            Log.i("Location", "couldnt get latlng");
        }

        else {
            Log.i("Location", "result " + latlong);
            String[] z = latlong.split(" ");
            Log.i("Location", "length " + z.length);

            latLng = new LatLng(Double.parseDouble(z[0]), Double.parseDouble(z[1]));
            Log.i("Location", "Lat : " + z[0] + " Long : " + z[1]);
        }


        // creating hospital object

        HospitalDetails hospitalDetails = new HospitalDetails(
                "Mallya Hospital", address, 0,
                    -10, latLng.latitude, latLng.longitude);


        // pushing user to 'users' node using the userId
        if (hospitalId != null) {
            databaseReference.child(hospitalId).setValue(hospitalDetails);
        }
    }


    // Get location from the give address...
    // Returns -1 if latitude, longitude is not found

    String getLocation(String locationAddress) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
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