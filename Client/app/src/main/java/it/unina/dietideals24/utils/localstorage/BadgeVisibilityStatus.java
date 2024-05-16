package it.unina.dietideals24.utils.localstorage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class BadgeVisibilityStatus {
    private static final String FILE_BADGE_VISIBILITY_STATUS_DATA = "badge_visibility_status_data";

    private BadgeVisibilityStatus() {
    }

    public static void setBadgeVisibilityStatus(Context context, boolean status) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_BADGE_VISIBILITY_STATUS_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("status", status);
        editor.apply();
        Log.e("BADGE", "BADGE SET: " + status);
    }

    public static boolean getBadgeVisibilityStatus(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_BADGE_VISIBILITY_STATUS_DATA, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("status", false);
    }

}
