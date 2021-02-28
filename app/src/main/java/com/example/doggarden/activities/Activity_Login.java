package com.example.doggarden.activities;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.doggarden.utils.MyLocation;
import com.example.doggarden.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Activity_Login extends BaseActivity {
    private enum LOGIN_STATE {
        ENTERING_NUMBER,
        ENTERING_CODE,
    }

    private LOGIN_STATE login_state = LOGIN_STATE.ENTERING_NUMBER;

    MaterialButton main_BTN_continue;
    TextInputLayout main_EDT_phone;
    FirebaseAuth mAuth;
    private MyLocation myLocation;
    private String phoneInput = "";
    private String smsVerificationCode = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__login);

        mAuth = FirebaseAuth.getInstance();

        findViews();
        initViews();
        updateUI();


    }


    private void initViews() {

        main_BTN_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueClicked();
            }
        });

    }

    private void continueClicked() {
        if (login_state == LOGIN_STATE.ENTERING_NUMBER) {
            sendVerificationCode();
        } else if (login_state == LOGIN_STATE.ENTERING_CODE) {
            verifySingInCode();
        }
    }

    private void verifySingInCode() {
        String code = main_EDT_phone.getEditText().getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(smsVerificationCode, code);
        signInWithPhoneAuthCredential(credential);
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //open new activity
                            Toast.makeText(Activity_Login.this, "Login Success", Toast.LENGTH_LONG).show();
                            openMainActivity();
                            finish();

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(Activity_Login.this, "verification invalid", Toast.LENGTH_LONG).show();
                                //updateUI();
                            }

                        }
                    }
                });
    }

    private void openMainActivity() {
        Intent myIntent = new Intent(Activity_Login.this, Activity_Main.class);
        startActivity(myIntent);
        finish();
    }


    private void sendVerificationCode() {

        phoneInput = main_EDT_phone.getEditText().getText().toString();

        if (phoneInput.isEmpty()) {
            main_EDT_phone.setError("Phone number is required");
            main_EDT_phone.requestFocus();
            return;
        }

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneInput)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(onVerificationStateChangedCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks onVerificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            login_state = LOGIN_STATE.ENTERING_CODE;
            smsVerificationCode = verificationId;
            updateUI();
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            super.onCodeAutoRetrievalTimeOut(s);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            e.printStackTrace();
            Toast.makeText(Activity_Login.this, "VerificationFailed ", Toast.LENGTH_SHORT).show();
            login_state = LOGIN_STATE.ENTERING_NUMBER;
            updateUI();
        }
    };

    private void updateUI() {
        if (login_state == LOGIN_STATE.ENTERING_NUMBER) {
            main_EDT_phone.getEditText().setText("");
            main_EDT_phone.setHint(getString(R.string.phone_number));
            main_EDT_phone.setPlaceholderText("+972 54-000-0000");
            main_BTN_continue.setText(getString(R.string.continue_));
        } else if (login_state == LOGIN_STATE.ENTERING_CODE) {
            main_EDT_phone.getEditText().setText("");
            main_EDT_phone.setHint(getString(R.string.enter_code));
            main_EDT_phone.setPlaceholderText("******");
            main_BTN_continue.setText(getString(R.string.login));
        }
    }

    private void findViews() {
        main_BTN_continue = findViewById(R.id.main_BTN_continue);
        main_EDT_phone = findViewById(R.id.main_EDT_phone);
    }

}