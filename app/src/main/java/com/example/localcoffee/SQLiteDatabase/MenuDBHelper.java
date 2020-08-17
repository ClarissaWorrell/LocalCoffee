package com.example.localcoffee.SQLiteDatabase;

/*
 Creates the Menu SQLite database
*/

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MenuDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MenuItems.DB";

    public MenuDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creates the menu database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MenuTable.SQL_CREATE_MENU);
    }

    // Upgrades the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(MenuTable.SQL_DELETE_MENU);
        onCreate(db);
    }
}
