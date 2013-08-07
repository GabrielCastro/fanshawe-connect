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

package ca.GabrielCastro.fanshawelogin.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * A class copied from StackOverflow to Check if a device is Rooted
 * @author Kevin Kowalewski
 */
public class Root {

    private static String TAG = Root.class.getName();

    /**
     * Check weather the device is currently rooted
     * @return
     */
    public static boolean isDeviceRooted() {
        if (checkRoot_BuildTags()) {
            Log.d(TAG, "WE has root 1");
            return true;
        }
        if (checkRoot_SuperUserApk()) {
            Log.d(TAG, "WE has root 2");
            return true;
        }
        if (checkRoot_WhichSu()) {
            Log.d(TAG, "WE has root 3");
            return true;
        }
        return false;
    }

    /**
     * Checks if the current rom is signed with test keys
     * @return
     */
    public static boolean checkRoot_BuildTags() {
        String buildTags = android.os.Build.TAGS;

        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }
        return false;
    }

    /**
     * Checks for the presence of SuperUser.apk
     * @return
     */
    public static boolean checkRoot_SuperUserApk() {
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            }
        } catch (Exception e) {
        }

        return false;
    }

    /**
     * Runs "which su" in a shell to se if we have the binary
     * @return
     */
    public static boolean checkRoot_WhichSu() {
        if (new ExecShell().executeCommand(ExecShell.SHELL_CMD.check_su_binary) != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Simple Shell
     * @author Kevin Kowalewski
     */
    public static class ExecShell {

        private static String LOG_TAG = ExecShell.class.getName();

        public ArrayList<String> executeCommand(SHELL_CMD shellCmd) {
            String line = null;
            ArrayList<String> fullResponse = new ArrayList<String>();
            Process localProcess = null;

            try {
                localProcess = Runtime.getRuntime().exec(shellCmd.command);
            } catch (Exception e) {
                return null;
                // e.printStackTrace();
            }

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(localProcess.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));

            try {
                while ((line = in.readLine()) != null) {
                    Log.d(LOG_TAG, "--> Line received: " + line);
                    fullResponse.add(line);
                }
                out.close();
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(LOG_TAG, "--> Full response was: " + fullResponse);

            return fullResponse;
        }

        public static enum SHELL_CMD {
            check_su_binary(new String[]{"/system/xbin/which", "su"});
            String[] command;

            SHELL_CMD(String[] command) {
                this.command = command;
            }
        }

    }

}