package com.athishworks.ccc;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.athishworks.ccc.pojomodels.HospitalDetails;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomDialog extends Dialog implements View.OnClickListener {

    Context context;
    Dialog dialog;

    EditText hName, hAddress, hLatitude, hLongitude, hAvailableBeds, hTotalBeds;

    String keyId;


    public CustomDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }


    // Display the dialog box

    public void showDialog (HospitalDetails details) {

        if (details.getKeyId()!=null)
            keyId = details.getKeyId();


        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        int DeviceTotalWidth = metrics.widthPixels;
        int DeviceTotalHeight = 3*metrics.heightPixels/5;

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(DeviceTotalWidth, DeviceTotalHeight);
        dialog.show();


        Button okButton = dialog.findViewById(R.id.ok_button);
        okButton.setOnClickListener(this);

        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(this);

        setValues(details);

        dialog.show();

    }


    // Set the provided values to the respective edittexts

    private void setValues(HospitalDetails details) {

        hName = dialog.findViewById(R.id.care_name);
        hName.setText(details.getName());
        hName.requestFocus();

        hAddress = dialog.findViewById(R.id.care_address);
        hAddress.setText(details.getAddress());

        hLatitude = dialog.findViewById(R.id.latitude);
        hLatitude.setText(String.valueOf(details.getLatitude()));

        hLongitude = dialog.findViewById(R.id.longitude);
        hLongitude.setText(String.valueOf(details.getLongitude()));

        hAvailableBeds = dialog.findViewById(R.id.available_beds);
        hAvailableBeds.setText(String.valueOf(details.getAvailableBeds()));

        hTotalBeds = dialog.findViewById(R.id.total_beds);
        hTotalBeds.setText(String.valueOf(details.getTotalBeds()));


        hAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Required
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String latLong = GlobalClass.getLocation(context, s.toString());
                if (latLong.equals("-1"))
                    return;

                hLatitude.setText(latLong.split(" ")[0]);
                hLongitude.setText(latLong.split(" ")[1]);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Required
            }
        });

    }


    @Override
    public void onClick(View v) {

        // ok button is clicked
        if (v.getId()==R.id.ok_button)
            updateDatabase();

        // cancel button is clicked
        else dialog.dismiss();

    }


    private void updateDatabase() {

        // Checks if all the input fields are filled with respective values or not

        if (checkIfNull(hName) || checkIfNull(hAddress) ||
                checkIfNull(hLatitude) || checkIfNull(hLongitude) ||
                checkIfNull(hAvailableBeds) || checkIfNull(hTotalBeds)) {

                   GlobalClass.callAToast(context, context.getString(R.string.empty_message));
                   return;
        }

        // Checks if the available and total bed counts are valid or not

        if (Integer.parseInt(hAvailableBeds.getText().toString()) > Integer.parseInt(hTotalBeds.getText().toString())
                || Integer.parseInt(hAvailableBeds.getText().toString())<0
                || Integer.parseInt(hTotalBeds.getText().toString())<0) {

            GlobalClass.callAToast(context, "Enter the values properly");
            return;
        }


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("hospitals");


        // keyId will be equal to null only when the dialog is called to add a hospital

        if (keyId==null)
            keyId = databaseReference.push().getKey();

        // creating hospital object

        HospitalDetails hospitalDetails = new HospitalDetails(
                hName.getText().toString().trim(), hAddress.getText().toString().trim(),
                    Integer.parseInt(hTotalBeds.getText().toString()), Integer.parseInt(hAvailableBeds.getText().toString()),
                    Double.parseDouble(hLatitude.getText().toString()), Double.parseDouble(hLongitude.getText().toString()));


        // updating the database

        if (keyId != null) {
            databaseReference.child(keyId).setValue(hospitalDetails);
        }

        dialog.dismiss();

    }


    // Checks if the given edittext is filled with values or not

    private boolean checkIfNull(EditText editText) {
        Editable editable = editText.getText();
        return editable == null || editable.toString().trim().equals("");
    }

}
