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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import ca.GabrielCastro.fanshaweconnect.R;
import ca.GabrielCastro.fanshaweconnect.fragments.MainFragment;
import ca.GabrielCastro.fanshaweconnect.util.ObfuscatedSharedPreferences;
import ca.GabrielCastro.fanshawelogin.CONSTANTS;
import ca.GabrielCastro.fanshawelogin.util.CheckCredentials;
import ca.GabrielCastro.fanshawelogin.util.OnCredentialsChecked;

public class MainActivity extends BaseActivity implements
        OnCredentialsChecked,
        MainFragment.CallBacks,
        MenuItem.OnMenuItemClickListener {

    public static final String TAG = "FanConnect.MainActivity";
    public static final String EXTRA_PERSON_NAME = "fanshaweconnect.MainActivity.personName";


    private TextView mConnectingText;
    private CheckBox mAutoConnectSetting;
    private Button mGoToFOL;
    private Button mGoToEmail;
    private SharedPreferences mPrefs;
    private CheckCredentials.FolAuthResponse mLastAuth;

    public static Intent IntentWithPersonName(Context from, String[] personName) {
        return new Intent(from, MainActivity.class).putExtra(EXTRA_PERSON_NAME, personName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] personName = getIntent().getExtras().getStringArray(EXTRA_PERSON_NAME);
        Fragment retained = getSupportFragmentManager().findFragmentByTag(TAG);
        if (retained == null) {
            retained = MainFragment.newInstance(personName);
            retained.setRetainInstance(true);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, retained, TAG)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_logout).setOnMenuItemClickListener(this);
        return super.onCreateOptionsMenu(menu);
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
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout(LoginActivity.Reasons.USER_LOGGED_OUT);
                return true;
        }
        return false;
    }

    public void logout(LoginActivity.Reasons why) {
        ObfuscatedSharedPreferences.create(MainActivity.this, CONSTANTS.PREFS_NAME).edit().clear().commit();
        startActivity(LoginActivity.getIntent(this, why));
        MainActivity.this.finish();
    }

    @Override
    public void credentialsChecked(CheckCredentials.FolAuthResponse result, String[] name) {

    }
}
