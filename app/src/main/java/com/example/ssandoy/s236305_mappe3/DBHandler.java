package com.example.ssandoy.s236305_mappe3;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by ssandoy on 05.11.2016.
 */
public class DBHandler {


    Context context;

    public static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    static final String DATABASE_NAME = "PracticDatabase";


    static final int DATABASE_VERSION = 1;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBHandler(Context context) {
        this.context = context;
        DBHelper = new DatabaseHelper(context);
    }


    public static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(PlannedEvent.createSqlString());
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + PlannedEvent.TABLE_PLANS);
            onCreate(db);
        }


        public static void delete(SQLiteDatabase db) { //sletter alle alarmer som er satt til Cancelled.
            db.delete(PlannedEvent.TABLE_PLANS, PlannedEvent.KEY_ALARMSTATUS+" = ?", new String[]{PlannedEvent.CANCELLED});
        }
    } //END OF HELPER

    public DBHandler open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }


   /* public void reopen() throws SQLException{
        close();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }*/



    public Cursor findAll(){
        String	selectQuery	=	"SELECT * FROM "+ PlannedEvent.TABLE_PLANS + " ORDER BY " + PlannedEvent.KEY_PLANNAME;
        Cursor cursor = db.rawQuery(selectQuery,null);
        return cursor;
    }



    public Calendar setPlanDate(int day, int month, int year){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar;
    }

    public static final String getDateString(int year, int month, int date) {
        if(date <= 9) {
            return buildString("0" + date, "-", month, "-", year);
        } else {
            return buildString(date, "-", month, "-", year);
        }

    }

    public static final String getTimeString(int minute, int hour){
        return buildString(hour, ":", minute>9 ? "":"0", minute); //sjekker om minutt har ett eller to siffer
    }


    public static String buildString(Object... strings) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.setLength(0);
        for (Object obj : strings) {
            stringBuilder.append(obj);
        }
        return stringBuilder.toString();
    }
}
