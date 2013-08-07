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

/**
 * {@inheritDoc}
 */
public abstract class SupportASyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    public SupportASyncTask<Params, Progress, Result> executeOnPool(Params... args) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return (SupportASyncTask<Params, Progress, Result>) this.executeOnExecutor(THREAD_POOL_EXECUTOR, args);
        } else {
            return (SupportASyncTask<Params, Progress, Result>) this.execute(args);
        }
    }

}
