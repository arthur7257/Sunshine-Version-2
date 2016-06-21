package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * Created by arturo.ayala on 6/9/16.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
            };

    private static final int COL_DATE = 0;
    private static final int COL_DESC = 1;
    private static final int COL_MAX = 2;
    private static final int COL_MIN = 3;
    private static final int COL_HUMIDITY = 4;
    private static final int COL_WIND_SPEED = 5;
    private static final int COL_WIND_DEGREES = 6;
    private static final int COL_PRESSURE = 7;
    private static final int COL_WEATHER_CONDITION_ID = 8;

    private static final int DETAIL_LOADER_ID = 0;
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String DATE_KEY = "DATE_KEY";
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private TextView mTxtDateDesc;
    private TextView mTxtDate;
    private TextView mTxtMax;
    private TextView mTxtMin;
    private TextView mTxtDesc;
    private TextView mTxtHumidity;
    private TextView mTxtWind;
    private TextView mTxtPressure;
    private Uri mDataUri;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    public static DetailFragment newInstance(long date) {
        Bundle args = new Bundle();
        args.putLong(DATE_KEY, date);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            setUpDataUri(args.getLong(DATE_KEY));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mTxtDateDesc = ((TextView) rootView.findViewById(R.id.txt_date_desc));
        mTxtDate = ((TextView) rootView.findViewById(R.id.txt_date));
        mTxtMax = ((TextView) rootView.findViewById(R.id.txt_max));
        mTxtMin = ((TextView) rootView.findViewById(R.id.txt_min));
        mTxtDesc = ((TextView) rootView.findViewById(R.id.txt_weather_desc));
        mTxtHumidity = ((TextView) rootView.findViewById(R.id.txt_humidity));
        mTxtWind = ((TextView) rootView.findViewById(R.id.txt_wind));
        mTxtPressure = ((TextView) rootView.findViewById(R.id.txt_pressure));
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                                mDataUri,
                                DETAIL_COLUMNS,
                                null,
                                null,
                                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || !data.moveToFirst()) {
            return;
        }
        long date = data.getLong(COL_DATE);
        mTxtDateDesc.setText(Utility.getDayName(getActivity(), date));
        mTxtDate.setText(Utility.getFormattedMonthDay(getActivity(), date));
        mTxtDesc.setText(data.getString(COL_DESC));
        String conditionId = data.getString(COL_WEATHER_CONDITION_ID);
        mTxtDesc.setCompoundDrawablesWithIntrinsicBounds(0,
                                                         Utility.getArtWeatherCondition(conditionId),
                                                         0,
                                                         0);
        boolean isMetric = Utility.isMetric(getActivity());
        mTxtMax.setText(Utility.formatTemperature(getActivity(),
                                                  data.getDouble(COL_MAX),
                                                  isMetric));
        mTxtMin.setText(Utility.formatTemperature(getActivity(),
                                                  data.getDouble(COL_MIN),
                                                  isMetric));
        double humidity = data.getDouble(COL_HUMIDITY);
        mTxtHumidity.setText(getActivity().getString(R.string.format_humidity, humidity));
        mTxtWind.setText(Utility.formatWind(getActivity(),
                                            data.getFloat(COL_WIND_SPEED),
                                            data.getFloat(COL_WIND_DEGREES),
                                            isMetric));
        double pressure = data.getDouble(COL_PRESSURE);
        mTxtPressure.setText(getActivity().getString(R.string.format_pressure, pressure));

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void setUpDataUri(long date) {
        String location = Utility.getPreferredLocation(getActivity());
        mDataUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(location, date);
    }

    public void onLocationChanged() {
        setUpDataUri(WeatherContract.WeatherEntry.getDateFromUri(mDataUri));
        getLoaderManager().restartLoader(DETAIL_LOADER_ID, null, this);
    }
}
