package com.example.localcoffee;

/*
 This class opens and closes the order database,
 sets the recycler view, calculates the total price of the order
 and allows the user to place an order.
 */

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localcoffee.SQLiteDatabase.MenuDBHelper;
import com.example.localcoffee.SQLiteDatabase.OrderLayout;
import com.example.localcoffee.SQLiteDatabase.OrderAdapter;
import com.example.localcoffee.SQLiteDatabase.OrdersDBHelper;
import java.text.DecimalFormat;
import java.util.List;

public class CurrentOrder extends AppCompatActivity implements OrderAdapter.DisplayTotal {

    private SQLiteDatabase orderDatabase;
    private OrderAdapter orderAdapter;
    private OrdersDBHelper orderDBHelper;
    private DecimalFormat priceFormat = new DecimalFormat("0.00");
    private Button button;
    private ImageButton iButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_order);

        // Opens the main screen
        iButton = findViewById(R.id.backButton);
        iButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuScreen();
            }
        });

        // Opens the order database
        OrdersDBHelper dbHelper = new OrdersDBHelper(this);
        orderDatabase = dbHelper.getWritableDatabase();

        // Tries to open the order database
        orderDBHelper = new OrdersDBHelper(CurrentOrder.this);
        try {
            orderDBHelper.getReadableDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(CurrentOrder.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Sets the recycler view to the data from the SQLite order database
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(this, getAllItems());
        recyclerView.setAdapter(orderAdapter); // Passes the items from the order database to the recycler view

        // Button to place the order
        button = findViewById(R.id.placeOrder);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Checks if there is an item in the order
                if (DatabaseUtils.queryNumEntries(orderDatabase, OrderLayout.ItemEntry.TABLE_NAME) > 0) {
                    addOrderToFirebase(); // Starts the process of adding the current order to the Firebase database
                }
                // Tells the user to add items if the cart is empty
                else {
                    Toast.makeText(CurrentOrder.this, "Add items to the cart to place an order" , Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    // Opens the main screen
    public void openMenuScreen() {
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }

    private void addOrderToFirebase() {

        // Gets the current total from the text view
        TextView totalTextView = findViewById(R.id.totalPrice);
        String total = totalTextView.getText().toString();

        // Call placeOrder method from order recycler view adapter
        orderAdapter.placeOrder(total); // passes the total to the placeOrder method

        orderDBHelper.close();

        // Opens the thank you screen when the order is completed
        this.finish();
        openThankYouScreen();
    }

    // closes the order database when the current order page is closed
    @Override
    protected void onPause(){
        super.onPause();
        orderDBHelper.close();
    }

    // Reopens the order database when the current order page is reopened
    @Override
    protected void onResume() {
        super.onResume();
        try {
            orderDBHelper.getReadableDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(CurrentOrder.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Gets the data from the SQLite order database
    private Cursor getAllItems() {
        return orderDatabase.query(
                OrderLayout.ItemEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    // Calculates the total of the order
    @Override
    public void displayTotal(List<Double> subTotals) {
        Double total = 0.00;

        // Loops through the list of subtotals
        for (Double price : subTotals) {
            total = total + price;
        }

        // Sets the total to the text view on the current order screen
        String totalPrice = "$" + priceFormat.format(total);
        TextView totalTextView = findViewById(R.id.totalPrice);
        totalTextView.setText(totalPrice);
    }

    // Opens the thank you screen
    public void openThankYouScreen() {
        Intent intent = new Intent(this, ThankYou.class);
        startActivity(intent);
    }
}