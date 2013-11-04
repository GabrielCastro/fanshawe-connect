package ca.GabrielCastro.fanshaweconnect.util.pref;

import ca.GabrielCastro.betterpreferences.preftypes.BooleanPreference;
import ca.GabrielCastro.betterpreferences.preftypes.StringPreference;
import ca.GabrielCastro.fanshawelogin.CONSTANTS;

public interface AvailablePrefs {

    StringPreference  USER_NAME    = new StringPreference(CONSTANTS.KEY_USERNAME, null);
    StringPreference  PASS_WORD    = new StringPreference(CONSTANTS.KEY_PASSWD, null);
    StringPreference  FIRST_NAME   = new StringPreference(CONSTANTS.KEY_FIRST_NAME, null);
    StringPreference  LAST_NAME    = new StringPreference(CONSTANTS.KEY_LAST_NAME, null);
    BooleanPreference AUTO_CONNECT = new BooleanPreference(CONSTANTS.KEY_AUTO_CONNECT, true);

}
