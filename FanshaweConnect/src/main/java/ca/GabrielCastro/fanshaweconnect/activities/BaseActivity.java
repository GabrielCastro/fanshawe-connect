package ca.GabrielCastro.fanshaweconnect.activities;


import android.support.v7.app.ActionBarActivity;

import ca.GabrielCastro.fanshaweconnect.util.AnalyticsWrapper;

public class BaseActivity extends ActionBarActivity {

    @Override
    protected void onStart() {
        super.onStart();
        AnalyticsWrapper.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AnalyticsWrapper.getInstance(this).activityStop(this);
    }
}
