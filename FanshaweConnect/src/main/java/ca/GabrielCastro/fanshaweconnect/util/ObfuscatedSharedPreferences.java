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

package ca.GabrielCastro.fanshaweconnect.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/*
 * Warning, this gives a false sense of security.  If an attacker has enough access to 
 * acquire your password store, then he almost certainly has enough access to acquire your 
 * source binary and figure out your encryption key.  However, it will prevent casual 
 * investigators from acquiring passwords, and thereby may prevent undesired negative 
 * publicity. 
 */

/**
 * wraps a SharedPreferences object and "encrypts" all values with a standard key
 * and generated salt
 */
public class ObfuscatedSharedPreferences implements SharedPreferences {

    protected static final String UTF8 = "utf-8";
    private static final String KEY_VERSION = "OSP_version";
    // completely randomly generated
    private static final char[] SEKRIT = SecretKeyGenerator.getSecretKey();
    private final byte[] SALT;
    private final String saltHash64;
    protected SharedPreferences delegate;

    private final WeakHashMap<OnSharedPreferenceChangeListener, OnSharedPreferenceChangeListener> mDelegateMap
            = new WeakHashMap<OnSharedPreferenceChangeListener, OnSharedPreferenceChangeListener>();

    public ObfuscatedSharedPreferences(Context context, SharedPreferences delegate) {
        AnalyticsWrapper.Timer timer = AnalyticsWrapperImpl
                .getTimer(context, "ObfuscatedSharedPreferences", "create", null).start();
        this.delegate = delegate;
        try {
            PRNGFixes.apply();
            SALT = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID).getBytes(UTF8);
            saltHash64 = Base64.encodeToString(SHA1(SALT, 5), Base64.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        boolean shouldClear = false;
        try {
            shouldClear = !saltHash64.equals(this.getString(KEY_VERSION, null));
        } catch(Exception e) {
            shouldClear = true;
        } finally {
            if (shouldClear) {
                this.edit()
                        .clear()
                        .putString(KEY_VERSION, saltHash64)
                        .commit();
            }
        }
        timer.end().submit();
    }

    public static ObfuscatedSharedPreferences create(Context context, String name) {

        return new ObfuscatedSharedPreferences(
                context.getApplicationContext(),
                context.getSharedPreferences(name, Context.MODE_PRIVATE)
        );

    }

    protected static byte[] SHA1(byte[] text, int iterations) throws NoSuchAlgorithmException {
        if (iterations > 0) {
            text = SHA1(text, --iterations);
        }
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] sha1hash;
        md.update(text, 0, text.length);
        sha1hash = md.digest();
        return sha1hash;

    }

    public ObfuscatedEditor edit() {
        return new ObfuscatedEditor();
    }

    @Override
    public Map<String, ?> getAll() {
        Map<String, ?> encrypted = delegate.getAll();
        Map<String, Object> decrypted = new HashMap<String, Object>(encrypted.size());

        for (Map.Entry<String, ?> entry : encrypted.entrySet()) {
            String key = entry.getKey();
            String value = decrypt(entry.getValue().toString());
            if (value.length() == 1 && value.charAt(0) == 0) {
                value = null;
                decrypted.put(key, value);
                continue;
            }

            try {
                Boolean isTure = "ture".equalsIgnoreCase(value);
                Boolean isFalse = "false".equalsIgnoreCase(value);
                if (isTure == isFalse) {
                    throw new ParseException(value.toString(), 0);
                }
                decrypted.put(entry.getKey(), isTure);
                continue;
            } catch (ParseException e) {

            }
            try {
                Integer v = Integer.valueOf(value);
                decrypted.put(entry.getKey(), v);
                continue;
            } catch (NumberFormatException e) {

            }
            try {
                Long v = Long.valueOf(value);
                decrypted.put(entry.getKey(), v);
                continue;
            } catch (NumberFormatException e) {

            }
            try {
                Float v = Float.valueOf(value);
                decrypted.put(entry.getKey(), v);
                continue;
            } catch (NumberFormatException e) {

            }
            decrypted.put(key, value);
        }
        return decrypted;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        final String v = delegate.getString(key, null);
        return v != null ? Boolean.parseBoolean(decrypt(v)) : defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        final String v = delegate.getString(key, null);
        return v != null ? Float.parseFloat(decrypt(v)) : defValue;
    }

    @Override
    public int getInt(String key, int defValue) {
        final String v = delegate.getString(key, null);
        return v != null ? Integer.parseInt(decrypt(v)) : defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        final String v = delegate.getString(key, null);
        return v != null ? Long.parseLong(decrypt(v)) : defValue;
    }

    @Override
    public String getString(String key, String defValue) {
        final String v = delegate.getString(key, null);
        return v != null ? decrypt(v) : defValue;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        Set<String> values = delegate.getStringSet(key, null);
        if (values == null) {
            return defValues;
        }
        for (String v : new ArrayList<String>(values)) {
            values.remove(v);
            values.add(decrypt(v));
        }
        return values;
    }

    @Override
    public boolean contains(String s) {
        return delegate.contains(s);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(final OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        OnSharedPreferenceChangeListener delegateListener = new OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                onSharedPreferenceChangeListener.onSharedPreferenceChanged(ObfuscatedSharedPreferences.this, key);

            }
        };
        mDelegateMap.put(onSharedPreferenceChangeListener, delegateListener);
        delegate.registerOnSharedPreferenceChangeListener(delegateListener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        delegate.unregisterOnSharedPreferenceChangeListener(mDelegateMap.get(onSharedPreferenceChangeListener));
        mDelegateMap.remove(onSharedPreferenceChangeListener);
    }

    protected String encrypt(String value) {

        try {
            final byte[] bytes = value != null ? value.getBytes(UTF8) : new byte[0];
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SEKRIT));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
            byte[] doneFinal = pbeCipher.doFinal(bytes);
            return new String(Base64.encode(doneFinal, Base64.NO_WRAP), UTF8);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    protected String decrypt(String value) {
        try {
            final byte[] bytes = value != null ? Base64.decode(value, Base64.DEFAULT) : new byte[0];
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SEKRIT));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
            byte[] doneFinal = pbeCipher.doFinal(bytes);
            return new String(doneFinal, UTF8);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public class ObfuscatedEditor implements SharedPreferences.Editor {
        protected SharedPreferences.Editor delegate;

        private ObfuscatedEditor() {
            this.delegate = ObfuscatedSharedPreferences.this.delegate.edit();
        }

        @Override
        public ObfuscatedEditor putBoolean(String key, boolean value) {
            delegate.putString(key, encrypt(Boolean.toString(value)));
            return this;
        }

        @Override
        public ObfuscatedEditor putFloat(String key, float value) {
            delegate.putString(key, encrypt(Float.toString(value)));
            return this;
        }

        @Override
        public ObfuscatedEditor putInt(String key, int value) {
            delegate.putString(key, encrypt(Integer.toString(value)));
            return this;
        }

        @Override
        public ObfuscatedEditor putLong(String key, long value) {
            delegate.putString(key, encrypt(Long.toString(value)));
            return this;
        }

        @Override
        public ObfuscatedEditor putString(String key, String value) {
            delegate.putString(key, encrypt(value));
            return this;
        }

        @Override
        public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
            throw new UnsupportedOperationException();
        }

        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void apply() {
            delegate.apply();
        }

        @Override
        public ObfuscatedEditor clear() {
            delegate.clear();
            return this;
        }

        @Override
        public boolean commit() {
            return delegate.commit();
        }

        @Override
        public ObfuscatedEditor remove(String s) {
            delegate.remove(s);
            return this;
        }
    }

} 