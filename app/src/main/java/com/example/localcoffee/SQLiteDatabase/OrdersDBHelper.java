package com.example.localcoffee.SQLiteDatabase;

/*
 Creates the Order SQLite database
*/

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OrdersDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CurrentOrders.DB";

    public OrdersDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creates the Order database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(OrderTable.SQL_CREATE_ORDER);
    }

    // Upgrades the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(OrderTable.SQL_DELETE_ORDER);
        onCreate(db);
    }
}