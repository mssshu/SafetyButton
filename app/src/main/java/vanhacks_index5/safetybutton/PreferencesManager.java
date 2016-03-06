package vanhacks_index5.safetybutton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private static final String PREF_NAME = "AOP_PREFS";
    private static final String KEY_TOKEN = "remember_token";
    private static final String KEY_USERID = "user_id";
    private static final String KEY_NUMBER = "number";

    private static PreferencesManager sInstance;
    private final SharedPreferences mPref;

    private PreferencesManager(Context context) {
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesManager(context);
        }
    }

    public static synchronized PreferencesManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(PreferencesManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return sInstance;
    }

    @SuppressLint("CommitPrefEdits")
    public void setToken(String value) {
        mPref.edit()
                .putString(KEY_TOKEN, value)
                .commit();
    }

    public String getToken() {
        return mPref.getString(KEY_TOKEN, "");
    }

    @SuppressLint("CommitPrefEdits")
    public void setUserID(String value) {
        mPref.edit()
                .putString(KEY_USERID, value)
                .commit();
    }

    public String getUserID() {
        return mPref.getString(KEY_USERID, "");
    }

    @SuppressLint("CommitPrefEdits")
    public void setNumber(String value) {
        mPref.edit()
                .putString(KEY_NUMBER, value)
                .commit();
    }

    public String getNumber() {
        return mPref.getString(KEY_NUMBER, "");
    }

    @SuppressLint("CommitPrefEdits")
    public void remove(String key) {
        mPref.edit()
                .remove(key)
                .commit();
    }

    public boolean clear() {
        return mPref.edit()
                .clear()
                .commit();
    }
}