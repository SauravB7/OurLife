package com.saurav.ourlife.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.saurav.ourlife.R;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FingerprintAuthenticationActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_authentication);

        autheticateLogin();
    }

    private void autheticateLogin() {
        //get instance of autheticator
        final BiometricPrompt myBiometricPrompt = createAutheticator();

        //Create the BiometricPrompt instance//
        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Sign In")
                .setSubtitle("Authenticate to Continue")
                .setDescription("Place your finger on fingerprint sensor for authentication")
                .setNegativeButtonText("Cancel")
                .build();

        myBiometricPrompt.authenticate(promptInfo);
    }

    private BiometricPrompt createAutheticator() {
        Executor newExecutor = Executors.newSingleThreadExecutor();
        FragmentActivity activity = this;

        final BiometricPrompt myBiometricPrompt = new BiometricPrompt(activity, newExecutor, new BiometricPrompt.AuthenticationCallback() {
            @Override

            //onAuthenticationError is called when a fatal error occurrs//
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                } else {
                    Log.d(TAG, "An unrecoverable error occurred");
                }
            }

            //onAuthenticationSucceeded is called when a fingerprint is matched successfully//
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Log.d(TAG, "Fingerprint recognised successfully");
                openHomeActivity(getApplicationContext());
            }

            //onAuthenticationFailed is called when the fingerprint doesn’t match//
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.d(TAG, "Fingerprint not recognised");
            }
        });

        return myBiometricPrompt;
    }

    private void openHomeActivity(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        startActivity(intent);
    }

}