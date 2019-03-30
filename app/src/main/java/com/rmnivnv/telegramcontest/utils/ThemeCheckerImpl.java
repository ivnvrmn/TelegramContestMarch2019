package com.rmnivnv.telegramcontest.utils;

import android.content.SharedPreferences;

public class ThemeCheckerImpl implements ThemeChecker {

    private static final String KEY_IS_DARK_THEME = "isDarkTheme";
    private SharedPreferences preferences;

    public ThemeCheckerImpl(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public void setDarkTheme(boolean isDarkTheme) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_IS_DARK_THEME, isDarkTheme);
        editor.apply();
    }

    @Override
    public boolean isDarkTheme() {
        return preferences.getBoolean(KEY_IS_DARK_THEME, false);
    }
}
