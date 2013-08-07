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

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import ca.GabrielCastro.fanshaweconnect.App;
import ca.GabrielCastro.fanshaweconnect.util.SupportASyncTask;
import ca.GabrielCastro.fanshawelogin.CONSTANTS;
import eu.masconsult.android_ntlm.NTLMSchemeFactory;

/**
 * A One Use Request that runs in a Background Thread to Verify a UserName/Password,
 * or reuseable if #doInThisThread is called directly
 */
public class CheckCredentials extends SupportASyncTask<Void, Void, CheckCredentials.FolAuthResponse> {

    private static final String TAG = "checkCred AsyncTask";
    protected String userName;
    protected String password;
    protected String[] name = {"", ""};
    private OnCredentialsChecked cb;

    /**
     * Constructs a request to CheckCredentials with a CallBack that will only
     * be called if the request is ran in a background thread through {@link #execute(Object[])}
     * @param userName
     * @param password
     * @param cb
     */
    public CheckCredentials(String userName, String password, OnCredentialsChecked cb) {
        this.userName = userName;
        this.password = password;
        this.cb = cb;
    }

    /**
     * Used by the ASyncTask implementation to do work in an other thread
     * @see #doInThisThread()
     * @param params
     * @return
     */
    @Override
    protected FolAuthResponse doInBackground(Void... params) {
        return doInThisThread();
    }

    /**
     * Will attempt to authenticate the user on portal.myfanshawec.ca using NTLM
     * if that is successful the it will attempt to extract the users real name from FOL
     * using the portal's SSO fol_pass_thru.aspx
     * @return One of {@link FolAuthResponse}
     */
    public FolAuthResponse doInThisThread() {
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            client.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.TRUE);
            client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, App.userAgent);
            // register ntlm auth scheme
            client.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
            client.getCredentialsProvider().setCredentials(
                    // Limit the credentials only to the specified domain and port
                    new AuthScope("portal.myfanshawe.ca", -1),
                    // Specify credentials, most of the time only user/pass is needed
                    new NTCredentials(userName, password, "", "")
            );

            HttpPost post = new HttpPost("https://portal.myfanshawe.ca/_layouts/Fanshawe/fol_pass_thru.aspx");
            HttpContext localContext = new BasicHttpContext();
            // Bind custom cookie store to the local context
            localContext.setAttribute(ClientContext.COOKIE_STORE, new BasicCookieStore());

            HttpResponse response = client.execute(post, localContext);
            if (!isLoggedIn(response)) {
                return FolAuthResponse.RETURN_INVALID;
            }

            response = client.execute(new HttpGet(CONSTANTS.SECOND_URL), localContext);
            Log.d(TAG, "response code " + response.getStatusLine().getStatusCode());
            return doParseContent(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
            return FolAuthResponse.RETURN_EXCEPTION;
        }
    }

    /**
     * Checks the response code and returns true if it is {@code 200}
     * @param response the response to check
     * @return true is the status code is 200
     */
    private boolean isLoggedIn(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == 200;
    }

    /**
     * Extracts the persons name from FOL
     * @param content the HTML content of the page at {@link CONSTANTS#SECOND_URL}
     * @return {@link FolAuthResponse#RETURN_OK} if the name was extracted correctly
     * or {@link FolAuthResponse#RETURN_ERROR} if the name could not be parsed
     */
    private FolAuthResponse doParseContent(String content) {
        // FirstName:'Gabriel',LastName:'Castro Londono',UserName:
        String nameStart = "FirstName:'";
        String nameSplit = "',LastName:'";
        String nameEnd = "',UserName:";
        try {
            int startName = content.indexOf(nameStart) + nameStart.length();
            int endName = content.indexOf(nameEnd, startName);
            name = content.substring(startName, endName).split(nameSplit);
        } catch (ArrayIndexOutOfBoundsException e) {
            return FolAuthResponse.RETURN_ERROR;
        }
        return FolAuthResponse.RETURN_OK;
    }

    /**
     * Used by the ASyncTask implementation to trigger the callback
     * @param result
     */
    @Override
    protected void onPostExecute(FolAuthResponse result) {
        if (cb != null) {
            cb.credentialsChecked(result, this.name);
        }
    }

    /**
     * Gets the persons name parsed from FOL
     * if the name has not yet been successfully parsed
     * two empty strings will be returned
     *
     * @return
     */
    public String[] getName() {
        return this.name;
    }

    /**
     * Contains all the possible Authentication Responses
     */
    public static enum FolAuthResponse {
        RETURN_OK,
        RETURN_INVALID,
        RETURN_ERROR,
        RETURN_EXCEPTION
    }
}
