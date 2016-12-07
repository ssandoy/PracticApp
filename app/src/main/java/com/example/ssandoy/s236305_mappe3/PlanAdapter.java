package com.example.ssandoy.s236305_mappe3;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class PlanAdapter extends SimpleCursorAdapter {

    private String[] months =  {"Jan", "Feb", "Mar", "Apr", "Mai", "Juni", "Juli", "Aug", "Sept", "Okt", "Nov", "Des"};

    private LayoutInflater inflater;
    private Cursor cursor;
    private Context context;

    public PlanAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.context = context;
        cursor = c;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (cursor != null) {
            ViewHolder holder;
            cursor.moveToPosition(position);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_plan, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.name.setText(cursor.getString(cursor.getColumnIndex(PlannedEvent.KEY_PLANNAME)));

            holder.time.setText(cursor.getString(cursor.getColumnIndex(PlannedEvent.KEY_STARTTIME)) + ":" + cursor.getString(cursor.getColumnIndex(PlannedEvent.KEY_ENDTIME)));

            String[] parts = cursor.getString(cursor.getColumnIndex(PlannedEvent.KEY_DATE)).split("-");

            holder.day.setText(parts[0]);
            holder.year.setText(parts[2]);
            holder.month.setText(months[Integer.parseInt(parts[1])-1]);


        }

        return convertView;
    }

    @Override
    public Cursor swapCursor(Cursor c) {
        cursor = c;
        return super.swapCursor(c);
    }

    private class ViewHolder {

        TextView name;
        TextView time;
        TextView month;
        TextView year;
        TextView day;

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.Name);
            time = (TextView) view.findViewById(R.id.eventTime);
            month = (TextView) view.findViewById(R.id.Month);
            year = (TextView) view.findViewById(R.id.Year);
            day = (TextView) view.findViewById(R.id.Day);
        }
    }
}