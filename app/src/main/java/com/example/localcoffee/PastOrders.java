package com.example.localcoffee;

/*

 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.SQLException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

public class PastOrders extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference UsersRef;
    private FirebaseDatabase mDatabase;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_orders);

        // Sets the recycler view
        recyclerView = findViewById(R.id.pastOrdersRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Gets the current user's Uid
        String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Sets the query
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid).child("Orders");

        // Queries the Firebase database
        final FirebaseRecyclerOptions<Order> options =
                new FirebaseRecyclerOptions.Builder<Order>()
                        .setQuery(UsersRef, Order.class)
                        .build();

        // Creates the recycler view using the FirebaseUI
        FirebaseRecyclerAdapter<Order, ItemHolder> adapter =
                new FirebaseRecyclerAdapter<Order, ItemHolder>(options) {
                    @NonNull
                    @Override
                    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        // Sets the item holder view
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_order_item, parent, false);
                        ItemHolder viewHolder = new ItemHolder(view);
                        return viewHolder;
                    }

                    // Sets the data of the recycler view
                    @Override
                    protected void onBindViewHolder(@NonNull ItemHolder holder, int position, @NonNull Order model) {

                        // Creates lists to store the values from the order class
                        List<String> itemNames = model.getItemNames();
                        List<Integer> itemQuantities = model.getItemQuantities();
                        List<Double> itemSubtotals = model.getSubtotals();
                        String total = model.getTotal();

                        // Sets the total price of the order
                        holder.userStatus.setText(model.getTotal());

                        // Sets the layout of the nested recycler view
                        LinearLayoutManager layoutManager
                                = new LinearLayoutManager(
                                holder.childRecyclerView.getContext(),
                                LinearLayoutManager.VERTICAL,
                                false);
                        layoutManager.setInitialPrefetchItemCount(itemNames.size());

                        // Passes the lists to the nested recycler view
                        PastOrdersAdapter childItemAdapter = new PastOrdersAdapter(itemNames, itemQuantities, itemSubtotals);
                        holder.childRecyclerView.setLayoutManager(layoutManager);
                        holder.childRecyclerView.setAdapter(childItemAdapter);
                        holder.childRecyclerView.setRecycledViewPool(viewPool);
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    // Sets the variables and the views of the recycler view
    public class ItemHolder extends RecyclerView.ViewHolder {

        private RecyclerView childRecyclerView;
        private TextView userStatus;

        // Sets the views
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            userStatus = itemView.findViewById(R.id.itemTotal);
            childRecyclerView = itemView.findViewById(R.id.nestedRecyclerview);
        }
    }
}