/*
 * This file is part of FanshaweConnect.
 *
 * Copyright 2013 Gabriel Castro (c)
 *
 *     FanshaweConnect is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     FanshaweConnect is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with FanshaweConnect.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.GabrielCastro.fanshaweconnect.util;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import ca.GabrielCastro.fanshaweconnect.BuildConfig;

public class AnalyticsWrapperImpl extends AnalyticsWrapper {

    private static final String TAG = "AnalyticsWrapperImpl.non_free";
    private static final boolean ANALYTICS_DEBUG = BuildConfig.DEBUG;

    private static AnalyticsWrapperImpl instance;
    private final Context mApp;
    private EasyTracker mTracker;

    private AnalyticsWrapperImpl(Context context) {
        mApp = context.getApplicationContext();
        mTracker = EasyTracker.getInstance(mApp);
    }

    public static AnalyticsWrapper getInstance(Context context) {
        if (instance == null) {
            if (context == null) {
                return null;
            }
            instance = new AnalyticsWrapperImpl(context);
        }
        return instance;
    }

    public static AnalyticsWrapper.Timer getTimer(Context context, String category, String name, String label) {
        return new Timer(context, category, name, label);
    }

    @Override
    public void activityStart(Activity activity) {
        if (ANALYTICS_DEBUG) {
            Log.d(TAG,"activityStart: " + activity.getClass().getSimpleName());
        }
        if (BuildConfig.DEBUG) {
            return;
        }
        mTracker.activityStart(activity);
    }

    @Override
    public void activityStop(Activity activity) {
        if (ANALYTICS_DEBUG) {
            Log.d(TAG,"activityStop: " + activity.getClass().getSimpleName());
        }
        if (BuildConfig.DEBUG) {
            return;
        }
        mTracker.activityStop(activity);
    }

    @Override
    public void sendEvent(String category, String action, String label, long value) {
        mTracker.send(
                MapBuilder.createEvent(
                        category,
                        action,
                        label,
                        Long.valueOf(value)
                ).build()
        );
    }

    public static class Timer implements AnalyticsWrapper.Timer {

        private static final boolean DEBUG_TIMERS = true;

        private final Context context;
        private final String category;
        private final String name;
        private final String label;

        private int step = 0;
        private long mills;

        public Timer(Context context, String category, String name, String label) {
            this.category = category;
            this.name = name;
            this.label = label;
            this.context = context.getApplicationContext();
        }

        public Timer start() {
            if (DEBUG_TIMERS && step > 0) {
                throw new IllegalStateException("timer already started");
            }
            ++step;
            mills = System.currentTimeMillis();
            return this;
        }

        public Timer end() {
            if (DEBUG_TIMERS && step != 1) {
                throw new IllegalStateException(step > 1 ? "Timer already ended" : "Timer not started");
            }
            ++step;
            mills = System.currentTimeMillis() - mills;
            return this;
        }

        public Timer submit() {
            if (DEBUG_TIMERS && step < 2) {
                throw new IllegalStateException("Timer not ended");
            }
            if (ANALYTICS_DEBUG) {
                Log.d(TAG, "timer took " + mills + " ms");
                return this;
            }
            EasyTracker tracker = EasyTracker.getInstance(context);
            tracker.send(
                    MapBuilder.createTiming(category, Long.valueOf(mills), name, label).build()
            );
            return this;
        }
    }

}