package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.DrawableRes;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    public static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG,
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;
    public static final int COL_WEATHER_CONDITION_ID = 6;
    public static final int COL_COORD_LAT = 7;
    public static final int COL_COORD_LONG = 8;

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    private boolean mUseTodayLayout = true;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 && mUseTodayLayout ? VIEW_TYPE_TODAY : VIEW_TYPE_DAY;
    }

    /*
                Remember that these views are reused as needed.
             */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutRes;
        switch (getItemViewType(cursor.getPosition())) {
            case VIEW_TYPE_TODAY:
                layoutRes = R.layout.list_item_forecast_today;
                break;
            case VIEW_TYPE_DAY:
            default:
                layoutRes = R.layout.list_item_forecast;
        }
        View view = LayoutInflater.from(context).inflate(layoutRes, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = ((ViewHolder) view.getTag());
        holder.txtDate.setText(Utility.getFriendlyDayString(context, cursor.getLong(COL_WEATHER_DATE)));

        holder.txtDesc.setText(cursor.getString(COL_WEATHER_DESC));

        boolean isMetric = Utility.isMetric(context);
        holder.txtMax.setText(Utility.formatTemperature(context,
                                                        cursor.getDouble(COL_WEATHER_MAX_TEMP),
                                                        isMetric));
        holder.txtMin.setText(Utility.formatTemperature(context,
                                                        cursor.getDouble(COL_WEATHER_MIN_TEMP),
                                                        isMetric));

        holder.imgIcon.setImageResource(getWeatherIcon(getItemViewType(cursor.getPosition()),
                                                       cursor.getString(COL_WEATHER_CONDITION_ID)));
    }

    private int getWeatherIcon(int viewType, String conditionId) {
        int res = -1;
        switch (viewType) {
            case VIEW_TYPE_TODAY:
                res = Utility.getArtWeatherCondition(conditionId);
                break;
            case VIEW_TYPE_DAY:
                res = Utility.getIconWeatherCondition(conditionId);
                break;
        }
        return res;
    }

    private static class ViewHolder {
        TextView txtDate;
        TextView txtDesc;
        TextView txtMax;
        TextView txtMin;
        ImageView imgIcon;

        ViewHolder(View view) {
            txtDate = (TextView) view.findViewById(R.id.txt_date);
            txtDesc = (TextView) view.findViewById(R.id.txt_weather_desc);
            txtMax = (TextView) view.findViewById(R.id.txt_max);
            txtMin = (TextView) view.findViewById(R.id.txt_min);
            imgIcon = ((ImageView) view.findViewById(R.id.img_weather));
        }
    }
}