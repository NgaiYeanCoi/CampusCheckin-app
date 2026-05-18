package cn.nyc1.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import cn.nyc1.myapplication.model.LoginResponse;

public class SessionManager {
    private static final String PREFS = "campus_checkin_session";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";
    private static final String KEY_PROFILE_ID = "profile_id";
    private static final String KEY_DISPLAY_NAME = "display_name";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void saveLogin(LoginResponse response) {
        preferences.edit()
                .putString(KEY_TOKEN, response.token)
                .putLong(KEY_USER_ID, response.userId == null ? -1 : response.userId)
                .putString(KEY_USERNAME, response.username)
                .putString(KEY_ROLE, response.role)
                .putLong(KEY_PROFILE_ID, response.profileId == null ? -1 : response.profileId)
                .putString(KEY_DISPLAY_NAME, response.displayName)
                .apply();
    }

    public boolean isLoggedIn() {
        return getToken() != null && !getToken().isEmpty();
    }

    public String getToken() {
        return preferences.getString(KEY_TOKEN, "");
    }

    public String getUsername() {
        return preferences.getString(KEY_USERNAME, "");
    }

    public String getRole() {
        return preferences.getString(KEY_ROLE, "");
    }

    public String getDisplayName() {
        return preferences.getString(KEY_DISPLAY_NAME, "");
    }

    public long getProfileId() {
        return preferences.getLong(KEY_PROFILE_ID, -1);
    }

    public String authHeader() {
        return getToken();
    }

    public void clear() {
        preferences.edit().clear().apply();
    }
}
