package pis03_2016.savealltherobots.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Data that is conserved through Shared Preferences and reloaded with the game
 */
public class PersistentData {

    /**
     * Name of the file stores the preferences.
     */
    static public final String PREFERENCES_FILE = "PreferencesFile";
    /**
     * Boolean that indicates if it's the first time playing
     */
    private static boolean isUserWelcomed = false;
    /**
     * user's best score
     */
    private static long highscore;

    /**
     * user's amount of robots saved when achieving the highscore
     */
    private static int highscoreSavedRobotsAmount;

    /**
     * user's amount of levels completed when achieving the highscore
     */
    private static int highscoreCompletedLevelsAmount;

    /**
     * user's amount of levels completed when achieving the highscore
     */
    private static int backgroundResourceId;

    /**
     * whether music should be enabled
     */
    private static boolean musicEnabled;

    /**
     * whether effects should be enabled
     */
    private static boolean effectsEnabled;

    /**
     * whether portrait inventory position is in the upper part of the screen
     */
    private static boolean portraitInventoryUp;

    /**
     * whether landscape inventory position is in the left part of the screen
     */
    private static boolean landscapeInventoryLeft;

    /**
     * integer code associated with a language
     */
    private static int languageInt;


    /**
     * Returns the highscore
     *
     * @return the highscore
     */
    public static long getHighscore() {
        return highscore;
    }


    /**
     * Sets the highscore
     *
     * @param newHighscore the highscore
     */
    public static void setHighscore(long newHighscore) {
        highscore = newHighscore;
    }


    /**
     * Return the highscore's saved robots amount
     *
     * @return the highscore's saved robots amount
     */
    public static int getHighscoreSavedRobotsAmount() {
        return highscoreSavedRobotsAmount;
    }

    /**
     * Set the highscore's saved robots amount
     *
     * @param savedRobotsAmount the robots amount
     */
    public static void setHighscoreSavedRobotsAmount(int savedRobotsAmount) {
        highscoreSavedRobotsAmount = savedRobotsAmount;
    }

    /**
     * @return boolean that indicates if it's the first time playing
     */
    public static boolean isUserWelcomed() {
        return isUserWelcomed;
    }

    /**
     * Set's welcome message true, so it won't appear anymore
     *
     * @param b boolean
     */
    public static void setIsUserWelcomed(boolean b) {
        isUserWelcomed = b;
    }

    /**
     * Return the highscore's completed levels amount
     *
     * @return the highscore's completed levels amount
     */
    public static int getHighscoreCompletedLevelsAmount() {
        return highscoreCompletedLevelsAmount;
    }

    /**
     * Set the highscore's completed levels amount
     *
     * @param completedLevelsAmount the completed levels
     */
    public static void setHighscoreCompletedLevelsAmount(int completedLevelsAmount) {
        highscoreCompletedLevelsAmount = completedLevelsAmount;
    }

    /**
     * Return the background's resource id
     */
    public static int getBackgroundResourceId() {
        return highscoreCompletedLevelsAmount;
    }

    /**
     * Set the background's resource id
     */
    public static void setBackgroundResourceId(int pResourceId) {
        highscoreCompletedLevelsAmount = pResourceId;
    }

    /**
     * Set whether effects are enabled
     *
     * @param enabled enabled
     */
    public static void setEffectsEnabled(boolean enabled) {
        effectsEnabled = enabled;
    }

    /**
     * Getter for inventory position
     */
    public static boolean getPortraitInventoryUp() {
        return portraitInventoryUp;
    }

    /**
     * Getter for inventry is left or right in a landscape position screen
     *
     * @return if the inventory is on the left side
     */
    public static boolean getLandscapeInventoryLeft() {
        return landscapeInventoryLeft;
    }

    /**
     * Returns whether music is enabled
     *
     * @return whether music is enabled
     */
    public static boolean isMusicEnabled() {
        return musicEnabled;
    }

    /**
     * Set whether music is enabled
     *
     * @param enabled whether music is enabled
     */
    public static void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;
    }

    /**
     * Returns whether effects are enabled
     *
     * @return whether effects are enabled
     */
    public static boolean areEffectsEnabled() {
        return effectsEnabled;
    }

    /**
     * Returns whether portrait inventory is up
     *
     * @return wen the inventory is on the top side
     */
    public static boolean isPortraitInventoryUp() {
        return portraitInventoryUp;
    }

    /**
     * Set whether portrait inventory is up
     *
     * @param isUp wen the inventory is on the top side
     */
    public static void setPortraitInventoryUp(boolean isUp) {
        portraitInventoryUp = isUp;
    }

    /**
     * Returns whether landscape inventory is left
     *
     * @return wen the inventory is on the left side
     */
    public static boolean isLandscapeInventoryLeft() {
        return landscapeInventoryLeft;
    }

    /**
     * Set wether landscape inventory is left
     *
     * @param isLeft wen the inventory is on the left side
     */
    public static void setLandscapeInventoryLeft(boolean isLeft) {
        landscapeInventoryLeft = isLeft;
    }

    /**
     * Returns the language integer
     *
     * @return the language integer
     */
    public static int getLanguageInt() {
        return languageInt;
    }

    /**
     * Sets the int that establish the language of the application.
     *
     * @param lInt number
     */
    public static void setLanguageInt(int lInt) {
        languageInt = lInt;
    }

    /**
     * Restores the preferences from the SharedPreferences.
     */
    public static void loadPersistentData(Activity activity) {
        // Restore shared preferences
        SharedPreferences prefs = activity.getSharedPreferences(PersistentData.PREFERENCES_FILE, Context.MODE_PRIVATE);

        // load records data
        highscore = prefs.getLong("highScore", 0);
        highscoreSavedRobotsAmount = prefs.getInt("highScoreSavedRobotsAmount", 0);
        highscoreCompletedLevelsAmount = prefs.getInt("highScoreCompletedLevelsAmount", 0);
        backgroundResourceId = prefs.getInt("bgResId", 0);

        // load settings data
        musicEnabled = prefs.getBoolean("enableMusic", true);
        effectsEnabled = prefs.getBoolean("enableEffects", true);
        portraitInventoryUp = prefs.getBoolean("portrait", true);
        landscapeInventoryLeft = prefs.getBoolean("landscape", true);
        languageInt = prefs.getInt("language", 0);

        Locale locale = new Locale(getLang(languageInt));
        Resources resources = activity.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.locale = locale;
        resources.updateConfiguration(config, dm);

    }

    /**
     * Returns the language passed the int associated.
     *
     * @param languageInt languageInt
     * @return language
     */
    private static String getLang(int languageInt) {
        String lang;
        switch (languageInt) {
            case 0:
                lang = "en";
                break;
            case 1:
                lang = "es";
                break;
            case 2:
                lang = "ca";
                break;
            default:
                lang = "en";
                break;
        }
        return lang;
    }

}