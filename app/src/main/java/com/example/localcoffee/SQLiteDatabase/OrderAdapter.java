package com.example.localcoffee.SQLiteDatabase;

/*
 Displays the items from the current order database in the recyclerView,
 removes an item and updates the total when the user clicks the remove button,
 and adds the current order to the Firebase database.
 */

import android.content.Context;
import android.database.Cursor;
import androidx.recyclerview.widget.RecyclerView;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.localcoffee.Order;
import com.example.localcoffee.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ItemHolder> {

    private Context mContext;
    private Cursor mCursor;
    private DisplayTotal adapterCallback;
    private OrdersDBHelper oDBHelper;
    private SQLiteDatabase db;
    private DatabaseReference mDatabase;
    int rowsDeleted;
    public List<String> itemNames = new ArrayList<>();
    public List<Integer> itemQuantities = new ArrayList<>();
    public List<Double> subTotals = new ArrayList<>();
    public DecimalFormat priceFormat = new DecimalFormat("#.00");

    public OrderAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;

        // Tries to access the DisplayTotal method from the current order class
        try {
            adapterCallback = ((DisplayTotal) context);
        } catch (ClassCastException e) {
            //throw new ClassCastException(e);
        }

        // Opens the order database
        oDBHelper = new OrdersDBHelper(mContext);
        try {
            oDBHelper.getReadableDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Sets the layout of the recycler view items
    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.order_item, parent, false);
        return new ItemHolder(view);
    }

    // Sets the data of the recycler view
    @Override
    public void onBindViewHolder(ItemHolder holder, final int position) {

        // Stops looking for data when the last item in the menu database is displayed
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        // Gets the name, price, and quantity from the order database at the current row
        String name = mCursor.getString(mCursor.getColumnIndex(OrderLayout.ItemEntry.COLUMN_NAME));
        Double price = mCursor.getDouble(mCursor.getColumnIndex(OrderLayout.ItemEntry.COLUMN_PRICE));
        int qty = mCursor.getInt(mCursor.getColumnIndex(OrderLayout.ItemEntry.COLUMN_QUANTITY));

        // Adds the items to the name and quantities lists
        itemNames.add(name);
        itemQuantities.add(qty);

        // Calculates the subtotal
        Double subPrice = price * qty;
        subTotals.add(subPrice); // Adds the subtotal to the list
        adapterCallback.displayTotal(subTotals); // Calls the displayTotal method to recalculates the total of the order
        String itemSubTotal = "$" + priceFormat.format(subPrice);

        // Displays the data of each item in the recycler view
        holder.nameText.setText(name);
        holder.priceText.setText(itemSubTotal);
        holder.quantityText.setText(Integer.toString(qty));

        // Deletes the current item from the order database
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mCursor.moveToPosition(position);

                // Opens the order database
                db = oDBHelper.getWritableDatabase();

                // Gets the name of the current item to delete
                String name = mCursor.getString(mCursor.getColumnIndex(OrderLayout.ItemEntry.COLUMN_NAME));
                String selection = OrderLayout.ItemEntry.COLUMN_NAME + " = ?";
                String[] selectionArgs = {name};

                // Deletes the item
                rowsDeleted = db.delete(OrderLayout.ItemEntry.TABLE_NAME, selection, selectionArgs);

                // Updates the recycler view
                notifyItemRemoved(position);
                swapCursor(getAllItems());

                // Clears the lists so that duplicates are not added when the recycler view updates
                itemNames.clear();
                itemQuantities.clear();
                subTotals.clear();
                adapterCallback.displayTotal(subTotals);
            }
        });
    }

    // Calls the displayTotal method from the Current Order file
    public interface DisplayTotal {
        void displayTotal(List<Double> subtotals);
    }

    public void placeOrder(String total) {

        // Calls the method to add an order to the Firebase database
        addOrderToFirebase(total);

        // Opens the current order database and deletes the data from the table
        db = oDBHelper.getWritableDatabase();
        db.delete(OrderLayout.ItemEntry.TABLE_NAME, null, null);

        // Clears the lists so a new order can be started
        itemNames.clear();
        itemQuantities.clear();

        // Resets the total of the current order to 0.00
        subTotals.clear();
        adapterCallback.displayTotal(subTotals);
    }

    public void addOrderToFirebase(String total) {

        // Gets the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Inserts the order under the current user's profile in the database
        if (currentUser != null) {
            String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference(); // Opens the Fireabase database

            // Maps the items to be inserted into the Firebase database
            Order order = new Order(itemNames, itemQuantities, subTotals, total);
            Map<String, Object> orderValues = order.toMap();

            DatabaseReference mRef =  mDatabase.child("Users").child(Uid).child("Orders").push(); // Gets the location to add the order to
            mRef.setValue(orderValues); // Adds the order
            return;
        }
        // If the user is null, then it doesn't add the order to the database
        else {
            return;
        }
    }

    // Gets the number of rows in the Order database
    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    // Sets the variables and the views of the recycler view
    public class ItemHolder extends RecyclerView.ViewHolder {
        public TextView nameText;
        public TextView priceText;
        public TextView quantityText;
        public Button deleteButton;

        public ItemHolder(View itemView) {
            super(itemView);

            // Sets the views
            nameText = itemView.findViewById(R.id.textview_Oname_item);
            priceText = itemView.findViewById(R.id.textview_Oprice_item);
            quantityText = itemView.findViewById(R.id.textview_Oquantity_item);
            deleteButton = itemView.findViewById(R.id.deleteButton);

        }
    }

    // Refreshes the recycler view when an item is deleted or added
    public void swapCursor(Cursor newCursor) {
        // Closes if the order database is empty
        if (mCursor != null) {
            mCursor.close();
        }

        // Updates the recycler view
        mCursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    // Gets the data from the SQLite order database
    private Cursor getAllItems() {
        return db.query(
                OrderLayout.ItemEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}
