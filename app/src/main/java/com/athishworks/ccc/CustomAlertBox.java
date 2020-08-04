package com.athishworks.ccc;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomAlertBox extends Dialog {

    Context context;
    Dialog dialog;


    public CustomAlertBox(@NonNull Context context) {
        super(context);
        this.context = context;
    }


    // Display the dialog box

    public void showDialog (final String hospitalId, String title, String message) {

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_alert_box);
        dialog.setCancelable(true);
        dialog.show();



        TextView titleTV = dialog.findViewById(R.id.title);
        titleTV.setText(title);

        TextView messageTV = dialog.findViewById(R.id.message);
        messageTV.setText(message);

        Button okButton = dialog.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDatabase(hospitalId);
                dialog.dismiss();
            }
        });

        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    // Delete record

    private void deleteDatabase(String hospitalId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("hospitals");
        databaseReference.child(hospitalId).removeValue();
    }

}
