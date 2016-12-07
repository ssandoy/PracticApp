package com.example.ssandoy.s236305_mappe3;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PlanOverviewActivity extends ListActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    DBHandler db;


    private ImageButton addEventButton;
    private TextView tvCategory;
    private TextView tvDate;
    private ImageButton leftButton;
    private ImageButton rightButton;



    public final Calendar cal = Calendar.getInstance();

    private PlanAdapter adapter;

    public final Date day = new Date();


    private ListView plansView;

    private static final SimpleDateFormat sdf = new SimpleDateFormat(PracticApp.DATEFORMAT);

    private String[] monthlyArray; //For bruk i ListView

    private String[] monthArray; //For bruk i tvDate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planoverview);
        this.getListView().setDividerHeight(2);
        initWidgets();

        registerForContextMenu(getListView());

        db = new DBHandler(this);

        monthlyArray = getResources().getStringArray(R.array.month_array);
        monthArray = getResources().getStringArray(R.array.monthly_array);

        createListeners();

        //Hvor data hentes fra
        String[] from = new String[]{PlannedEvent.KEY_PLANNAME, PlannedEvent.KEY_STARTTIME, PlannedEvent.KEY_ENDTIME, PlannedEvent.KEY_DATE};
        // Felter i UI hvor data skal sendes til
        int[] to = new int[]{R.id.Name, R.id.eventTime, R.id.Month};

        adapter = new PlanAdapter(this, R.layout.item_plan, initCursor(tvCategory.getText().toString()), from,
                to, 0);


        setListAdapter(adapter);

    }



    public void initWidgets() {
        addEventButton = (ImageButton) findViewById(R.id.addButton);
        leftButton = (ImageButton) findViewById(R.id.leftButton);
        rightButton = (ImageButton) findViewById(R.id.rightButton);
        tvCategory = (TextView) findViewById(R.id.tvCategory);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvDate.setText(sdf.format(cal.getTime()));
        plansView = (ListView)findViewById(android.R.id.list);
    }



    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        String category = state.getString("tvCategory");
        String date = state.getString("tvDate");
        cal.setTimeInMillis(state.getLong("cal"));
        tvDate.setText(date);
        tvCategory.setText(category);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("cal", cal.getTimeInMillis());
        outState.putString("tvCategory", tvCategory.getText().toString());
        outState.putString("tvDate", tvDate.getText().toString());
    }

    public void createListeners() {
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PlanOverviewActivity.this, PlanActivity.class);
                startActivity(i);
                finish();
            }
        });


        tvCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] viewOptions = getResources().getStringArray(R.array.viewOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(PlanOverviewActivity.this);
                builder.setTitle(R.string.displayMode);
                builder.setItems(viewOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvCategory.setText(viewOptions[which]);
                        if(tvCategory.getText().toString().equals("Ukentlig")) {
                            int week = cal.get(Calendar.WEEK_OF_YEAR);
                            if(week == 1) {
                                tvDate.setText("UKE " + week);
                            } else
                                tvDate.setText("UKE " + --week);
                        } else if(tvCategory.getText().toString().equals("Månedlig")) {
                            int month = cal.get(Calendar.MONTH);
                            tvDate.setText(monthArray[month]);
                        }else
                            tvDate.setText(sdf.format(cal.getTime()));
                        ((PlanAdapter)getListAdapter()).changeCursor(initCursor(tvCategory.getText().toString()));
                    }
                });
                builder.show();

            }
        });

        plansView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                Intent intent = new Intent(PlanOverviewActivity.this, EditPlanActivity.class);
                intent.putExtra("ID", id);
                startActivity(intent);
            }
        });
    }


    private Cursor initCursor(String range) {
        Cursor curr;
        switch (range) {
            case "Daglig":
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.clear(Calendar.MINUTE);
                cal.clear(Calendar.SECOND);
                cal.clear(Calendar.MILLISECOND);
                long time = cal.getTimeInMillis();
                long nowtime = System.currentTimeMillis();
                curr = PracticApp.db.rawQuery("SELECT * FROM PlannedEvents WHERE DateTime BETWEEN " +
                        time + " AND " + (time + 86400000) + " ORDER BY DATETIME", null);
                return curr;

            case "Ukentlig":
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.clear(Calendar.MINUTE);
                cal.clear(Calendar.SECOND);
                cal.clear(Calendar.MILLISECOND);
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                long weekTime = cal.getTimeInMillis();
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                long endWeekTime = cal.getTimeInMillis();
                cal.add(Calendar.WEEK_OF_YEAR,-1); //setter uke til naavaerende maaned.
                curr = PracticApp.db.rawQuery("SELECT * FROM PlannedEvents WHERE DateTime BETWEEN " +
                        weekTime + " AND " + endWeekTime + " ORDER BY DATETIME", null);
                return curr;
            case "Månedlig":
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.clear(Calendar.MINUTE);
                cal.clear(Calendar.SECOND);
                cal.clear(Calendar.MILLISECOND);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                long monthTime = cal.getTimeInMillis();
                cal.add(Calendar.MONTH, 1);
                long endMonthTime = cal.getTimeInMillis();
                cal.add(Calendar.MONTH, -1); //setter cal til naavaerende maaned.
                curr = PracticApp.db.rawQuery("SELECT * FROM PlannedEvents WHERE DateTime BETWEEN " +
                        monthTime + " AND " + endMonthTime + " ORDER BY DATETIME", null);
                return curr;

            default:
                return null;
        }
    }



    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftButton:
                moveDate(-1);
                if(tvCategory.getText().toString().equals("Ukentlig")) {
                    int week = cal.get(Calendar.WEEK_OF_YEAR);
                    if(week == 1) {
                        tvDate.setText("UKE " + week);
                    } else
                    tvDate.setText("UKE " + --week);
                } else if(tvCategory.getText().toString().equals("Månedlig")) {
                    int month = cal.get(Calendar.MONTH);
                    tvDate.setText(monthArray[month]);
                } else
                tvDate.setText(sdf.format(cal.getTime()));
                ((PlanAdapter)getListAdapter()).changeCursor(initCursor(tvCategory.getText().toString()));
                break;
            case R.id.rightButton:
                moveDate(1);
                if(tvCategory.getText().toString().equals("Ukentlig")) {
                    int week = cal.get(Calendar.WEEK_OF_YEAR);
                    if(week == 1) {
                        tvDate.setText("UKE " + week);
                    } else
                        tvDate.setText("UKE " + --week);
                } else if(tvCategory.getText().toString().equals("Månedlig")) {
                    int month = cal.get(Calendar.MONTH);
                    tvDate.setText(monthArray[month]);
                } else
                tvDate.setText(sdf.format(cal.getTime()));
                ((PlanAdapter)getListAdapter()).changeCursor(initCursor(tvCategory.getText().toString()));
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] PLAN_SUMMARY_PROJECTION = new String[]{
                PlannedEvent.KEY_PLANID,
                PlannedEvent.KEY_PLANNAME,
                PlannedEvent.KEY_DATE
        };
        CursorLoader cursorLoader = new CursorLoader(this,
                PlanContentProvider.CONTENT_URI, PLAN_SUMMARY_PROJECTION, null, null, null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


    private void moveDate(int step) {
        switch(tvCategory.getText().toString()) {
            case "Daglig":
                cal.add(Calendar.DATE, 1*step);
                break;
            case "Ukentlig":
                cal.add(Calendar.DATE, 7*step);
                break;
            case "Månedlig":
                cal.add(Calendar.MONTH, 1*step);
                break;
        }
    }

}

