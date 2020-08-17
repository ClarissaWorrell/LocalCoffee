package com.example.localcoffee.SQLiteDatabase;

/*
 Creates the columns of the menu database
*/

import android.provider.BaseColumns;

public class MenuLayout {
    public MenuLayout (){
    }

    public static class ItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "Menu";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHOTO = "photo";
        public static final String COLUMN_PRICE = "price";
    }

}
