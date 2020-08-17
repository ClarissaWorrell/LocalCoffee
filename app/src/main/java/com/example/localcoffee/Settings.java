package com.example.localcoffee;

/*
 This class holds the settings for the app.
 It also the user to logout or delete their account.
 */

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Settings extends AppCompatActivity {

    private Button button;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Logout button
        button = findViewById(R.id.logoutButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        // Delete account button
        button = findViewById(R.id.deleteAccountButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccount();
            }
        });
    }

    // Opens the menu display
    public void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    // Opens the menu display
    public void deleteAccount() {

        // Gets current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Checks if a user is logged in
        if (currentUser != null) {
            // Gets the current user's ID
            String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            mDatabase = FirebaseDatabase.getInstance().getReference();
            // Deletes the users data from the database
            DatabaseReference userAccount = mDatabase.child("Users").child(Uid);
            userAccount.removeValue();

            // Deletes the user in the Firebase authentication
            currentUser.delete();
            return;
        }
        // If the user is null, then it doesn't do anything
        else {
            return;
        }
    }
}
