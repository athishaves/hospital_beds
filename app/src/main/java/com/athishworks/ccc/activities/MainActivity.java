package com.athishworks.ccc.activities;

// Activity for admin login
// For now this is the main activity

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.athishworks.ccc.GlobalClass;
import com.athishworks.ccc.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView skip_signin;
    Button signInButton;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        declarations();

        onClickLists();
    }


    // set on click listeners for required ui components
    private void onClickLists() {
        skip_signin.setOnClickListener(this);
        signInButton.setOnClickListener(this);
    }


    // declare all the global variables
    private void declarations() {
        skip_signin = findViewById(R.id.skip_login);
        signInButton = findViewById(R.id.button);

        firebaseAuth = FirebaseAuth.getInstance();
    }


    // onclick method
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.skip_login:
                openActivity(LocateCenter.class, true);
                break;
            case R.id.button:
//                signIn();
                // TODO : Change later
                openActivity(HospitalBeds.class, false);
                break;
        }
    }


    // signIn
    private void signIn() {

        // Checks if the mail edit text is empty or filled with spaces

        TextInputEditText email = findViewById(R.id.mail);
        if (email.getText()==null) {
            email.setError(getString(R.string.empty_message));
            GlobalClass.callAToast(MainActivity.this, getString(R.string.empty_message));
            return;
        }
        String mailText = email.getText().toString();
        if(mailText.trim().isEmpty()) {
            email.setText("");
            email.setError(getString(R.string.empty_message));
            GlobalClass.callAToast(MainActivity.this, getString(R.string.empty_message));
            return;
        }


        // Checks if the password edit text is empty or filled with spaces

        TextInputEditText pass = findViewById(R.id.pass);
        if (pass.getText()==null) {
            TextInputLayout textInputLayout = findViewById(R.id.pass_layout);
            textInputLayout.setError(getString(R.string.empty_message));
            GlobalClass.callAToast(MainActivity.this, getString(R.string.empty_message));
            return;
        }
        String passText = pass.getText().toString();
        if(passText.trim().isEmpty()) {
            pass.setText("");
            TextInputLayout textInputLayout = findViewById(R.id.pass_layout);
            textInputLayout.setError(getString(R.string.empty_message));
            GlobalClass.callAToast(MainActivity.this, getString(R.string.empty_message));
            return;
        }


        // Signs in if all the fields are non-empty

        firebaseAuth
                .signInWithEmailAndPassword(mailText, passText)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        openActivity(HospitalBeds.class, true);
                        GlobalClass.callAToast(MainActivity.this, "Signed in successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("SignIn", "Sign in failure\n" + e.getMessage());
                        GlobalClass.callAToast(MainActivity.this,
                                "Check your mail and password..");
                    }
                });

    }


    // A method which can be called whenever a new activity is to be opened
    private void openActivity(Class<?> a, boolean isFinish) {
        startActivity(new Intent(this, a));
        if (isFinish)
            finish();
    }

}