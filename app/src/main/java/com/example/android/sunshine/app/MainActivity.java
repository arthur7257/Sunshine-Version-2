/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.sync.SunshineSyncAdapter;

public class MainActivity extends ActionBarActivity implements
        ForecastFragment.Callbacks {

    private static final String FORECAST_FRAG_TAG = ForecastFragment.class.getSimpleName();
    private static final String DETAIL_FRAG_TAG = DetailFragment.class.getSimpleName();
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private String mLocation;
    private boolean mTwoPaneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTwoPaneLayout = findViewById(R.id.detail_pane) != null;
        addForecastFragment(savedInstanceState, !mTwoPaneLayout);
        SunshineSyncAdapter.initializeSyncAdapter(this);
    }

    private void addForecastFragment(Bundle savedInstanceState, boolean useTodaySpecialLayout) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                                       .add(R.id.forecast_pane,
                                            ForecastFragment.newInstance(useTodaySpecialLayout),
                                            FORECAST_FRAG_TAG)
                                       .commit();
        }
    }

    private void replaceDetailFragment(long date) {
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.detail_pane,
                                            DetailFragment.newInstance(date),
                                            DETAIL_FRAG_TAG)
                                   .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLocationChange();
    }

    private void checkLocationChange() {
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        String storedLocation = sharedPrefs.getString(
                getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
        if (storedLocation.equals(mLocation)) {
            return;
        }
        mLocation = storedLocation;
        ForecastFragment forecastFragment = (ForecastFragment) getSupportFragmentManager()
                .findFragmentByTag(FORECAST_FRAG_TAG);
        forecastFragment.onLocationChanged();

        DetailFragment detailFragment = ((DetailFragment) getSupportFragmentManager()
                .findFragmentByTag(DETAIL_FRAG_TAG));
        if (detailFragment != null) {
            detailFragment.onLocationChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onForecastItemClick(long date) {
        if (mTwoPaneLayout) {
            replaceDetailFragment(date);
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailActivity.DATE_KEY, date);
            startActivity(intent);
        }
    }
}
