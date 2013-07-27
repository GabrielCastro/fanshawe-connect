package ca.GabrielCastro.fanshaweconnect.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import ca.GabrielCastro.fanshaweconnect.R;

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
        mTask = new LoginTask();
        mTask.execute();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        v.setError(null);
        return false;
    }

    private class LoginTask extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected void onPreExecute() {
            mCallbacks.showLoading();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {

            }
            return mPasswordText.equals("bannana");
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mCallbacks.done(success);
        }

    }

    public static interface CallBacks {

        public void showLoading();

        public void done(boolean success);

    }

}
