package com.athishworks.ccc.activities;

// Activity where we can update the bed counts

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.athishworks.ccc.CustomAlertBox;
import com.athishworks.ccc.CustomDialog;
import com.athishworks.ccc.GlobalClass;
import com.athishworks.ccc.R;
import com.athishworks.ccc.adapter.HospitalAdapter;
import com.athishworks.ccc.pojomodels.HospitalDetails;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HospitalBeds extends AppCompatActivity {

    RecyclerView hospitalLists;
    HospitalAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    List<HospitalDetails> input;
    List<HospitalDetails> currentInput;

    DatabaseReference databaseReference;

    EditText searchView;

    ImageView addHospital;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_beds);



        // Exits the activity if the admin is not logged in
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser()==null) {
            finish();
        }


        databaseReference = FirebaseDatabase.getInstance().getReference("hospitals");


        input = new ArrayList<>();
        currentInput = new ArrayList<>();
        initialiseRecyclerView();
        populateList();


        searchField();


        Button addCenter = findViewById(R.id.add_center);
        addCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog(new HospitalDetails());
            }
        });



        editDeleteAdapter();


        addHospital = findViewById(R.id.add_hospital);
        addHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog(new HospitalDetails());
            }
        });
        addHospital.setVisibility(View.GONE);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HospitalBeds.this, LocateCenter.class));
            }
        });

    }


    // Add edit and delete functions for adapter-listener interface

    void editDeleteAdapter() {
        mAdapter.setListener(new HospitalAdapter.OnItenClickListener() {
            @Override
            public void onEditClick(int position) {
                callDialog(currentInput.get(position));
            }


            @Override
            public void onDeleteClick(int position) {
                deleteDataRecord(currentInput.get(position).getKeyId());
            }
        });
    }


    // The function is called with empty values when add button is clicked
    // And with the values of the data when edit option is clicked

    void callDialog (HospitalDetails details) {
        CustomDialog alert = new CustomDialog(this);
        alert.showDialog(details);
    }


    // Delete test center details

    void deleteDataRecord(String hospitalId) {
        CustomAlertBox alertBox = new CustomAlertBox(this);
        alertBox.showDialog(hospitalId, "Deleted data cannot be recovered",
                "Are your sure you want to delete ?");
    }


    // Sign out automatically when you go back or the activity is destroyed

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseAuth.getInstance().signOut();
        GlobalClass.callAToast(HospitalBeds.this, "Signed out successfully");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
        GlobalClass.callAToast(HospitalBeds.this, "Signed out successfully");
    }



    // Initialise search edit text

    private void searchField() {
        searchView = findViewById(R.id.searchView);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Auto generated
            }

            @Override
            public void afterTextChanged(Editable s) {

                // If the edit text is empty then the whole list of hospitals will be retained
                if (s==null || s.toString().trim().equals("")) {
                    mAdapter.updateList(input);
                    return;
                }

                // else the list will be filtered
                filter(s.toString().toLowerCase().trim());
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
        currentInput = new ArrayList<>();

        // looping through existing elements
        for (HospitalDetails hospital : input) {

            // if the existing elements contains the search input
            if (hospital.getName().toLowerCase().contains(text) || hospital.getAddress().toLowerCase().contains(text)) {
                // adding the element to filtered list
                currentInput.add(hospital);
            }
        }

        // calling a method of the adapter class and passing the filtered list
        mAdapter.updateList(currentInput);
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

                if (input.size()==0)
                    addHospital.setVisibility(View.VISIBLE);
                else addHospital.setVisibility(View.GONE);

                mAdapter.updateList(input);
                GlobalClass.callAToast(HospitalBeds.this, "Hospitals updated");

                currentInput = input;
                searchView.setText("");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}