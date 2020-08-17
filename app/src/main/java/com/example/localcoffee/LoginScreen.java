package com.example.localcoffee;

/*
 Allows the user to login using Facebook or Google and authenticates the login through Firebase
*/

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginScreen extends AppCompatActivity {

    private static final String TAG = "Login Activity";
    private CallbackManager mCallbackManager;
    private FirebaseAuth mFirebaseAuth;
    private Button button;
    private GoogleSignInClient mGoogleSignInClient;
    private Integer RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // Gets the view of the Facebook Login button
        LoginButton loginButton = findViewById(R.id.login_button);

        // Handles clicks on the Google sign in button
        findViewById(R.id.google_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        // Starts the Firebase authentication
        mFirebaseAuth = FirebaseAuth.getInstance();

        // Sets the permissions of the app from the user's Google account
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Allows the user to continue as a guest and not store their orders
        button = findViewById(R.id.continueAsGuest);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainScreen();
            }
        });

        // Handles the user clicking the logout facebook button
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    // Logs the user out of Firebase
                    FirebaseAuth.getInstance().signOut();
                }
                else {
                    return;
                }
            }
        };

        // Creates a callback manager
        mCallbackManager = CallbackManager.Factory.create();

        // Sets the permissions of the app from the user's Facebook account
        loginButton.setPermissions("email");

        // Registers callback
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Signed in
                Log.i(TAG, "onSuccess: logged in successfully");

                // Handles token for Firebase Authenication
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            // Cancels the Facebook login attempt
            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            // Handles an error with the Facebook Login
            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

        // Starts tracking if a user is logged in to Facebook
        accessTokenTracker.startTracking();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Checks if the user is signed in
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        // Opens the main screen if a user is already logged in
        if (currentUser != null) {
            openMainScreen();
        }
        // Continues with the login process
        else {
            return;
        }
    }

    // Signs the user into Google
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleFacebookAccessToken(AccessToken token) {

        Toast.makeText(LoginScreen.this, "Signing in, Please wait.", Toast.LENGTH_LONG).show();

        // Tries to authenticate the Facebook login using Firebase
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // Opens the main screen if the user authentication was successful
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            openMainScreen();
                        }
                        else {
                            // Handles if the user authentication failed
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginScreen.this, "Sign in failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(String idToken) {

        Toast.makeText(LoginScreen.this, "Signing in, Please wait.", Toast.LENGTH_LONG).show();

        // Tries to authenticate the Google login using Firebase
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // Opens the main screen if the user authentication was successful
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            openMainScreen();
                        }
                        else {
                            // Handles if the user authentication failed
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginScreen.this, "Sign in failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handles the user logging in and out of Facebook
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Handles the user logging into Google
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            // Google login successful
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken()); // Calls to authenticate the user with Firebase
            }

            // Handles Google login error
            catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    // Opens the main screen
    public void openMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}