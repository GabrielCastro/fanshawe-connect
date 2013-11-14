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

public class AnalyticsWrapperImpl extends AnalyticsWrapper {

    @Override
    public void activityStart(Activity activity) {

    }

    @Override
    public void activityStop(Activity activity) {

    }

    @Override
    public void sendEvent(String category, String action, String label, long value) {

    }

    public static AnalyticsWrapper.Timer getTimer(Context context, String category, String name, String label) {
        return new Timer();
    }

    public static class Timer implements AnalyticsWrapper.Timer {

        public Timer start() {
            return this;
        }

        public Timer end() {
            return this;
        }

        public Timer submit() {
            return this;

        }
    }
}