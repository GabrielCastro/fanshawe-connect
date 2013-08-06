package ca.GabrielCastro.fanshaweconnect.util;


import android.net.Uri;
import android.os.AsyncTask;

public class GetSSOTask extends AsyncTask<Void, Void, Uri> {

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
