package com.example.localcoffee;

/*
 This class displays the main screen. It has buttons for opening the navigation drawer or starting an order,
 it handles the clicks in the navigation drawer,
 and handles the logic for the SurveyMonkey survey.
 */

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.surveymonkey.surveymonkeyandroidsdk.SurveyMonkey;
import com.surveymonkey.surveymonkeyandroidsdk.utils.SMError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Variables for Survey Monkey response
    private SurveyMonkey sdkInstance = new SurveyMonkey();
    public static final int SM_REQUEST_CODE = 0;
    public static final String SM_RESPONDENT = "smRespondent";
    public static final String SM_ERROR = "smError";
    public static final String RESPONSES = "responses";
    public static final String QUESTION_ID = "question_id";
    public static final String FEEDBACK_QUESTION_ID = "813797519";
    public static final String ANSWERS = "answers";
    public static final String ROW_ID = "row_id";
    public static final String FEEDBACK_FIVE_STARS_ROW_ID = "9082377273";
    public static final String FEEDBACK_POSITIVE_ROW_ID_2 = "9082377274";
    public static final String SAMPLE_APP = "Sample App";
    public static final String SURVEY_HASH = "ZPJ2GQW";

    public DrawerLayout drawerLayout;
    public NavigationView navigationView;
    public Toolbar toolbar;
    public Intent intent;
    public String url;
    public ImageButton iButton;
    public Button button;
    public FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Starts the timer to display the survey to the user 3 days after the app has been installed
        sdkInstance.onStart(this, SAMPLE_APP, SM_REQUEST_CODE, SURVEY_HASH);

        // Sets the view for the navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        // Listens for clicks on items in the navigation drawer
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.home);
        navigationView.setItemIconTintList(null);

        // Opens the navigation drawer
        iButton = findViewById(R.id.nav_drawer);
        iButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Opens the menu to start an order
        button = findViewById(R.id.startOrder);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuScreen();
            }
        });
    }

    // Opens the menu display
    public void openMenuScreen() {
        intent = new Intent(this, Menu.class);
        startActivity(intent);
    }

    // Closes the navigation drawer
    @Override
    public void onBackPressed(){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    // Handles clicks in the navigation drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.home:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.menuButton :
                intent = new Intent(this, Menu.class);
                startActivity(intent);
                break;
            case R.id.cart :
                intent = new Intent(this, CurrentOrder.class);
                startActivity(intent);
                break;
            case R.id.pastOrders :
                // Checks if user is already logged in before opening past orders screen
                currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    Intent intent = new Intent(this, PastOrders.class);
                    startActivity(intent);
                    break;
                }
                else {
                    Toast.makeText(this, "Login to see past orders", Toast.LENGTH_SHORT).show();
                    break;
                }
            case R.id.login :
                // Checks if user is already logged in before opening login screen
                currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    intent = new Intent(this, LoginScreen.class);
                    startActivity(intent);
                    break;
                }
                else {
                    Toast.makeText(this, "User already logged in", Toast.LENGTH_SHORT).show();
                    break;
                }
            case R.id.contactOrFindUs :
                intent = new Intent(this, ContactAndFindUs.class);
                startActivity(intent);
                break;
            case R.id.ourStory :
                intent = new Intent(this, AboutUs.class);
                startActivity(intent);
                break;
            case R.id.facebookIntegration :
                intent = new Intent(this, FacebookIntegration.class);
                startActivity(intent);
                break;
            case R.id.settings :
                intent = new Intent(this, Settings.class);
                startActivity(intent);
                break;
            case R.id.feedback:
                Toast.makeText(this, "Opening the survey!", Toast.LENGTH_LONG).show();
                sdkInstance.startSMFeedbackActivityForResult(this, SM_REQUEST_CODE, SURVEY_HASH);
                break;
            case R.id.facebook :
                url = "https://www.facebook.com/";
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                break;
            case R.id.instagram :
                url = "https://www.instagram.com/";
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                break;
            case R.id.twitter :
                url = "https://twitter.com/";
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                break;
            default:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
        }
        // Closes the drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Starts survey monkey
    @Override
    protected void onStart() {
        super.onStart();
    }

    // Collects the response data from the survey
    // This code is directly from the Survey Monkey Android SDK instructions
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            boolean isPromoter = false;
            try {
                String respondent = intent.getStringExtra(SM_RESPONDENT);
                Log.d("SM", respondent);
                JSONObject surveyResponse = new JSONObject(respondent);
                JSONArray responsesList = surveyResponse.getJSONArray(RESPONSES);
                JSONObject response;
                JSONArray answers;
                JSONObject currentAnswer;
                for (int i = 0; i < responsesList.length(); i++) {
                    response = responsesList.getJSONObject(i);
                    if (response.getString(QUESTION_ID).equals(FEEDBACK_QUESTION_ID)) {
                        answers = response.getJSONArray(ANSWERS);
                        for (int j = 0; j < answers.length(); j++) {
                            currentAnswer = answers.getJSONObject(j);
                            if (currentAnswer.getString(ROW_ID).equals(FEEDBACK_FIVE_STARS_ROW_ID) || currentAnswer.getString(ROW_ID).equals(FEEDBACK_POSITIVE_ROW_ID_2)) {
                                isPromoter = true;
                                break;
                            }
                        }
                        if (isPromoter) {
                            break;
                        }
                    }
                }
            } catch (JSONException e) {
                Log.getStackTraceString(e);
            }
            if (isPromoter) {
                Toast t = Toast.makeText(this, "is promoter", Toast.LENGTH_LONG);
                t.show();
            } else {
                Toast t = Toast.makeText(this, "detractor promoter", Toast.LENGTH_LONG);
                t.show();
            }
        }
        else {
            SMError e = (SMError) intent.getSerializableExtra(SM_ERROR);
            Log.d("SM-ERROR", e.getDescription());
        }
    }

    // Stops the survey
    @Override
    protected void onStop() {
        super.onStop();
    }

    // Closes the survey when the user has completed it
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
}
