package it.unina.dietideals24.utils.localstorage;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.List;

import it.unina.dietideals24.model.DietiUser;

public class LocalDietiUser {
    private static final String FILE_NAME_DIETI_USER = "local_dieti_user_data";
    private static final String LINKS = "links";

    private LocalDietiUser() {
    }

    public static void setLocalDietiUser(Context context, DietiUser dietiUser) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME_DIETI_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("id", dietiUser.getId());
        editor.putString("name", dietiUser.getName());
        editor.putString("surname", dietiUser.getSurname());
        editor.putString("email", dietiUser.getEmail());
        editor.putString("password", dietiUser.getPassword());
        if (dietiUser.getLinks() == null)
            editor.putString(LINKS, "");
        else
            editor.putString(LINKS, dietiUser.getLinks().toString());
        editor.putString("geographicalArea", dietiUser.getGeographicalArea());
        editor.putString("biography", dietiUser.getBiography());
        editor.putString("profilePictureUrl", dietiUser.getProfilePictureUrl());
        editor.apply();
    }

    public static void deleteLocalDietiUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME_DIETI_USER, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    public static DietiUser getLocalDietiUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME_DIETI_USER, Context.MODE_PRIVATE);
        List<String> links = Collections.singletonList(sharedPreferences.getString(LINKS, ""));
        return new DietiUser(
                sharedPreferences.getLong("id", 0),
                sharedPreferences.getString("name", ""),
                sharedPreferences.getString("surname", ""),
                sharedPreferences.getString("email", ""),
                sharedPreferences.getString("password", ""),
                sharedPreferences.getString("biography", ""),
                links,
                sharedPreferences.getString("geographicalArea", ""),
                sharedPreferences.getString("profilePictureUrl", "")
        );
    }
}
