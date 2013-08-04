package ca.GabrielCastro.fanshaweconnect.util;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.URI;

import ca.GabrielCastro.fanshaweconnect.App;
import ca.GabrielCastro.fanshawelogin.CONSTANTS;
import eu.masconsult.android_ntlm.NTLMSchemeFactory;

public class GetSSO extends AsyncTask<Void,Void,Uri> {

    public static enum Destination {
        FOL,
        EMAIL;
        //PORTAL;
    }

    private static final String TAG = "FanConnect[getSSO]";
    private final String requestURL;
    private final String user, pass;
    private final OnComplete cb;

    public GetSSO(Destination destination, String user, String pass, OnComplete cb) {
        switch (destination) {
            case FOL:
                requestURL = "https://portal.myfanshawe.ca/_layouts/Fanshawe/fol_pass_thru.aspx";
                break;
            case EMAIL:
                requestURL = "https://portal.myfanshawe.ca/_layouts/Fanshawe/fol_pass_thru.aspx?dest=inbox";
                break;
            default:
                throw new IllegalArgumentException(destination.toString() + " is not a valid destination");
        }
        this.user = user;
        this.pass = pass;
        this.cb = cb;
    }


    @Override
    protected Uri doInBackground(Void... params) {

        if (user == null || pass == null) {
            return null;
        }

        DefaultHttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, App.userAgent);
        // register ntlm auth scheme
        client.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
        client.getCredentialsProvider().setCredentials(
                // Limit the credentials only to the specified domain and port
                new AuthScope("portal.myfanshawe.ca", -1),
                // Specify credentials, most of the time only user/pass is needed
                new NTCredentials(user, pass, "", "")
        );

        final String[] ssoUrl = {null};
        final RedirectHandler defaultHandler = client.getRedirectHandler();
        client.setRedirectHandler(new RedirectHandler() {
            @Override
            public boolean isRedirectRequested(HttpResponse httpResponse, HttpContext httpContext) {
                Log.i(TAG, "isRedirectRequested");
                for (Header header : httpResponse.getAllHeaders()) {
                    String name = header.getName();
                    String value = header.getValue();
                    if ("Location".equals(name)) {
                        ssoUrl[0] = value;
                    }
                }
                return false;
            }

            @Override
            public URI getLocationURI(HttpResponse httpResponse, HttpContext httpContext) throws ProtocolException {
                return defaultHandler.getLocationURI(httpResponse, httpContext);
            }
        });

        HttpGet folSSO = new HttpGet(requestURL);

        try {
            HttpResponse response = client.execute(folSSO);
            HttpEntity entity = response.getEntity();
            entity.consumeContent();
            Log.i(TAG, "SSO OK");
        } catch (IOException e) {
            return null;
        }

        if (ssoUrl[0] == null) {
            return null;
        }
        return Uri.parse(ssoUrl[0]);
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
