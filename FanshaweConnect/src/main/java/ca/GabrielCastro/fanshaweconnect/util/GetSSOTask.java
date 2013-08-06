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


import android.net.Uri;

public class GetSSOTask extends SupportASyncTask<Void, Void, Uri> {

    private final GetSSO delegate;
    private OnComplete cb;

    public GetSSOTask(GetSSO.Destination destination, String user, String pass, OnComplete cb) {
        delegate = new GetSSO(destination, user, pass);
        this.cb = cb;
    }

    @Override
    protected Uri doInBackground(Void... params) {
        return delegate.doGetSSO();
    }

    @Override
    protected void onPostExecute(Uri uri) {
        if (uri == null) {
            cb.onFailed();
        } else {
            cb.onGotSSO(uri);
        }
    }

    public static interface OnComplete {
        public void onGotSSO(Uri ssoUri);

        public void onFailed();
    }

}
