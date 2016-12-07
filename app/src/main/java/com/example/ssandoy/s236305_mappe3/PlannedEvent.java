package com.example.ssandoy.s236305_mappe3;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ssandoy on 10.11.2016.
 */
public class PlannedEvent {

    private long eventID;
    private String eventName;
    private String eventDate;
    private String startTime;
    private String endTime;
    private long alertTime;
    private long dateTime;
    private String alarmStatus;


    public PlannedEvent() {
    }

    public PlannedEvent(long eventID) {
        this.eventID = eventID;
    }

    public PlannedEvent(long eventID, String eventName, String eventDate, String startTime) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.startTime = startTime;
    }

    public long getAlertTime() {
        return alertTime;
    }

    public PlannedEvent setAlertTime(long alertTime) {
        this.alertTime = alertTime;
        return this;
    }

    public long getDateTime() {
        return dateTime;
    }

    public PlannedEvent setDateTime(long dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public String getEndTime() {
        return endTime;
    }

    public PlannedEvent setEndTime(String endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getStartTime() {
        return startTime;
    }

    public PlannedEvent setStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public String getEventDate() {
        return eventDate;
    }

    public PlannedEvent setEventDate(String eventDate) {
        this.eventDate = eventDate;
        return this;
    }

    public String getEventName() {
        return eventName;
    }

    public PlannedEvent setEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public long getEventID() {
        return eventID;
    }

    public PlannedEvent setEventID(long eventID) {
        this.eventID = eventID;
        return this;
    }

    public String getAlarmStatus() {
        return alarmStatus;
    }

    public PlannedEvent setAlarmStatus(String alarmStatus) {
        this.alarmStatus = alarmStatus;
        return this;
    }

    //DB-ENTITY

    //Plans
    static final String TABLE_PLANS = "PlannedEvents";
    static final String KEY_PLANID = "_id";
    static final String KEY_PLANNAME = "Event";
    static final String KEY_DATE = "EventDate";
    static final String KEY_STARTTIME = "StartTime";
    static final String KEY_ENDTIME = "EndTime";
    static final String KEY_ALERTTIME = "AlertTime";
    static final String KEY_DATETIME = "DateTime";
    static final String KEY_ALARMSTATUS = "AlarmStatus";

    public static final String ACTIVE = "PENDING";
    public static final String EXECUTED = "EXECUTED";
    public static final String CANCELLED = "C";

    static String createSqlString() {
        return DBHandler.buildString("CREATE TABLE ", TABLE_PLANS, " (",
                KEY_PLANID, " INTEGER PRIMARY KEY AUTOINCREMENT, ",
                KEY_PLANNAME, " TEXT, ",
                KEY_DATE, " DATE, ",
                KEY_STARTTIME, " INTEGER, ",
                KEY_ENDTIME, " INTEGER, ",
                KEY_ALERTTIME, " INTEGER, ",
                KEY_DATETIME, " INTEGER, ",
                KEY_ALARMSTATUS, " TEXT",
                ");");
    }

    long save(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_PLANNAME, eventName);
        cv.put(KEY_DATE, eventDate);
        cv.put(KEY_STARTTIME, startTime);
        cv.put(KEY_ENDTIME, endTime);
        cv.put(KEY_ALERTTIME, alertTime);
        cv.put(KEY_DATETIME, dateTime);
        cv.put(KEY_ALARMSTATUS, alarmStatus);

        return db.insert(TABLE_PLANS, null, cv);
    }

    long update(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_PLANNAME, eventName);
        cv.put(KEY_DATE, eventDate);
        cv.put(KEY_STARTTIME, startTime);
        cv.put(KEY_ENDTIME, endTime);
        cv.put(KEY_ALERTTIME, alertTime);
        cv.put(KEY_DATETIME, dateTime);
        cv.put(KEY_ALARMSTATUS, alarmStatus);

        return db.update(TABLE_PLANS, cv, KEY_PLANID + "='" + eventID + "'", null);
    }

    public boolean loadEvent(SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_PLANS, null, KEY_PLANID + " = ?", new String[]{String.valueOf(eventID)}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                resetCurrentEvent();
                eventID = cursor.getLong(cursor.getColumnIndex(KEY_PLANID));
                eventName = cursor.getString(cursor.getColumnIndex(KEY_PLANNAME));
                eventDate = cursor.getString(cursor.getColumnIndex(KEY_DATE));
                startTime = cursor.getString(cursor.getColumnIndex(KEY_STARTTIME));
                endTime = cursor.getString(cursor.getColumnIndex(KEY_ENDTIME));
                alertTime = cursor.getLong(cursor.getColumnIndex(KEY_ALERTTIME));
                dateTime = cursor.getLong(cursor.getColumnIndex(KEY_DATETIME));
                alarmStatus = cursor.getString(cursor.getColumnIndex(KEY_ALARMSTATUS));
                return true;
            }
            return false;
        } finally {
            cursor.close();
        }

    }

    public void resetCurrentEvent() {
        eventID = 0;
        eventName = null;
        eventDate = null;
        startTime = null;
        endTime = null;
        alertTime = 0;
        dateTime = 0;
        alarmStatus = null;
    }


}

