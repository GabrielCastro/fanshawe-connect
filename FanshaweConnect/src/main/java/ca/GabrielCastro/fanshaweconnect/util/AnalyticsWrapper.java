package ca.GabrielCastro.fanshaweconnect.util;

import android.app.Activity;
import android.content.Context;

public abstract class AnalyticsWrapper {

    protected AnalyticsWrapper() {
    }

    public static AnalyticsWrapper getInstance(Context context) {
        return AnalyticsWrapperImpl.getInstance(context);
    }

    public interface Timer {

        public void start();

        public void end();

        public void submit();

    }

    public abstract void activityStart(Activity activity);

    public abstract void activityStop(Activity activity);

    public abstract void sendEvent(String category, String action, String label, long value);


}
