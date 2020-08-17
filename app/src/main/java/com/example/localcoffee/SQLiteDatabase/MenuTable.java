package com.example.localcoffee.SQLiteDatabase;

/*
 Creates the statements to create and delete the menu database
 */

public class MenuTable {

    public static final String TEXT_TYPE = " TEXT ";
    public static final String BLOB_TYPE = " BLOB ";
    public static final String COMMA_SEP = ", ";

    public static final String SQL_CREATE_MENU =
            "CREATE TABLE " + MenuLayout.ItemEntry.TABLE_NAME + " (" +
                    MenuLayout.ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MenuLayout.ItemEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    MenuLayout.ItemEntry.COLUMN_PHOTO + BLOB_TYPE + COMMA_SEP +
                    MenuLayout.ItemEntry.COLUMN_PRICE + TEXT_TYPE + " )";

    public static final String SQL_DELETE_MENU =
            "DROP TABLE IF EXISTS " + MenuLayout.ItemEntry.TABLE_NAME;
}


