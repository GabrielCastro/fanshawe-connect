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
