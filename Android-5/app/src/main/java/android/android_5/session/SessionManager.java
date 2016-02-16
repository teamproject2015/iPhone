package android.android_5.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * SessionManager is used to handle the session of Application
 */
public class SessionManager {

    private SharedPreferences prefs;

    public SessionManager(Context cntx) {
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public String getUserName() {
        return prefs.getString("userName", "");
    }

    public void setUserName(String userName) {
        prefs.edit().putString("userName", userName).apply();
    }
}
