package com.example.expensetracker.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "ExpenseTrackerSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ID = "userId";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Save user login session
     * @param username The logged in username
     */
    public void saveLogin(String username) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    /**
     * Save user login session with user ID
     * @param username The logged in username
     * @param userId The user ID
     */
    public void saveLogin(String username, int userId) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    /**
     * Check if user is logged in
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get current logged in username
     * @return username if logged in, null otherwise
     */
    public String getUsername() {
        if (isLoggedIn()) {
            return sharedPreferences.getString(KEY_USERNAME, null);
        }
        return null;
    }

    /**
     * Get current logged in user ID
     * @return user ID if logged in, -1 otherwise
     */
    public int getUserId() {
        if (isLoggedIn()) {
            return sharedPreferences.getInt(KEY_USER_ID, -1);
        }
        return -1;
    }

    /**
     * Clear user session (logout)
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }

    /**
     * Check login status and get username in one call
     * @return username if logged in, null if not logged in
     */
    public String getLoggedInUsername() {
        return isLoggedIn() ? getUsername() : null;
    }
}