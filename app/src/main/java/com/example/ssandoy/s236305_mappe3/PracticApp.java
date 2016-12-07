package com.example.ssandoy.s236305_mappe3;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ssandoy on 23.11.2016.
 */
public class PracticApp extends Application {


    public static DBHandler.DatabaseHelper dbHelper;
    public static SQLiteDatabase db;


    public static final String DATEFORMAT = "dd-MM-yyyy";

    @Override
    public void onCreate() {
        super.onCreate();

        dbHelper = new DBHandler.DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
    }


}
