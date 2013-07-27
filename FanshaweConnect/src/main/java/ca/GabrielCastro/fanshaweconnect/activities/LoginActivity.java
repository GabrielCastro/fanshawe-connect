package ca.GabrielCastro.fanshaweconnect.activities;

import android.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import ca.GabrielCastro.fanshaweconnect.fragments.LoginFragment;
import ca.GabrielCastro.fanshaweconnect.fragments.ProgressDisplayFragment;

public class LoginActivity extends ActionBarActivity
        implements LoginFragment.CallBacks, ProgressDisplayFragment.Callbacks {

    private static final String FRAG_TAG_LOGIN = "LoginActivity.LoginFragment";
    private static final String FRAG_TAG_PROGRESS = "LoginActivity.Progress";

    LoginFragment mLoginFragment;
    Fragment mProgressFragment;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag(FRAG_TAG_LOGIN);
        mProgressFragment = getSupportFragmentManager().findFragmentByTag(FRAG_TAG_PROGRESS);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mLoginFragment == null) {
            mLoginFragment = LoginFragment.newInstance();
            ft.replace(R.id.content, mLoginFragment, FRAG_TAG_LOGIN);
        }
        if (mProgressFragment == null) {
            mProgressFragment = new ProgressDisplayFragment();
            ft.add(R.id.content, mProgressFragment, FRAG_TAG_PROGRESS)
                .hide(mProgressFragment);
        }
        ft.commit();

    }

    @Override
    public void showLoading() {
        getSupportFragmentManager()
                .beginTransaction()
                .show(mProgressFragment)
                .hide(mLoginFragment)
                .setCustomAnimations(R.anim.fade_out, R.anim.fade_in)
                .commit();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void done(boolean success) {
        if (success) {
            if (!isFinishing()) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .hide(mProgressFragment)
                    .show(mLoginFragment)
                    .setCustomAnimations(R.anim.fade_out, R.anim.fade_in)
                    .commit();
        }
    }
}