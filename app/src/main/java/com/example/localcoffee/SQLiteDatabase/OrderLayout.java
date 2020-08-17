package com.example.localcoffee.SQLiteDatabase;

/*
 Creates the columns of the order database
*/

import android.provider.BaseColumns;

public class OrderLayout {
    public OrderLayout (){
    }

    public static class ItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "Orders";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
    }
}
