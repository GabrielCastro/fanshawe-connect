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

package ca.GabrielCastro.fanshaweconnect.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import ca.GabrielCastro.fanshaweconnect.R;
import ca.GabrielCastro.fanshaweconnect.util.GetSSO;
import ca.GabrielCastro.fanshaweconnect.util.GetSSOTask;
import ca.GabrielCastro.fanshaweconnect.util.ObfuscatedSharedPreferences;
import ca.GabrielCastro.fanshawelogin.CONSTANTS;
import ca.GabrielCastro.fanshawelogin.util.CheckCredentials;
import ca.GabrielCastro.fanshawelogin.util.OnCredentialsChecked;

public class MainActivity extends ActionBarActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, GetSSOTask.OnComplete, MenuItem.OnMenuItemClickListener, OnCredentialsChecked {

    public static final String TAG = "FanConnect";
    private TextView mConnectingText;
    private CheckBox mAutoConnectSetting;
    private Button mGoToFOL;
    private Button mGoToEmail;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] userPass = getIntent().getExtras().getStringArray("a");
        ((TextView) findViewById(R.id.hello_world)).setText(getString(R.string.person_name, userPass[0], userPass[1]));

        mConnectingText = (TextView) findViewById(R.id.connected);
        mAutoConnectSetting = (CheckBox) findViewById(R.id.wifi_check);
        mGoToFOL = (Button) findViewById(R.id.go_fol);
        mGoToEmail = (Button) findViewById(R.id.go_email);

        mConnectingText.setText(R.string.login_progress_connecting);
        mConnectingText.setTextColor(getResources().getColor(R.color.holo_yellow));
        mAutoConnectSetting.setOnCheckedChangeListener(this);
        mGoToFOL.setOnClickListener(this);
        mGoToEmail.setOnClickListener(this);

        mPrefs = ObfuscatedSharedPreferences.create(this, CONSTANTS.PREFS_NAME);

        String user = mPrefs.getString(CONSTANTS.KEY_USERNAME, null);
        String pass = mPrefs.getString(CONSTANTS.KEY_PASSWD, null);
        if (user != null && pass != null) {
            new CheckCredentials(user, pass, this).execute();
        } else {
            logout(LoginActivity.Reasons.CORRUPT_PREF);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_logout).setOnMenuItemClickListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mPrefs.edit().putBoolean(CONSTANTS.KEY_AUTO_CONNECT, isChecked).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_fol:
                launchFOL(GetSSO.Destination.FOL);
                break;
            case R.id.go_email:
                launchFOL(GetSSO.Destination.EMAIL);
                break;
        }
    }

    private void launchFOL(GetSSO.Destination destination) {
        String user = mPrefs.getString(CONSTANTS.KEY_USERNAME, null);
        String pass = mPrefs.getString(CONSTANTS.KEY_PASSWD, null);
        if (user == null || pass == null) {
            logout(LoginActivity.Reasons.CORRUPT_PREF);
            return;
        }
        GetSSOTask getSSO = new GetSSOTask(destination, user, pass, this);
        getSSO.executeOnPool((Void) null);
    }

    @Override
    public void onGotSSO(Uri ssoUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, ssoUri);
        if (getPackageManager().queryIntentActivities(intent, 0).size() < 1) {
            Toast.makeText(this, "no web browser", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(intent);
    }

    @Override
    public void onFailed() {
        Toast.makeText(this, "get SSO Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout(LoginActivity.Reasons.USER_LOGGED_OUT);
                return true;
        }
        return false;
    }

    private void logout(LoginActivity.Reasons why) {
        ObfuscatedSharedPreferences.create(MainActivity.this, CONSTANTS.PREFS_NAME).edit().clear().commit();
        startActivity(LoginActivity.getIntent(this, why));
        MainActivity.this.finish();
    }

    @Override
    public void credentialsChecked(CheckCredentials.FolAuthResponse result, String[] name) {
        switch (result) {
            case RETURN_ERROR:
            case RETURN_EXCEPTION:
                mConnectingText.setText("Can't Connect");
                mConnectingText.setTextColor(getResources().getColor(R.color.fanshawe_red));
                break;
            case RETURN_INVALID:
                logout(LoginActivity.Reasons.INLAID_PASS);
                break;
            case RETURN_OK:
                mConnectingText.setText(R.string.connected);
                mConnectingText.setTextColor(getResources().getColor(R.color.holo_green));
                break;
        }
    }
}
