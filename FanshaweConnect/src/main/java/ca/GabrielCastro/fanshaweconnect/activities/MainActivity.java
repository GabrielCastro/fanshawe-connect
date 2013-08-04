package ca.GabrielCastro.fanshaweconnect.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import ca.GabrielCastro.fanshaweconnect.R;
import ca.GabrielCastro.fanshaweconnect.util.ObfuscatedSharedPreferences;
import ca.GabrielCastro.fanshawelogin.CONSTANTS;

public class MainActivity extends ActionBarActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private CheckBox mAutoConnectSetting;
    private Button mGoToFOL;
    private Button mGoToEmail;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] userPass = getIntent().getExtras().getStringArray("a");
        ((TextView) findViewById(R.id.hello_world)).setText(getString(R.string.welcome, userPass[0], userPass[1]));

        mAutoConnectSetting = (CheckBox) findViewById(R.id.wifi_check);
        mGoToFOL = (Button) findViewById(R.id.go_fol);
        mGoToEmail = (Button) findViewById(R.id.go_email);

        mAutoConnectSetting.setOnCheckedChangeListener(this);
        mGoToFOL.setOnClickListener(this);
        mGoToEmail.setOnClickListener(this);

        mPrefs = ObfuscatedSharedPreferences.create(this, CONSTANTS.PREFS_NAME);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mPrefs.edit().putBoolean(CONSTANTS.KEY_AUTO_CONNECT, isChecked).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
