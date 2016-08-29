package com.johnmillercoding.hometrashaudit.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    // LogCat tag
    private static final String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    private final SharedPreferences pref;

    private final Editor editor;

    // Shared pref mode
    private final int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "HomeTrashAudit";

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_UNIT = "unit";

    public SessionManager(Context context) {
        Context _context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.apply();
    }

    public void setLoggedIn(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setUsername(String username){

        editor.putString(KEY_USERNAME, username);

        editor.commit();

        Log.d(TAG, "Username retrieved!");
    }

    public void setUnit(String unit){

        editor.putString(KEY_UNIT, unit);

        editor.commit();

        Log.d(TAG, "Unit retrieved!");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    public String getUsername() { return pref.getString(KEY_USERNAME, ""); }
    public String getUnit() { return pref.getString(KEY_UNIT, ""); }
}