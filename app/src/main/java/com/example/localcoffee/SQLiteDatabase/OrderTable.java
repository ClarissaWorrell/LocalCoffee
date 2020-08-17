package com.example.localcoffee.SQLiteDatabase;

/*
 Creates the statements to create and delete the order database
 */

public class OrderTable {

    public static final String TEXT_TYPE = " TEXT ";
    public static final String FLOAT_TYPE = " REAL ";
    public static final String INTEGER_TYPE = " INTEGER ";
    public static final String COMMA_SEP = ", ";

    public static final String SQL_CREATE_ORDER =
            "CREATE TABLE " + OrderLayout.ItemEntry.TABLE_NAME + " (" +
                    OrderLayout.ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    OrderLayout.ItemEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    OrderLayout.ItemEntry.COLUMN_QUANTITY + INTEGER_TYPE + COMMA_SEP +
                    OrderLayout.ItemEntry.COLUMN_PRICE + FLOAT_TYPE + " )";

    public static final String SQL_DELETE_ORDER =
            "DROP TABLE IF EXISTS " + OrderLayout.ItemEntry.TABLE_NAME;
}

