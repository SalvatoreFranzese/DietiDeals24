package it.unina.dietideals24.utils.localstorage;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManagement {
    private static final String TOKEN_KEY = "token";
    private static final String EXPIRATION_KEY = "expiration";
    private static TokenManagement token = null;
    private static SharedPreferences sharedPreferences = null;

    private TokenManagement(Context context) {
        setSharedPreferences(context);
    }

    public static synchronized TokenManagement getInstance(Context context) {
        if (token == null) {
            token = new TokenManagement(context);
        }
        return token;
    }

    public static String getToken() {
        if (sharedPreferences == null)
            return "";
        return sharedPreferences.getString(TOKEN_KEY, "");
    }

    public static void deleteTokenData() {
        if (sharedPreferences != null)
            sharedPreferences.edit().clear().apply();
    }

    public static boolean isExpired() {
        if (sharedPreferences != null)
            return sharedPreferences.getLong(EXPIRATION_KEY, 0) < System.currentTimeMillis();

        return true;
    }

    public static long getTokenExpiration() {
        if (sharedPreferences == null)
            return 0;
        return sharedPreferences.getLong(EXPIRATION_KEY, 0);
    }

    private synchronized void setSharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("token_data", Context.MODE_PRIVATE);
    }

    public void setToken(String token, long expiresIn) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply();
        sharedPreferences.edit().putLong(EXPIRATION_KEY, System.currentTimeMillis() + expiresIn).apply();
    }
}
