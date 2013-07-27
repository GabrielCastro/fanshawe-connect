package ca.GabrielCastro.fanshawelogin.util;

import ca.GabrielCastro.fanshawelogin.CONSTANTS;

public class Tools {

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static boolean isDebugLevelSet(byte level) {
        return (level == (CONSTANTS.DEBUG_LEVEL & level));
    }

}
