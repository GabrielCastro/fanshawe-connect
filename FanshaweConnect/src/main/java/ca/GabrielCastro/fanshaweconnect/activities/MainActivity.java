package ca.GabrielCastro.fanshaweconnect.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import ca.GabrielCastro.fanshaweconnect.R;
import ca.GabrielCastro.fanshaweconnect.util.GetSSO;
import ca.GabrielCastro.fanshaweconnect.util.ObfuscatedSharedPreferences;
import ca.GabrielCastro.fanshawelogin.CONSTANTS;

public class MainActivity extends ActionBarActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, GetSSO.OnComplete {

    public static final String TAG = "FanConnect";
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
            Toast.makeText(this, "unable to read username from settings", Toast.LENGTH_SHORT).show();
            return;
        }
        GetSSO getSSO = new GetSSO(destination, user, pass, this);
        getSSO.execute((Void) null);
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
}
