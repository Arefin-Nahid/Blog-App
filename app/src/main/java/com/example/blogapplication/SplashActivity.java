package com.example.blogapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.blogapplication.databinding.ActivitySplashBinding;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private GoogleSignInOptions signInOptions;
    private FirebaseAuth auth;
    private GoogleSignInClient signInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupSignIn();
    }

    private void setupSignIn() {
        auth = getFirebaseAuthInstance();
        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient = getGoogleSignInClientInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, DrawerActivity.class));
            finish();
        } else {
            signIn();
        }
    }

    private void signIn() {
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login Successful!!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), DrawerActivity.class));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Login Failed!!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    // Singleton pattern for FirebaseAuth
    private static FirebaseAuth firebaseAuthInstance;

    private static synchronized FirebaseAuth getFirebaseAuthInstance() {
        if (firebaseAuthInstance == null) {
            firebaseAuthInstance = FirebaseAuth.getInstance();
        }
        return firebaseAuthInstance;
    }

    // Singleton pattern for GoogleSignInClient
    private static GoogleSignInClient googleSignInClientInstance;

    private synchronized GoogleSignInClient getGoogleSignInClientInstance() {
        if (googleSignInClientInstance == null) {
            googleSignInClientInstance = GoogleSignIn.getClient(this, signInOptions);
        }
        return googleSignInClientInstance;
    }
}
