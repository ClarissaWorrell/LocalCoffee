package com.example.localcoffee;

/*
 This class is a nested recycler view to display the items from the
 lists of names, quantities, and subtotals under each of the user's orders.
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.util.List;

public class PastOrdersAdapter extends RecyclerView.Adapter<PastOrdersAdapter.ItemViewHolder> {

    // Initializes the lists
    private List<String> itemNames;
    private List<Integer> itemQuantities;
    private List<Double> itemSubtotals;
    public DecimalFormat priceFormat = new DecimalFormat("#.00");

    // Default constructor
    public PastOrdersAdapter(List<String> itemNames, List<Integer> itemQuantities, List<Double> itemSubtotals) {
        this.itemNames = itemNames;
        this.itemQuantities = itemQuantities;
        this.itemSubtotals = itemSubtotals;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Sets the layout of the each item in the recycler view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_order_interior_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        // Formats the subtotal
        String itemSubTotal = "$" + priceFormat.format(itemSubtotals.get(position));

        // Sets the values of the item in the recycler view at the current position
        holder.itemName.setText(itemNames.get(position));
        holder.itemQuantity.setText(itemQuantities.get(position).toString());
        holder.itemSubtotal.setText(itemSubTotal);
    }

    // Gets the number of items to be displayed
    @Override
    public int getItemCount() {
        return itemNames.size();
    }

    // Sets the variables and the views of the recycler view
    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView itemName, itemQuantity, itemSubtotal;

        public ItemViewHolder(View itemView) {
            super(itemView);

            // Sets the views
            itemName = itemView.findViewById(R.id.itemName);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            itemSubtotal = itemView.findViewById(R.id.itemSubtotal);
        }
    }
}