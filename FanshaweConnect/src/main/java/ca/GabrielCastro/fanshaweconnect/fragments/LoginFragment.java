package ca.GabrielCastro.fanshaweconnect.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import ca.GabrielCastro.fanshaweconnect.R;
import ca.GabrielCastro.fanshawelogin.CONSTANTS;
import ca.GabrielCastro.fanshawelogin.util.CheckCredentials;

public class LoginFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener {

    private CallBacks mCallbacks;
    private LoginTask mTask;
    private View mView;
    private EditText mUserName;
    private String mUserText;
    private EditText mPassword;
    private String mPasswordText;
    private Button mLoginButton;
    private Spinner mDomainSpinner;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getParentFragment() instanceof CallBacks) {
            mCallbacks = (CallBacks) getParentFragment();
        } else if (activity instanceof CallBacks) {
            mCallbacks = (CallBacks) activity;
        } else {
            throw new IllegalStateException(
                    "No Fragment callbacks present in " + activity.getClass().getSimpleName()
                            + getParentFragment() == null ? "" : " or " + getParentFragment().getClass().getSimpleName()
            );
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_login, container, false);
        } else {
            ((ViewGroup) mView.getParent()).removeView(mView);
        }
        setRetainInstance(getParentFragment() == null);
        mUserName = (EditText) mView.findViewById(R.id.user_name);
        mPassword = (EditText) mView.findViewById(R.id.password);
        mLoginButton = (Button) mView.findViewById(R.id.sign_in_button);
        mDomainSpinner = (Spinner) mView.findViewById(R.id.domain_spinner);

        mLoginButton.setOnClickListener(this);
        mUserName.setOnEditorActionListener(this);
        mPassword.setOnEditorActionListener(this);
        ArrayAdapter adapter = (ArrayAdapter) mDomainSpinner.getAdapter();
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                attemptLogin();
                break;
        }

    }

    private void attemptLogin() {

        mUserText = mUserName.getText().toString();
        mPasswordText = mPassword.getText().toString();
        boolean failed = false;

        if (mUserText.matches(".*@.*")) {
            failed = true;
            mUserName.setError(getString(R.string.error_invalid_email));
        }
        if (TextUtils.isEmpty(mPasswordText)) {
            failed = true;
            mPassword.setError(getString(R.string.error_invalid_password));
        }

        if (failed) {
            return;
        }
        //Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
        mTask = new LoginTask(mUserText, mPasswordText);
        mTask.execute();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED && v.getId() == R.id.password) {
            attemptLogin();
            return true;
        }
        return false;
    }

    private void setFailure(CheckCredentials.FolAuthResponse code) {
        mPassword.setText(null);
        switch (code) {
            case RETURN_OK:
                mPassword.setError(null);
                break;
            case RETURN_INVALID:
                mPassword.setError(getString(R.string.error_incorrect_password));
                mPassword.requestFocus();
                break;
            case RETURN_ERROR:
                mPassword.setError("unable to verify your email");
                mPassword.requestFocus();
                break;
            case RETURN_EXCEPTION:
                mPassword.setError("unable to connect to FOL");
                mPassword.requestFocus();
                break;
        }
    }

    public static interface CallBacks {

        public void showLoading();

        public void success(String[] userNameLastName);

        public void failed(CheckCredentials.FolAuthResponse code);

        public SharedPreferences getSecurePreferences();

    }

    private class LoginTask extends CheckCredentials {

        public LoginTask(String userName, String password) {
            super(userName, password, null);
        }

        @Override
        protected void onPreExecute() {
            mCallbacks.showLoading();
        }

        @Override
        protected FolAuthResponse doInBackground(Void... params) {
            FolAuthResponse response = super.doInBackground(params);
            SharedPreferences p = mCallbacks.getSecurePreferences();
            p.edit()
                    .putString(CONSTANTS.USER_KEY, userName)
                    .putString(CONSTANTS.PASSWORD_KEY, password)
                    .commit();
            return response;
        }

        @Override
        protected void onPostExecute(CheckCredentials.FolAuthResponse code) {
            if (code == FolAuthResponse.RETURN_OK) {
                mCallbacks.success(this.name);
            } else {
                setFailure(code);
                mCallbacks.failed(code);
            }
        }

    }

}
