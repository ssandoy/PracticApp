package com.example.ssandoy.s236305_mappe3;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by ssandoy on 25.11.2016.
 */
public class PlanContentProvider extends ContentProvider {

    static final String TABLE_PLANS = "PlannedEvents";
    static final String KEY_PLANID = "_ID";
    static final String DATABASE_NAME = "PracticDatabase";

    static final int DATABASE_VERSION = 1;

    private DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public final static String PROVIDER = "com.example.ssandoy.s236305_mappe3";
    private static final int PLAN =1;
    private static final int MPLAN=2;

    public static final Uri CONTENT_URI = Uri.parse("content://"+ PROVIDER + "/plan");
    private static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER, "plan",MPLAN);
        uriMatcher.addURI(PROVIDER, "plan/#",PLAN);
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
    }//SLUTT PÅ HELPER CLASS

    @Override
    public boolean onCreate() {
        DBHelper = new DatabaseHelper(getContext());
        db = DBHelper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        {
            Cursor cur=null;
            if (uriMatcher.match(uri) ==PLAN)
            {
                cur=db.query(TABLE_PLANS,projection, KEY_PLANID + " = " +
                                uri.getPathSegments().get(1)
                        ,selectionArgs, null, null, sortOrder);
                return cur;}
            else{
                cur=db.query(TABLE_PLANS,null, null, null, null, null,
                        null);
                return cur;
            }
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case MPLAN:return
                    "vnd.android.cursor.dir/vnd.com.example.ssandoy.s236305_mappe3";
            case PLAN:return
                    "vnd.android.cursor.item/vnd.com.example.ssandoy.s236305_mappe3";
            default: throw new
                    IllegalArgumentException("Ugyldig URI" +
                    uri);}
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = DBHelper.getWritableDatabase();
        db.insert(TABLE_PLANS,null, values);

        Cursor c= db.query(TABLE_PLANS, null, null, null, null,
                null, null);

        c.moveToLast();
        long minid=c.getLong(0);
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, minid);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        if (uriMatcher.match(uri)==PLAN){
            db.delete(TABLE_PLANS, KEY_PLANID + " = " +
                    uri.getPathSegments().get(1),selectionArgs);
            getContext().getContentResolver().notifyChange(uri,null);
            return 1;
        }
        if (uriMatcher.match(uri) == MPLAN){
            db.delete(TABLE_PLANS,null,null);
            getContext().getContentResolver().notifyChange(uri,null);
            return 2;
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (uriMatcher.match(uri)== PLAN){
            db.update(TABLE_PLANS, values,KEY_PLANID + " = " +
                    uri.getPathSegments().get(1),null);
            getContext().getContentResolver().notifyChange(uri, null);
            return 1;
        }
        if (uriMatcher.match(uri) == MPLAN){
            db.update(TABLE_PLANS, null, null, null);
            getContext().getContentResolver().notifyChange(uri, null);
            return 2;
        }
        return 0;
    }
}
