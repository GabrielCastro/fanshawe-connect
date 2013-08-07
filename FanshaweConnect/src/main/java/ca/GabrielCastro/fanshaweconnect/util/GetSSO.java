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

package ca.GabrielCastro.fanshaweconnect.util;

import android.net.Uri;
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
import eu.masconsult.android_ntlm.NTLMSchemeFactory;

/**
 * Gets Single Sign-On Uri's for FOL from the portal's fol_pass_thru.aspx
 */
public class GetSSO {

    public static enum Destination {
        FOL,
        EMAIL;
        //PORTAL;
    }

    private static final String TAG = "FanConnect[getSSO]";
    private final String requestURL;
    private final String user, pass;

    /**
     * Construct a Reusable Object to get SSO Uri's
     * <br/> note: the password is never transited as we use NTLM Auth
     * @param destination Where in FOL the link should endup
     * @param user The username to authenticate
     * @param pass The password to authenticate
     */
    public GetSSO(Destination destination, String user, String pass) {
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
    }

    /**
     * Get's an SSO Uri or null if there are any errors
     * @return SSO uri or null
     */
    protected Uri doGetSSO() {

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

}
