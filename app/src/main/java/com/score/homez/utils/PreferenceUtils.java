package com.score.homez.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.score.homez.R;
import com.score.homez.exceptions.NoUserException;
import com.score.senzc.pojos.User;

/**
 * Utility class to deal with Share Preferences
 *
 * @author erangaeb@gmail.com (eranga herath)
 */
public class PreferenceUtils {

    /**
     * Save user credentials in shared preference
     *
     * @param context application context
     * @param user    logged-in user
     */
    public static void saveUser(Context context, User user) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        //keys should be constants as well, or derived from a constant prefix in a loop.
        editor.putString("id", user.getId());
        editor.putString("username", user.getUsername());
        editor.commit();
    }

    /**
     * Get user details from shared preference
     *
     * @param context application context
     * @return user object
     */
    public static User getUser(Context context) throws NoUserException {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String id = preferences.getString("id", "0");
        String username = preferences.getString("username", "");

        if (username.isEmpty())
            throw new NoUserException();

        User user = new User(id, username);
        user.setUsername(username);
        return user;
    }

}
