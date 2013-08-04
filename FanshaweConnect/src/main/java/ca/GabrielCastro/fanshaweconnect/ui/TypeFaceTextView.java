package ca.GabrielCastro.fanshaweconnect.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import java.io.IOException;

import ca.GabrielCastro.fanshaweconnect.R;

public class TypeFaceTextView extends TextView {

    private String typeFace;

    public TypeFaceTextView(Context context) {
        super(context, null);
        init(context);
    }

    public TypeFaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttr(context, attrs);
        init(context);
    }

    public TypeFaceTextView(Context context, AttributeSet attrs, int defStyle) {
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
        try {
            context.getAssets().open(typeFace);
            this.setTypeface(Typeface.createFromAsset(context.getAssets(), typeFace));
        } catch (IOException e) {
            throw new RuntimeException(typeFace + " is not a valid asset", e);
        }
    }


}
