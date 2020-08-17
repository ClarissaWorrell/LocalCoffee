package com.example.localcoffee;

/*
 This class uses the google maps API to display the location of the coffee shop on a map in the app,
 lists the address, hours, phone number, and email,
 uses a hyperlink to to direct the user to google maps when the address is clicked.
 */

import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ContactAndFindUs extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    TextView HyperLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_and_find_us); // Sets the view to the corresponding xml file

        // Creates a hyperlink to open the google maps page of the coffee shop when clicked
        HyperLink = findViewById(R.id.addressLink);
        HyperLink.setMovementMethod(LinkMovementMethod.getInstance());

        // Adds the google maps API framework which displays the location of the coffee shop on google in the app
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Adds a marker to the map in Austin, Texas
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        LatLng austin = new LatLng(30.231440, -97.878180);
        map.addMarker(new MarkerOptions().position(austin).title("Marker in Austin for the Local Coffee Shop"));
        map.moveCamera(CameraUpdateFactory.newLatLng(austin));
        map.animateCamera(zoom);
    }
}
