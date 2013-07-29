package ca.GabrielCastro.fanshaweconnect.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import ca.GabrielCastro.fanshaweconnect.R;
import ca.GabrielCastro.fanshaweconnect.util.ObfuscatedSharedPreferences;
import ca.GabrielCastro.fanshawelogin.CONSTANTS;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] userPass = getIntent().getExtras().getStringArray("a");

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < userPass.length; ++i) {
            builder.append('[')
                    .append(i)
                    .append("] : ")
                    .append(userPass[i])
                    .append('\n');
        }
        SharedPreferences p = ObfuscatedSharedPreferences.create(this, CONSTANTS.PREFS_NAME);
        builder
                .append("user : ").append(p.getString("user", "no_user"))
                .append('\n')
                .append("pass : ").append(p.getString("pass", "no_pass"))
                .append('\n');
        ((TextView) findViewById(R.id.hello_world)).setText(builder.toString());
    }

}
