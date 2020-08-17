package com.example.localcoffee;

/*
 This class creates the layout and mapping of an order.
 It uses the item names, quantities, and subtotals, along with the total of the order.
 */

import com.google.firebase.database.Exclude;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order {

    public List<String> itemNames;
    public List<Integer> itemQuantities;
    public List<Double> subtotals;
    public String total;

    // Required for Firebase to access the model
    public Order() {
    }

    // Default constructor
    public Order(List<String> itemNames, List<Integer> itemQuantities, List<Double> subtotals, String total) {
        this.itemNames = itemNames;
        this.itemQuantities = itemQuantities;
        this.subtotals = subtotals;
        this.total = total;
    }

    // Creates a hash map of the values from the order
    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("itemNames", itemNames);
        result.put("itemQuantities", itemQuantities);
        result.put("subtotals", subtotals);
        result.put("total", total);
        return result;
    }

    // Retrieves the list of names
    public List<String> getItemNames() {
        return itemNames;
    }

    // Sets the list of names
    public void setItemNames(List<String> itemNames) {
        this.itemNames = itemNames;
    }

    // Retrieves the list of quantities
    public List<Integer> getItemQuantities() {
        return itemQuantities;
    }

    // Sets the list of names
    public void setItemQuantities(List<Integer> itemQuantities) {
        this.itemQuantities = itemQuantities;
    }

    // Retrieves the list of subtotals
    public List<Double> getSubtotals() {
        return subtotals;
    }

    // Sets the list of names
    public void setSubtotal(List<Double> subtotals) {
        this.subtotals = subtotals;
    }

    // Retrives the total of the order
    public String getTotal() {
        return total;
    }

    // Sets the total
    public void setTotal(String total) {
        this.total = total;
    }
}
