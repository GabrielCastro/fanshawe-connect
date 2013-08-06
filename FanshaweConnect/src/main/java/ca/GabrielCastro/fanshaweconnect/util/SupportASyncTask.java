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

import android.os.AsyncTask;
import android.os.Build;

public abstract class SupportASyncTask<P,S,R> extends AsyncTask<P,S,R> {

    public SupportASyncTask<P,S,R> executeOnPool(P...args) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return (SupportASyncTask<P, S, R>) this.executeOnExecutor(THREAD_POOL_EXECUTOR, args);
        } else {
            return (SupportASyncTask<P, S, R>) this.execute(args);
        }
    }



}
