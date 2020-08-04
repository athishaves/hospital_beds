package com.athishworks.ccc;

import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.athishworks.ccc.pojomodels.HospitalDetails;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CustomDialog extends Dialog implements View.OnClickListener {

    Context context;
    Dialog dialog;

    EditText hName, hAddress, hLatitude, hLongitude, hAvailableBeds, hTotalBeds, hPhone;

    String keyId;

    Timer timer;


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

        hPhone = dialog.findViewById(R.id.care_contact);
        hPhone.setText(String.valueOf(details.getPhoneNo()));


        hAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Required
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Required
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (s==null || s.toString().trim().equals(""))
                    return;

                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Log.i("TextChange", "Run method started");
                        String latLong = getLocation(s.toString());
                        if (latLong.equals("-1"))
                            return;

                        hLatitude.setText(latLong.split(" ")[0]);
                        hLongitude.setText(latLong.split(" ")[1]);
                    }
                }, 500); // 500ms delay before the timer executes the run method from TimerTask
            }

        });

    }



    // Get location from the give address...
    // Returns -1 if latitude, longitude is not found
    String getLocation(String locationAddress) {
        Geocoder geocoder = new Geocoder(context, Locale.forLanguageTag("kn_IN"));
        String result = "-1";
        try {
            List<Address> addressList = geocoder.getFromLocationName(locationAddress, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);

                // Round of latitude and longitude to 8 decimal places
                DecimalFormat df = new DecimalFormat("0.00000000");

                result = df.format(Double.valueOf(address.getLatitude())) + " " + df.format(Double.valueOf(address.getLongitude()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public void onClick(View v) {

        if (timer!=null)
            timer.cancel();

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
                checkIfNull(hAvailableBeds) || checkIfNull(hTotalBeds) || checkIfNull(hPhone)) {

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

        // Checks if the phone number is entered properly

        if (hPhone.getText().toString().trim().length()!=10) {

            GlobalClass.callAToast(context, "Phone number is not proper");
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
                    Double.parseDouble(hLatitude.getText().toString()), Double.parseDouble(hLongitude.getText().toString()),
                    hPhone.getText().toString().trim());


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
