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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {

    // Format used for storing dates in the database.  ALso used for converting those strings
    // back into date objects for comparison/processing.
    public static final String DATE_FORMAT = "yyyyMMdd";

    /**
     * Helper method to convert the database representation of the date into something to display
     * to users.  As classy and polished a user experience as "20140102" is, we can do better.
     *
     * @param context Context to use for resource localization
     * @param dateInMillis The date in milliseconds
     * @return a user-friendly representation of the date.
     */
    public static String getFriendlyDayString(Context context, long dateInMillis) {
        // The day string for forecast uses the following logic:
        // For today: "Today, June 8"
        // For tomorrow:  "Tomorrow"
        // For the next 5 days: "Wednesday" (just the day name)
        // For all days after that: "Mon Jun 8"

        Time time = new Time();
        time.setToNow();
        long currentTime = System.currentTimeMillis();
        int julianDay = Time.getJulianDay(dateInMillis, time.gmtoff);
        int currentJulianDay = Time.getJulianDay(currentTime, time.gmtoff);

        // If the date we're building the String for is today's date, the format
        // is "Today, June 24"
        if (julianDay == currentJulianDay) {
            String today = context.getString(R.string.today);
            int formatId = R.string.format_full_friendly_date;
            return String.format(context.getString(
                    formatId,
                    today,
                    getFormattedMonthDay(context, dateInMillis)));
        } else if ( julianDay < currentJulianDay + 7 ) {
            // If the input date is less than a week in the future, just return the day name.
            return getDayName(context, dateInMillis);
        } else {
            // Otherwise, use the form "Mon Jun 3"
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(dateInMillis);
        }
    }

    /**
     * Given a day, returns just the name to use for that day.
     * E.g "today", "tomorrow", "wednesday".
     *
     * @param context Context to use for resource localization
     * @param dateInMillis The date in milliseconds
     * @return
     */
    public static String getDayName(Context context, long dateInMillis) {
        // If the date is today, return the localized version of "Today" instead of the actual
        // day name.

        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        if (julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        } else if ( julianDay == currentJulianDay +1 ) {
            return context.getString(R.string.tomorrow);
        } else {
            Time time = new Time();
            time.setToNow();
            // Otherwise, the format is just the day of the week (e.g "Wednesday".
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(dateInMillis);
        }
    }

    /**
     * Converts db date format to the format "Month day", e.g "June 24".
     * @param context Context to use for resource localization
     * @param dateInMillis The db formatted date string, expected to be of the form specified
     *                in Utility.DATE_FORMAT
     * @return The day in the form of a string formatted "December 6"
     */
    public static String getFormattedMonthDay(Context context, long dateInMillis ) {
        Time time = new Time();
        time.setToNow();
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(Utility.DATE_FORMAT);
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
        String monthDayString = monthDayFormat.format(dateInMillis);
        return monthDayString;
    }

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),
                               context.getString(R.string.pref_location_default));
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_units_key),
                               context.getString(R.string.pref_units_metric))
                    .equals(context.getString(R.string.pref_units_metric));
    }

    public static boolean isDisplayNotificationsActivated(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.pref_display_notif_key), true);
    }

    public static String formatTemperature(Context context, double temperature, boolean isMetric) {
        double temp;
        if ( !isMetric ) {
            temp = 9*temperature/5+32;
        } else {
            temp = temperature;
        }
        return context.getString(R.string.format_temperature, temp);
    }

    static String formatDate(long dateInMillis) {
        Date date = new Date(dateInMillis);
        return DateFormat.getDateInstance().format(date);
    }

    public static String formatWind(Context context, float windSpeed, float degrees, boolean isMetric) {
        final String[] directionsText = { "N", "NE", "E", "SE", "S", "SW", "W", "NW" };
        final int DEGREES_TOTAL = 360;
        final int DIR_TOTAL = 8;

        String direction = directionsText[Math.round(degrees / (DEGREES_TOTAL / DIR_TOTAL)) % DIR_TOTAL];
        int res;
        if (isMetric) {
            res = R.string.format_wind_kmh;
        } else {
            res = R.string.format_wind_mph;
            windSpeed = getImperialWindSpeed(windSpeed);
        }
        return context.getString(res, windSpeed, direction);
    }

    public static float getImperialWindSpeed(float metricWindSpeed) {
        return .621371192237334f * metricWindSpeed;
    }

    private static final String[] CONDITION_ID_REGEX = {"^2[0-9]+$",
                                                        "^3[0-9]+$",
                                                        "^5[0-9]+$",
                                                        "^6[0-9]+$",
                                                        "^7[0-6]+$",
                                                        "^7[7-9]+$",
                                                        "^800$",
                                                        "^80[0-9]+$",
                                                        "^90[0-9]+$"};

    private static final int[] ART_ARRAY = {R.drawable.art_storm,
                                            R.drawable.art_light_rain,
                                            R.drawable.art_rain,
                                            R.drawable.art_snow,
                                            R.drawable.art_fog,
                                            R.drawable.art_storm,
                                            R.drawable.art_clear,
                                            R.drawable.art_light_clouds,
                                            R.drawable.art_clouds};

    private static final int[] ICON_ARRAY = {R.drawable.ic_storm,
                                             R.drawable.ic_light_rain,
                                             R.drawable.ic_rain,
                                             R.drawable.ic_snow,
                                             R.drawable.ic_fog,
                                             R.drawable.ic_storm,
                                             R.drawable.ic_clear,
                                             R.drawable.ic_light_clouds,
                                             R.drawable.ic_cloudy};

    private static final int RES_TYPE_ART = 0;
    private static final int RES_TYPE_ICON = 1;

    private static int getDrawableWeatherCondition(int resType, String conditionId) {
        int res = -1;
        for (int i = 0; i < CONDITION_ID_REGEX.length; i++) {
            if (conditionId.matches(CONDITION_ID_REGEX[i])) {
                res = resType == RES_TYPE_ART ? ART_ARRAY[i] : ICON_ARRAY[i];
                break;
            }
        }
        return res;
    }

    public static int getArtWeatherCondition(String conditionId) {
        return getDrawableWeatherCondition(RES_TYPE_ART, conditionId);
    }

    public static int getIconWeatherCondition(String conditionId) {
        return getDrawableWeatherCondition(RES_TYPE_ICON, conditionId);
    }
}