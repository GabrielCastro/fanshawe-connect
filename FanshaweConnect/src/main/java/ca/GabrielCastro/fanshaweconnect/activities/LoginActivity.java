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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.inputmethod.InputMethodManager;

import ca.GabrielCastro.fanshaweconnect.R;
import ca.GabrielCastro.fanshaweconnect.fragments.LoginFragment;
import ca.GabrielCastro.fanshaweconnect.fragments.ProgressDisplayFragment;
import ca.GabrielCastro.fanshaweconnect.util.ObfuscatedSharedPreferences;
import ca.GabrielCastro.fanshawelogin.CONSTANTS;
import ca.GabrielCastro.fanshawelogin.util.CheckCredentials;

public class LoginActivity extends ActionBarActivity
        implements LoginFragment.CallBacks, ProgressDisplayFragment.Callbacks, CONSTANTS {

    private static final String EXTRA_REASON = "LoginActivity.extra_reason";
    public static enum Reasons {
        INIT,
        USER_LOGGED_OUT,
        INLAID_PASS,
        CORRUPT_PREF
    }


    private static final String FRAG_TAG_LOGIN = "LoginActivity.LoginFragment";
    private static final String FRAG_TAG_PROGRESS = "LoginActivity.Progress";

    LoginFragment mLoginFragment;
    Fragment mProgressFragment;
    private Reasons mReason = Reasons.INIT;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] userFirstLastName = new String[2];
        SharedPreferences secPrefs = getSecurePreferences();
        userFirstLastName[0] = secPrefs.getString(KEY_FIRST_NAME, null);
        userFirstLastName[1] = secPrefs.getString(KEY_LAST_NAME, null);
        if (userFirstLastName[0] != null && userFirstLastName[1] != null) {
            startMain(userFirstLastName);
            return;
        }

        mLoginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag(FRAG_TAG_LOGIN);
        mProgressFragment = getSupportFragmentManager().findFragmentByTag(FRAG_TAG_PROGRESS);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mLoginFragment == null) {
            mLoginFragment = LoginFragment.newInstance();
            ft.replace(android.R.id.content, mLoginFragment, FRAG_TAG_LOGIN);
        }
        if (mProgressFragment == null) {
            mProgressFragment = new ProgressDisplayFragment();
            ft.add(android.R.id.content, mProgressFragment, FRAG_TAG_PROGRESS)
                .hide(mProgressFragment);
        }
        ft.commit();

        getReasonFromIntent();
    }

    private void getReasonFromIntent() {
        String name = getIntent().getStringExtra(EXTRA_REASON);
        if (name == null) {
            name = Reasons.INIT.name();
        }
        mReason = Reasons.valueOf(name);
        mLoginFragment.setReason(mReason);
    }

    @Override
    public void showLoading() {
        getSupportFragmentManager()
                .beginTransaction()
                .show(mProgressFragment)
                .hide(mLoginFragment)
                .setCustomAnimations(R.anim.abc_fade_out, R.anim.abc_fade_in)
                .commit();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    private void startMain(String[] userNameLastName) {
        if (!isFinishing()) {
            startActivity(MainActivity.IntentWithPersonName(this, userNameLastName));
            finish();
        }
    }

    @Override
    public void success(String[] userNameLastName) {
        getSecurePreferences().edit()
                .putString(KEY_FIRST_NAME, userNameLastName[0])
                .putString(KEY_LAST_NAME, userNameLastName[1])
                .commit();
        startMain(userNameLastName);
    }

    @Override
    public void failed(CheckCredentials.FolAuthResponse code) {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(mProgressFragment)
                .show(mLoginFragment)
                .setCustomAnimations(R.anim.abc_fade_out, R.anim.abc_fade_in)
                .commit();
    }

    @Override
    public SharedPreferences getSecurePreferences() {
        return ObfuscatedSharedPreferences.create(this, CONSTANTS.PREFS_NAME);
    }

    public static Intent getIntent(Context context, Reasons reason) {
        return new Intent(context, LoginActivity.class)
                .putExtra(EXTRA_REASON, reason.name());
    }
}