package ca.GabrielCastro.fanshawelogin.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import ca.GabrielCastro.fanshawelogin.CONSTANTS;

public class LogOnRequest extends AsyncTask<String, Integer, Integer> {

    public static final int RETURN_OK = 0;
    public static final int RETURN_NOT_AT_FANSHAWE = 1;
    public static final int RETURN_AT_FANSHAWE_OK = 2;
    public static final int RETURN_UNABLE_TO_LOGIN = 3;
    public static final int RETURN_CONNECTION_ERROR = 4;
    public static final int RETURN_USPECIFIED_ERROR = 5;
    public static final int RETURN_INVALID_CRED = 6;
    public static final int RETURN_NO_WIFI = 7;
    // public static final int RETURN_
    public static final String TAG = "LogOnRequest";
    /**
     * *** begin return codes for test uri *****
     */
    private static final int testUri_CONNECTION_OK = 8;
    private static final int testUri_FANSHAWE_REDIRECT = 9;
    private static final int testUri_OTHER_REDIRECT = 10;
    /**
     * *** end return codes for test uri *******
     */
    private static final int testUri_NULL_ENTITY = 11;
    /**
     * *** begin return codes for do Logon *****
     */
    private static final int doLog__OK = 12;
    private static final int doLog__LOGED_ON = 13;
    private static final int doLog__INVALID_USER_PASS = 14;
    private static final int doLog__OTHER = 15;
    /**
     * *** end return codes for do Logon *****
     */

    private static final String SSID_REGEX = "^\"Fanshawe (Students|Employees)\"$";
    private static final URI TEST_URI;
    private static final URI LOGON_URL;
    private LoggedOnListener cb;
    private Context context;
    private String userName = "";
    private String password = "";

    static {
        URI test = null;
        URI logon = null;
        try {
            test = new URI("http://www.apple.com/library/test/success.html");
            logon = new URI("https://virtualwireless.fanshawec.ca/login.html");
        } catch (URISyntaxException e) {
            // WONT HAPPEN, THE VALUES ARE HARD CODED
        }
        TEST_URI = test;
        LOGON_URL = logon;
    }

    /**
     * TODO
     *
     * @param listener
     * @param userName
     * @param password
     * @param context
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    public LogOnRequest(LoggedOnListener listener, String userName, String password, Context context) {

        this.userName = userName;
        this.password = password;
        this.cb = listener;
        this.context = context;

    }

    /**
     * TODO
     *
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    private static int checkTestURI() throws ClientProtocolException, IOException {

        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(TEST_URI);
        HttpResponse response = client.execute(get);

        // check if the server response is NULL
        if (response.getEntity() != null) {

            String sResponse = EntityUtils.toString(response.getEntity());

            // check if we got a redirect
            if (sResponse.contains("<TITLE>Success</TITLE>")) {
                return testUri_CONNECTION_OK;
            } else if (sResponse.contains("<TITLE> Web Authentication Redirect</TITLE>") && sResponse.contains("URL=https://virtualwireless.fanshawec.ca/login.html?")) { // DETECT
                // FANSHAWE
                return testUri_FANSHAWE_REDIRECT;
            }
            return testUri_OTHER_REDIRECT;
        }
        return testUri_NULL_ENTITY;
    }

    /**
     * TODO
     */
    @Override
    protected Integer doInBackground(String... args) {
        if (Tools.isDebugLevelSet(CONSTANTS.DEBUG_THREAD_LONGER)) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
        }

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        if (wifiInfo == null || ssid == null) {
            return RETURN_NO_WIFI;
        }

        // Check the ssid
        if (!ssid.matches(SSID_REGEX)) {
            Log.d(TAG, "ssid: |" + ssid + "| regex: |" + SSID_REGEX + "|");
            return RETURN_NOT_AT_FANSHAWE;
        }
        if (true) { // TODO if checking MAC
            byte[] macConnected = new byte[6];
            String[] macString = wifiInfo.getBSSID().split(":");
            for (int i = 0; i < macString.length; i++) {
                macConnected[i] = Tools.hexStringToByteArray(macString[i])[0];
            }
            // TODO acctually chack the mac
        }
        try {

            if (checkTestURI() != testUri_CONNECTION_OK) {
                switch (doLogon()) {
                    case doLog__OK:
                        break;
                    case doLog__LOGED_ON:
                    case doLog__OTHER:
                        return RETURN_UNABLE_TO_LOGIN;
                    case doLog__INVALID_USER_PASS:
                        return RETURN_INVALID_CRED;
                }
                switch (checkTestURI()) {
                    case testUri_CONNECTION_OK:
                        return RETURN_OK;
                    case testUri_FANSHAWE_REDIRECT:
                    case testUri_OTHER_REDIRECT:
                    case testUri_NULL_ENTITY:
                        return RETURN_UNABLE_TO_LOGIN;
                }
            }

            return RETURN_UNABLE_TO_LOGIN;
        } catch (ClientProtocolException e) {
            return RETURN_CONNECTION_ERROR;
        } catch (IOException e) {
            return RETURN_CONNECTION_ERROR;
        } catch (URISyntaxException e) {
            // this wont happen cause all the uri's are hard coded for now
        } catch (Exception e) {
            return RETURN_USPECIFIED_ERROR;
        }
        return RETURN_USPECIFIED_ERROR;
    }

    /**
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    private int doLogon() throws URISyntaxException, IOException {

        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        {
            pairs.add(new BasicNameValuePair("buttonClicked", "4"));
            pairs.add(new BasicNameValuePair("err_flag", "0"));
            pairs.add(new BasicNameValuePair("err_mas", ""));
            pairs.add(new BasicNameValuePair("info_flag", "0"));
            pairs.add(new BasicNameValuePair("info_msg", ""));
            pairs.add(new BasicNameValuePair("redirect_url", ""));
            pairs.add(new BasicNameValuePair("username", this.userName));
            pairs.add(new BasicNameValuePair("password", this.password));
        }
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(LOGON_URL);
        post.setEntity(new UrlEncodedFormEntity(pairs));
        HttpEntity entity = client.execute(post).getEntity();
        String response = EntityUtils.toString(entity);
        if (response.contains("You are already logged in. No further action is required on your part.")) {
            return doLog__LOGED_ON;
        }
        if (response.contains("<input type=\"hidden\" name=\"err_flag\" size=\"16\" maxlength=\"15\" value=\"1\">")) {
            return doLog__INVALID_USER_PASS;
        }
        if (response.contains("<title>Logged In</title>")) {
            return doLog__OK;
        }
        return doLog__OTHER;
    }

    /**
     * TODO
     */
    @Override
    protected void onPostExecute(Integer result) {
        if (cb != null)
            cb.loggedOn(result);
    }

}
