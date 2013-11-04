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

package ca.GabrielCastro.fanshaweconnect.fragments;


import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Stack;

import ca.GabrielCastro.betterpreferences.MyPreferences;
import ca.GabrielCastro.fanshaweconnect.R;
import ca.GabrielCastro.fanshaweconnect.activities.LoginActivity;
import ca.GabrielCastro.fanshaweconnect.util.GetSSO;
import ca.GabrielCastro.fanshaweconnect.util.GetSSOTask;
import ca.GabrielCastro.fanshaweconnect.util.pref.AvailablePrefs;
import ca.GabrielCastro.fanshawelogin.util.CheckCredentials;
import ca.GabrielCastro.fanshawelogin.util.OnCredentialsChecked;

public class MainFragment extends BaseFragment implements
        OnCredentialsChecked,
        CompoundButton.OnCheckedChangeListener,
        View.OnClickListener,
        GetSSOTask.OnComplete {


    public static final String TAG = "FanConnect.MainFragment";
    public static final String EXTRA_PERSON_NAME = "fanshaweconnect.MainActivity.personName";


    private TextView mConnectingText;
    private CheckBox mAutoConnectSetting;
    private Button mGoToFOL;
    private Button mGoToEmail;
    private CheckCredentials.FolAuthResponse mLastAuth;
    private CallBacks mCB;
    private final Stack<WithCallbacks> mCallBackStack = new Stack<WithCallbacks>();

    public static MainFragment newInstance(String[] personName) {
        MainFragment instance = new MainFragment();
        Bundle args = new Bundle(1);
        args.putStringArray(EXTRA_PERSON_NAME, personName);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getParentFragment() instanceof CallBacks) {
            mCB = (CallBacks) getParentFragment();
        } else if (activity instanceof CallBacks) {
            mCB = (CallBacks) activity;
        } else {
            throw new IllegalStateException("NO callbacks");
        }
        tryPopStack();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_main, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        String user = MyPreferences.read(mApp, AvailablePrefs.USER_NAME);
        String pass = MyPreferences.read(mApp, AvailablePrefs.PASS_WORD);

        if (user == null || pass == null) {
            logout(LoginActivity.Reasons.CORRUPT_PREF);
            return;
        }
        new CheckCredentials(user, pass, this).execute();

        String[] userPass = getArguments().getStringArray(EXTRA_PERSON_NAME);
        ((TextView) view.findViewById(R.id.hello_world)).setText(getString(R.string.person_name, userPass[0], userPass[1]));

        mConnectingText = (TextView) view.findViewById(R.id.connected);
        mAutoConnectSetting = (CheckBox) view.findViewById(R.id.wifi_check);
        mGoToFOL = (Button) view.findViewById(R.id.go_fol);
        mGoToEmail = (Button) view.findViewById(R.id.go_email);

        mConnectingText.setText(R.string.login_progress_connecting);
        mConnectingText.setTextColor(getResources().getColor(R.color.holo_yellow));

        mAutoConnectSetting.setChecked(MyPreferences.read(mApp, AvailablePrefs.AUTO_CONNECT));
        mAutoConnectSetting.setOnCheckedChangeListener(this);
        mGoToFOL.setOnClickListener(this);
        mGoToEmail.setOnClickListener(this);
        mGoToFOL.setTextColor(Color.GRAY);
        mGoToEmail.setTextColor(Color.GRAY);


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCB = null;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        MyPreferences.set(mApp, AvailablePrefs.AUTO_CONNECT, isChecked);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_fol:
                if (mLastAuth == CheckCredentials.FolAuthResponse.RETURN_OK) {
                    launchFOL(GetSSO.Destination.FOL);
                } else {
                    Toast.makeText(mApp, "Can't do that until we connect", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.go_email:
                if (mLastAuth == CheckCredentials.FolAuthResponse.RETURN_OK) {
                    launchFOL(GetSSO.Destination.EMAIL);
                } else {
                    Toast.makeText(mApp, "Can't do that until we connect", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void launchFOL(GetSSO.Destination destination) {
        String user = MyPreferences.read(mApp, AvailablePrefs.USER_NAME);
        String pass = MyPreferences.read(mApp, AvailablePrefs.PASS_WORD);
        if (user == null || pass == null) {
            logout(LoginActivity.Reasons.CORRUPT_PREF);
            return;
        }
        GetSSOTask getSSO = new GetSSOTask(destination, user, pass, this);
        getSSO.executeOnPool((Void) null);
    }

    @Override
    public void onGotSSO(final Uri ssoUri) {
        mCallBackStack.push(new WithCallbacks() {
            @Override
            protected void onCallbacks(CallBacks cb) {
                cb.onGotSSO(ssoUri);
            }
        });
        tryPopStack();
    }

    private void tryPopStack() {
        if (mCB != null) {
            while (!mCallBackStack.empty())
                mCallBackStack.pop().run();
        }
    }

    @Override
    public void onFailed() {
        Toast.makeText(mApp, "get SSO Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void credentialsChecked(CheckCredentials.FolAuthResponse result, String[] name) {
        mLastAuth = result;
        int color = Color.BLACK;
        switch (result) {
            case RETURN_ERROR:
            case RETURN_EXCEPTION:
                mConnectingText.setText("Can't Connect");
                mConnectingText.setTextColor(getResources().getColor(R.color.fanshawe_red));
                color = Color.GRAY;
                break;
            case RETURN_INVALID:
                logout(LoginActivity.Reasons.INLAID_PASS);
                return;
            case RETURN_OK:
                mConnectingText.setText(R.string.connected);
                mConnectingText.setTextColor(getResources().getColor(R.color.holo_green));
                color = Color.BLACK;
                break;
        }
        mGoToFOL.setTextColor(color);
        mGoToEmail.setTextColor(color);
    }

    private void logout(final LoginActivity.Reasons why) {
        mCallBackStack.push(new WithCallbacks() {
            @Override
            protected void onCallbacks(CallBacks cb) {
                cb.logout(why);
            }
        });
        tryPopStack();
    }

    public interface CallBacks {

        public void onGotSSO(Uri ssoUri);

        public void logout(LoginActivity.Reasons why);

    }

    private abstract class WithCallbacks implements Runnable {
        @Override
        public void run() {
            onCallbacks(mCB);
        }
        protected abstract void onCallbacks(CallBacks cb);
    }

}
