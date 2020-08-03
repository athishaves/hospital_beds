package com.athishworks.ccc.activities;

// Activity where we can update the bed counts

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.athishworks.ccc.CustomDialog;
import com.athishworks.ccc.GlobalClass;
import com.athishworks.ccc.R;
import com.athishworks.ccc.adapter.HospitalAdapter;
import com.athishworks.ccc.pojomodels.HospitalDetails;
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

    DatabaseReference databaseReference;

    EditText searchView;

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

    }


    // Add edit and delete functions for adapter-listener interface

    void editDeleteAdapter() {
        mAdapter.setListener(new HospitalAdapter.OnItenClickListener() {
            @Override
            public void onEditClick(int position) {
                callDialog(input.get(position));
            }


            @Override
            public void onDeleteClick(int position) {
                deleteDataRecord(input.get(position).getKeyId());
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

    void deleteDataRecord(final String hospitalId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete ?")
                .setTitle("Deleted data cannot be recovered")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        databaseReference.child(hospitalId).removeValue();
                        searchView.setText("");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        builder.show();
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


}