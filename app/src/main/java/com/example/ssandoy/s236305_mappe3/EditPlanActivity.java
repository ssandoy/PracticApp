package com.example.ssandoy.s236305_mappe3;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditPlanActivity extends AppCompatActivity {

    private SQLiteDatabase db;

    private Button deleteButton;
    private Button saveButton;
    private Button cancelButton;
    private EditText titleText;
    private Button dateButton;
    private Button alertButton;
    private Button startTimeButton;
    private Button endTimeButton;

    private static final SimpleDateFormat sdf = new SimpleDateFormat(PracticApp.DATEFORMAT);

    private int mHour, mMinute;

    int year, month, day;

    long eventID;

    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plan);

        db = PracticApp.db;

        initWidgets();

        createListeners();

        Intent intent = getIntent();
        Bundle pakke = intent.getExtras();
        eventID = pakke.getLong("ID");
        Cursor cursor = db.rawQuery("SELECT * FROM " + PlannedEvent.TABLE_PLANS + " WHERE " + PlannedEvent.KEY_PLANID + " LIKE " + eventID, null);
        cursor.moveToFirst();
        titleText.setText(cursor.getString(cursor.getColumnIndex(PlannedEvent.KEY_PLANNAME)));
        dateButton.setText(cursor.getString(cursor.getColumnIndex(PlannedEvent.KEY_DATE)));
        startTimeButton.setText(cursor.getString(cursor.getColumnIndex(PlannedEvent.KEY_STARTTIME)));
        endTimeButton.setText(cursor.getString(cursor.getColumnIndex(PlannedEvent.KEY_ENDTIME)));

        switch (cursor.getInt(cursor.getColumnIndex(PlannedEvent.KEY_ALERTTIME))) {
            case 0:
                alertButton.setText(R.string.eventStart);
                break;
            case 60*5*1000:
                alertButton.setText(R.string.event5min);
                break;
            case 60*30*1000:
                alertButton.setText(R.string.event30min);
                break;
            case 60*60*1000:
                alertButton.setText(R.string.event1hour);
                break;


        }
    }



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("titleText", titleText.getText().toString());
        savedInstanceState.putString("date", dateButton.getText().toString());
        savedInstanceState.putString("startTime", startTimeButton.getText().toString());
        savedInstanceState.putString("endTime", endTimeButton.getText().toString());

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String title = savedInstanceState.getString("titleText");
        String date = savedInstanceState.getString("date");
        String startTime = savedInstanceState.getString("startTime");
        String endTime = savedInstanceState.getString("endTime");
        titleText.setText(title);
        dateButton.setText(date);
        startTimeButton.setText(startTime);
        endTimeButton.setText(endTime);
    }



    public void initWidgets() {
        saveButton = (Button) findViewById(R.id.saveButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        titleText = (EditText) findViewById(R.id.titleText);
        alertButton = (Button) findViewById(R.id.alertButton);
        dateButton = (Button) findViewById(R.id.dateButton);
        startTimeButton = (Button) findViewById(R.id.startTimeButton);
        endTimeButton = (Button) findViewById(R.id.endTimeButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
    }

    public void createListeners() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (update()) {
                        Intent i = new Intent(EditPlanActivity.this, PlanOverviewActivity.class);
                        startActivity(i);
                        finish();
                        Toast.makeText(getApplicationContext(), R.string.planChanged, Toast.LENGTH_LONG).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });


        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);


                TimePickerDialog timePickerDialog = new TimePickerDialog(EditPlanActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                if (hourOfDay < 9) {
                                    if (minute <= 9) {
                                        startTimeButton.setText("0" + hourOfDay + ":" + "0" + minute);
                                        endTimeButton.setText("0" + (1 + hourOfDay) + ":" + "0" + minute);
                                    } else {
                                        startTimeButton.setText("0" + hourOfDay + ":" + minute);
                                        endTimeButton.setText("0" + (1 + hourOfDay) + ":" + minute);
                                    }

                                } else if (hourOfDay == 9) {
                                    startTimeButton.setText("0" + hourOfDay + ":" + "0" + minute);
                                    endTimeButton.setText((1 + hourOfDay) + ":" + "0" + minute);
                                } else {
                                    if (minute <= 9) {
                                        startTimeButton.setText(hourOfDay + ":" + "0" + minute);
                                        endTimeButton.setText((1 + hourOfDay) + ":" + "0" + minute);
                                    } else {

                                        startTimeButton.setText(hourOfDay + ":" + minute);
                                        endTimeButton.setText((1 + hourOfDay) + ":" + minute);
                                    }
                                }
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });

        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditPlanActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String timeArray[] = startTimeButton.getText().toString().split(":", 2);
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                Calendar start = Calendar.getInstance();
                                start.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
                                start.set(Calendar.MINUTE,  Integer.parseInt(timeArray[1]));
                                Calendar end = Calendar.getInstance();
                                end.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                end.set(Calendar.MINUTE, minute);

                                if(start.before(end)) {
                                    endTimeButton.setText(sdf.format(end.getTime()));

                                } else if (end.before(start)) {
                                    startTimeButton.setText(sdf.format(end.getTime()));
                                    endTimeButton.setText(sdf.format(end.getTime()));
                                }
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });


        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] alertOptions = getResources().getStringArray(R.array.alert_array);

                AlertDialog.Builder builder = new AlertDialog.Builder(EditPlanActivity.this);
                builder.setTitle("Varsel");
                builder.setItems(alertOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertButton.setText(alertOptions[which]);
                    }
                });
                builder.show();
                alertDialog = builder.create();

            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(EditPlanActivity.this, datePickerListener,
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditPlanActivity.this, PlanOverviewActivity.class);
                startActivity(i);
                finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new android.support.v7.app.AlertDialog.Builder(EditPlanActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.warning)
                        .setMessage(R.string.deleteQ)
                        .setPositiveButton("JA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //int i = db.delete(PlannedEvent.TABLE_PLANS, PlannedEvent.KEY_PLANID + "='" + eventID + "'", null);
                                Intent cancelThis = new Intent(EditPlanActivity.this, AlarmService.class);
                                cancelThis.putExtra(PlannedEvent.KEY_PLANID, String.valueOf(eventID));
                                cancelThis.setAction(AlarmService.CANCEL);
                                startService(cancelThis);
                                Intent intent = new Intent(EditPlanActivity.this, PlanOverviewActivity.class);
                                startActivity(intent);
                                finish();
                               // if (i > 0) {
                                    Toast.makeText(getApplicationContext(), R.string.planDeleted, Toast.LENGTH_LONG).show();
                                //}
                            }
                        })
                        .setNegativeButton("NEI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = ++selectedMonth;
            day = selectedDay;

            // setter dato til textviewet
            dateButton.setText(DBHandler.getDateString(year, month, day));

        }
    };

    private boolean validate() {
        if (titleText.getText().toString().matches("")) {
            titleText.requestFocus();
            Toast.makeText(this, R.string.typeTitle, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean update() throws ParseException {
        if (!validate())
            return false;
        PlannedEvent event = new PlannedEvent(eventID);
        event.loadEvent(db);
        event.setEventName(titleText.getText().toString());
        event.setEventDate(dateButton.getText().toString());
        event.setAlertTime(getAlertTime());
        String endTimeArray[] = endTimeButton.getText().toString().split(":", 2);
        event.setEndTime(DBHandler.getTimeString(Integer.parseInt(endTimeArray[1]),
                Integer.parseInt(endTimeArray[0]))); //TRENGS DENNE?

        event.setAlarmStatus(PlannedEvent.ACTIVE);
        String timeArray[] = startTimeButton.getText().toString().split(":", 2);
        event.setStartTime(DBHandler.getTimeString(Integer.parseInt(timeArray[1]),
                Integer.parseInt(timeArray[0])));

        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(event.getEventDate()));
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        event.setDateTime(cal.getTimeInMillis());

        event.update(db);

        Intent service = new Intent(this, AlarmService.class);
        service.putExtra(PlannedEvent.KEY_PLANID, String.valueOf(eventID));
        service.setAction(AlarmService.CREATE);
        startService(service);
        return true;
    }

    public int getAlertTime() {
        int alertTime;
        switch (alertButton.getText().toString()) {
            case "Når hendelsen starter":
                alertTime = 0;
                return alertTime;
            case "5 minutter før":
                alertTime = 60 * 5 * 1000;
                return alertTime;
            case "15 minutter før":
                alertTime = 60 * 15 * 1000;
                return alertTime;
            case "30 minutter før":
                alertTime = 60 * 30 * 1000;
                return alertTime;
            case "1 time før":
                alertTime = 60 * 60 * 1000;
                return alertTime;
            default:
                alertTime = 0;
                return alertTime;
        }

    }

    @Override
    protected void onDestroy() {
        if(alertDialog != null) {
            alertDialog.dismiss();
        }
        super.onDestroy();
    }
}