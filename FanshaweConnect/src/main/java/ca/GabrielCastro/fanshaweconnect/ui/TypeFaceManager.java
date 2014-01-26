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

package ca.GabrielCastro.fanshaweconnect.ui;


import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

public class TypeFaceManager {

    private static final HashMap<String, Typeface> CACHE = new HashMap<String, Typeface>(2);

    private TypeFaceManager() {

    }

    public static Typeface get(Context context, String name) {
        Typeface typeface = CACHE.get(name);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), name);
            CACHE.put(name, typeface);
        }
        return typeface;
    }

}
