package ca.GabrielCastro.fanshawelogin.util;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class CheckCredentials extends AsyncTask<Void, Void, Integer> {
    public static final int RETURN_OK = 0;
    public static final int RETURN_INVALID = 1;
    public static final int RETURN_ERROR = 2;
    public static final int RETURN_EXCEPTION = 3;
    private static final String TAG = "checkCred AsyncTask";
    private String userName;
    private String password;
    private OnCredentialsChecked cb;
    private String[] name = {};

    public CheckCredentials(String userName, String password, OnCredentialsChecked cb) {
        this.userName = userName;
        this.password = password;
        this.cb = cb;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("https://www.fanshaweonline.ca/d2l/lp/auth/login/login.d2l");
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            {
                pairs.add(new BasicNameValuePair("ostype", ""));
                pairs.add(new BasicNameValuePair("btype", ""));
                pairs.add(new BasicNameValuePair("bversion", ""));
                pairs.add(new BasicNameValuePair("userName", this.userName));
                pairs.add(new BasicNameValuePair("password", this.password));
                pairs.add(new BasicNameValuePair("Login", "Login"));
            }
            post.setEntity(new UrlEncodedFormEntity(pairs));

            HttpResponse response = client.execute(post);
            Log.d(TAG, "response code " + response.getStatusLine().getStatusCode());
            return doParseContent(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
            return RETURN_EXCEPTION;
        }
    }

    private Integer doParseContent(String content) {
        if (content.contains("alert( 'Invalid Username\\/Password.\\n\\nYou will be taken back to the login page.' );")) {
            return RETURN_INVALID;
        }

        // FirstName:'Gabriel',LastName:'Castro Londono',UserName:
        String nameStart = "FirstName:'";
        String nameSplit = "',LastName:'";
        String nameEnd = "',UserName:";
        try {
            int startName = content.indexOf(nameStart) + nameStart.length();
            int endName = content.indexOf(nameEnd, startName);
            name = content.substring(startName, endName).split(nameSplit);
        } catch (ArrayIndexOutOfBoundsException e) {
            return RETURN_ERROR;
        }
        return RETURN_OK;
    }

    @Override
    protected void onPostExecute(Integer result) {
        cb.credentialsChecked(result, this.name);
    }
}
