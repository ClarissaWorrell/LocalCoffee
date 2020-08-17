package com.example.localcoffee.SQLiteDatabase;

/*
 Displays the items from the Menu database in the recyclerView,
 allows the user to change the quantity of each item, and
 add items to the order database when the user clicks the add button
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import androidx.recyclerview.widget.RecyclerView;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.localcoffee.R;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ItemHolder> {

    private Context mContext;
    private Cursor mCursor;
    private OrdersDBHelper oDBHelper;
    private SQLiteDatabase db;
    int qty = 1;
    int oldQty;
    int rowsUpdated;

    public MenuAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;

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
        View view = inflater.inflate(R.layout.menu_item, parent, false);
        return new ItemHolder(view);
    }

    // Sets the data of the recycler view
    @Override
    public void onBindViewHolder(final ItemHolder holder, final int position) {

        // Stops looking for data when the last item in the menu database is displayed
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        // Gets the name and price from the menu database at the current row
        String name = mCursor.getString(mCursor.getColumnIndex(MenuLayout.ItemEntry.COLUMN_NAME));
        String price = mCursor.getString(mCursor.getColumnIndex(MenuLayout.ItemEntry.COLUMN_PRICE));

        // Sets the data for each item in the recycler view
        holder.nameText.setText(name);
        holder.priceText.setText(price);
        holder.qtyText.setText(Integer.toString(qty));

        // Allows the user to change the quantity
        // Increases quantity
        holder.increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                // Gets the quantity from the current item
                qty = Integer.parseInt(holder.qtyText.getText().toString());
                // Adds constraint so that the quantity can't go above 10
                if (qty < 10) {
                    qty++;
                    holder.qtyText.setText(Integer.toString(qty)); // Displays the new quantity
                }
                else {
                    return;
                }
            }
        });

        // Decreases quantity
        holder.decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                // Gets the quantity from the current item
                qty = Integer.parseInt(holder.qtyText.getText().toString());
                // Adds constraint so that the quantitiy can't go below 1
                if (qty > 1) {
                    qty--;
                    holder.qtyText.setText(Integer.toString(qty)); // Displays the new quantity
                }
                else {
                    return;
                }
            }
        });


        byte[] foodImage = mCursor.getBlob(mCursor.getColumnIndex(MenuLayout.ItemEntry.COLUMN_PHOTO)); // Gets the image from the menu database and stores it in a byte array
        Bitmap bitmap = BitmapFactory.decodeByteArray(foodImage, 0, foodImage.length); // Converts the byte array to bitmap
        holder.imageView.setImageBitmap(bitmap); // Displays the image in the recycler view

        // Adds an item to the orders database
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {

                mCursor.moveToPosition(position);

                // Gets the name and price from the recycler view at the current row
                String name = mCursor.getString(mCursor.getColumnIndex(MenuLayout.ItemEntry.COLUMN_NAME));
                // Converts the price into a double and removes the '$' character
                Double price = Double.parseDouble(mCursor.getString(mCursor.getColumnIndex(MenuLayout.ItemEntry.COLUMN_PRICE)).substring(1));
                // Gets the quantity from the current item
                qty = Integer.parseInt(holder.qtyText.getText().toString());

                // Opens the order database to write to it
                db = oDBHelper.getWritableDatabase();

                ContentValues values = new ContentValues();

                // Checks for existence of item in the order database
                String create = OrderLayout.ItemEntry.COLUMN_NAME + " = ?";
                String[] nameSelection = {name};
                Cursor cursor = db.query(OrderLayout.ItemEntry.TABLE_NAME, null, create, nameSelection, null, null, null);

                // Adds a new item if it doesn't already exist
                if (cursor.getCount() == 0) {
                    values.put(OrderLayout.ItemEntry.COLUMN_NAME, name);
                    values.put(OrderLayout.ItemEntry.COLUMN_QUANTITY, qty);
                    values.put(OrderLayout.ItemEntry.COLUMN_PRICE, price);
                    long rowID = db.insert(OrderLayout.ItemEntry.TABLE_NAME, null, values);

                    // Checks if the insert was successful
                    if (rowID != -1)
                        Toast.makeText(mContext, "Item added!", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(mContext, "Error: Please try again", Toast.LENGTH_SHORT).show();
                }

                // Updates the quantity of the item if it already exists
                else {

                    // Gets the previous quantity of the item
                    while(cursor.moveToNext()) {
                        oldQty = cursor.getInt(cursor.getColumnIndex(OrderLayout.ItemEntry.COLUMN_QUANTITY));
                    }
                    int newQty = oldQty + qty; // Adds the quantities

                    // Updates the item's quantity in the order database
                    values.put(OrderLayout.ItemEntry.COLUMN_QUANTITY, newQty);
                    String update = OrderLayout.ItemEntry.COLUMN_NAME + " LIKE ?";
                    rowsUpdated = db.update(OrderLayout.ItemEntry.TABLE_NAME, values, update, nameSelection);

                    // Checks if the update was successful
                    if(rowsUpdated > 0)
                        Toast.makeText(mContext, "Item Updated!", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(mContext, "Error: Please try again", Toast.LENGTH_SHORT).show();
                }
                cursor.close();
            }
        });
    }

    // Gets the number of rows in the Menu database
    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    // Sets the variables and the views of the recycler view
    public class ItemHolder extends RecyclerView.ViewHolder {
        public TextView nameText;
        public TextView priceText;
        public Button addButton;
        public ImageView imageView;
        public TextView qtyText;
        public ImageButton decreaseButton;
        public ImageButton increaseButton;


        public ItemHolder(View itemView) {
            super(itemView);

            // Sets the views
            nameText = itemView.findViewById(R.id.textview_name_item);
            priceText = itemView.findViewById(R.id.textview_price_item);
            addButton = itemView.findViewById(R.id.testButton);
            imageView = itemView.findViewById(R.id.itemPicture);
            qtyText = itemView.findViewById(R.id.qty);
            increaseButton = itemView.findViewById(R.id.increase);
            decreaseButton = itemView.findViewById(R.id.decrease);
        }
    }
}
