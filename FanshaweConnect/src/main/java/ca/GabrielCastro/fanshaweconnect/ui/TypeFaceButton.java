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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import ca.GabrielCastro.fanshaweconnect.R;

/**
 * An Extension of Button that reads the type_face attribute
 * from xml and sets it's type face using the value and
 * {@link Typeface#createFromAsset(android.content.res.AssetManager, String)}
 */
@SuppressLint("Instantiatable")
public class TypeFaceButton extends Button {

    private String typeFace;

    public TypeFaceButton(Context context) {
        super(context, null);
        init(context);
    }

    public TypeFaceButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttr(context, attrs);
        init(context);
    }

    public TypeFaceButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseAttr(context, attrs);
        init(context);
    }

    private void parseAttr(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TypeFaceTextView);
        for (int i = 0; i < ta.getIndexCount(); ++i) {
            int idx = ta.getIndex(i);
            switch (idx) {
                case R.styleable.TypeFaceTextView_type_face:
                    typeFace = ta.getString(idx);
                    break;
            }
        }
        ta.recycle();
    }

    private void init(Context context) {
        if (this.isInEditMode() || typeFace == null) {
            return;
        }
        this.setTypeface(TypeFaceManager.get(context, typeFace));
    }


}
