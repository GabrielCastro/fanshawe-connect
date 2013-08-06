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

public class LogOnRequest extends AsyncTask<String, Integer, LogOnRequest.Status> {

    public static enum Status {
        RETURN_OK,
        RETURN_NOT_AT_FANSHAWE,
        RETURN_AT_FANSHAWE_OK,
        RETURN_UNABLE_TO_LOGIN,
        RETURN_CONNECTION_ERROR,
        RETURN_USPECIFIED_ERROR,
        RETURN_INVALID_CRED,
        RETURN_NO_WIFI
    }

    public static final String TAG = "LogOnRequest";
    /**
     * *** begin return codes for test uri *****
     */
    private static enum TestUriStatus {
        testUri_CONNECTION_OK,
        testUri_FANSHAWE_REDIRECT,
        testUri_OTHER_REDIRECT,
        testUri_NULL_ENTITY;
    }

    /**
     * *** begin return codes for do Logon *****
     */
    private static enum LogOnResult {
        OK ,
        LOGGED_ON,
        INVALID_USER_PASS,
        OTHER_ERROR
    }

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
    private static TestUriStatus checkTestURI() throws IOException {

        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(TEST_URI);
        HttpResponse response = client.execute(get);

        // check if the server response is NULL
        if (response.getEntity() != null) {

            String sResponse = EntityUtils.toString(response.getEntity());

            // check if we got a redirect
            if (sResponse.contains("<TITLE>Success</TITLE>")) {
                return TestUriStatus.testUri_CONNECTION_OK;
            } else if (sResponse.contains("<TITLE> Web Authentication Redirect</TITLE>") && sResponse.contains("URL=https://virtualwireless.fanshawec.ca/login.html?")) { // DETECT
                // FANSHAWE
                return TestUriStatus.testUri_FANSHAWE_REDIRECT;
            }
            return TestUriStatus.testUri_OTHER_REDIRECT;
        }
        return TestUriStatus.testUri_NULL_ENTITY;
    }

    /**
     * TODO
     */
    @Override
    protected Status doInBackground(String... args) {
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
            return Status.RETURN_NO_WIFI;
        }

        // Check the ssid
        if (!ssid.matches(SSID_REGEX)) {
            Log.d(TAG, "ssid: |" + ssid + "| regex: |" + SSID_REGEX + "|");
            return Status.RETURN_NOT_AT_FANSHAWE;
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

            if (checkTestURI() != TestUriStatus.testUri_CONNECTION_OK) {
                switch (doLogon()) {
                    case OK:
                        break;
                    case LOGGED_ON:
                    case OTHER_ERROR:
                        return Status.RETURN_UNABLE_TO_LOGIN;
                    case INVALID_USER_PASS:
                        return Status.RETURN_INVALID_CRED;
                }
                switch (checkTestURI()) {
                    case testUri_CONNECTION_OK:
                        return Status.RETURN_OK;
                    case testUri_FANSHAWE_REDIRECT:
                    case testUri_OTHER_REDIRECT:
                    case testUri_NULL_ENTITY:
                        return Status.RETURN_UNABLE_TO_LOGIN;
                }
            }

            return Status.RETURN_UNABLE_TO_LOGIN;
        } catch (IOException e) {
            return Status.RETURN_CONNECTION_ERROR;
        } catch (URISyntaxException e) {
            // this wont happen cause all the uri's are hard coded for now
        } catch (Exception e) {
            return Status.RETURN_USPECIFIED_ERROR;
        }
        return Status.RETURN_USPECIFIED_ERROR;
    }

    /**
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    private LogOnResult doLogon() throws URISyntaxException, IOException {

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
            return LogOnResult.LOGGED_ON;
        }
        if (response.contains("<input type=\"hidden\" name=\"err_flag\" size=\"16\" maxlength=\"15\" value=\"1\">")) {
            return LogOnResult.INVALID_USER_PASS;
        }
        if (response.contains("<title>Logged In</title>")) {
            return LogOnResult.OK;
        }
        return LogOnResult.OTHER_ERROR;
    }

    /**
     * TODO
     */
    @Override
    protected void onPostExecute(Status result) {
        if (cb != null)
            cb.loggedOn(result);
    }

}
