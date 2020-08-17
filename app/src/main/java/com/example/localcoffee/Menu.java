package com.example.localcoffee;

/*
 This class opens and closes the menu and order databases,
 sets the recycler view, and allows the user to open the cart.
 */

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.app.AppCompatActivity;
import com.example.localcoffee.SQLiteDatabase.MenuAdapter;
import com.example.localcoffee.SQLiteDatabase.MenuDBHelper;
import com.example.localcoffee.SQLiteDatabase.MenuLayout;
import com.example.localcoffee.SQLiteDatabase.OrdersDBHelper;

public class Menu extends AppCompatActivity {
    private SQLiteDatabase menuDatabase;
    private MenuAdapter menuAdapter;
    private MenuDBHelper menuDBHelper;
    private OrdersDBHelper orderDBHelper;
    private ImageButton iButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // sets the functionality of the cart button
        iButton = findViewById(R.id.cart);
        iButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCurrentOrder();
            }
        });

        iButton = findViewById(R.id.backButton);
        iButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainScreen();
            }
        });

        // Tries to open the menu database
        menuDBHelper = new MenuDBHelper(Menu.this);
        try {
            menuDatabase = menuDBHelper.getReadableDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(Menu.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Tries to open the order database
        orderDBHelper = new OrdersDBHelper(Menu.this);
        try {
            orderDBHelper.getReadableDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(Menu.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Sets the recycler view to the data from the SQLite menu database
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        menuAdapter = new MenuAdapter(this, getAllItems());
        recyclerView.setAdapter(menuAdapter); // Passes the items from the menu database to the recycler view
    }

    // Opens the main screen
    public void openMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Opens the cart display
    public void openCurrentOrder() {
        Intent intent = new Intent(this, CurrentOrder.class);
        startActivity(intent);
    }

    // Closes the menu and order databases when the menu screen is closed
    @Override
    protected void onPause(){
        super.onPause();
        menuDBHelper.close();
        orderDBHelper.close();
        this.finish();
    }

    // Opens the databases when the screen is opened again
    @Override
    protected void onResume() {
        super.onResume();

        // Tries to open the order database
        try {
            orderDBHelper.getReadableDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(Menu.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Tries to open the menu database
        try {
            menuDBHelper.getReadableDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(Menu.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Gets the data from the menu database
    private Cursor getAllItems() {
        return menuDatabase.query(
                MenuLayout.ItemEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}