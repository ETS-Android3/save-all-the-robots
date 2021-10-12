package pis03_2016.savealltherobots.view.viewclass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import pis03_2016.savealltherobots.R;
import pis03_2016.savealltherobots.controller.GamePlayController;
import pis03_2016.savealltherobots.model.LevelSquare;
import pis03_2016.savealltherobots.model.Trap;
import pis03_2016.savealltherobots.model.Trap_Fist;
import pis03_2016.savealltherobots.model.VisualEffectDisplayData;
import pis03_2016.savealltherobots.view.activity.GamePlayActivity;


public final class ViewFunctions {

    /**
     * Prefixes to get id from resources
     */
    public static final String ROBOT_VIEW_PREFIX = "cell_";
    public static final String ROBOT_CONT_PREFIX = "num_";
    public static final String ROBOT_BG_PREFIX = "bg_";
    public static final String ROBOT_LIGHT_PREFIX = "light_";
    public static final String OVERLAP_EFFECT_PREFIX = "PictureOverlapLayerEffect";
    public static final String SATR_PREFIX = "pis03_2016.savealltherobots";
    public static final String BG_RES_ID_BUNDLE_TAG = "RandomResIdBackground";

    /**
     * 1 second.
     */
    public static int SECOND = 1000;

    /**
     * Singleton Instance of Class
     */
    private static ViewFunctions instance = null;

    /**
     * Context application
     */
    private Context context;

    /**
     * ViewFunctions Constructor.
     *
     * @param context is the context
     */
    private ViewFunctions(Context context) {
        this.context = context;
    }

    /**
     * Obtains and returns the drawable id corresponding to the passed visual effect type
     *
     * @param effectType the visual effect type
     * @return the drawable id
     */
    public static int obtainDrawableIdForVisualEffectType(VisualEffectDisplayData.EVisualEffectType effectType) {

        int drawableId = 0;

        switch (effectType) {

            case ELECTRIC_BEAM:
                drawableId = R.drawable.electric_beam_loop;
                break;

            case FLAME:
                drawableId = R.drawable.flame_loop;
                break;

            case FIST_0:
                drawableId = R.drawable.trap_fist_attack_0;
                break;

            case FIST_1:
                drawableId = R.drawable.trap_fist_attack_1;
                break;

            case LASER_BEAM:
                drawableId = R.drawable.laser_beam_loop;
                break;

            case LASER_ENDPOINT:
                drawableId = R.drawable.laser_endpoint_loop;
                break;

            case ROBOT_EXPLOSION:
                drawableId = R.drawable.robot_explosion;
                break;
        }

        return drawableId;
    }

    /**
     * Returns whether the static image shown on a square should be hidden when displaying
     * dynamic effects on that square
     *
     * @param square the square
     * @return whether the static image should be hidden
     */
    public static boolean shouldHideStaticImageAtSquareWhenDisplayingEffects(LevelSquare square) {

        if (square.isOccupied()) {

            if (square.getObject() instanceof Trap) {

                if (square.getObject() instanceof Trap_Fist) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Getter of singleton instance uses lazy creation
     *
     * @param context is the context
     * @return instance of class
     */
    public static ViewFunctions getInstance(Context context) {
        if (instance == null) {
            instance = new ViewFunctions(context);
        }
        return instance;
    }

    /**
     * Start to play
     *
     * @param activity activity
     */
    public void startPlaying(AppCompatActivity activity) {

        if (!GamePlayActivity.onRestartClipboardFromBackPressed) {

            GamePlayController.getInstance().resetGameData();

            GamePlayController.getInstance().prepareNextLevel(activity);

        }
        GamePlayActivity.onRestartClipboardFromBackPressed = true;
        go(activity, GamePlayActivity.class, ViewFunctions.TransitionActivityMode.LEFT_TO_RIGHT);
    }

    /**
     * Go from start activity to goal activity;
     *
     * @param start is the activity from.
     * @param goal  is the activity to.
     */
    public void go(Activity start, Class<?> goal, TransitionActivityMode mode) {
        Intent intent = new Intent(start, goal);
        start.startActivity(intent);
        if (mode == TransitionActivityMode.LEFT_TO_RIGHT) {
            start.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        } else {
            start.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }
    }

    /**
     * Go from start activity to goal activity;
     *
     * @param start is the activity from.
     * @param goal  is the activity to.
     */
    public void goWithTimeOut(final Activity start, final Class<?> goal, int time, final TransitionActivityMode mode) {

        Handler handler = new Handler();

        /**
         * Delay default timer
         */
        Runnable runner = new Runnable() {
            public void run() {
                goAndFinish(start, goal, mode);

            }
        };

        handler.postDelayed(runner, time);

    }

    /**
     * Go from start activity to goal activity with finsh activity start;
     *
     * @param start is the activity from.
     * @param goal  is the activity to.
     */
    public void goAndFinish(Activity start, Class<?> goal, TransitionActivityMode mode) {
        go(start, goal, mode);
        start.finish();
    }

    /**
     * Go from start activity to goal activity with clean all flags;
     *
     * @param start is the activity from.
     * @param goal  is the activity to.
     */
    public void goWithRemoveHistory(Activity start, Class<?> goal) {
        Intent intent = new Intent(start, goal);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        start.startActivity(intent);
        start.finish();
        start.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    /**
     * Getter of id from resource object.
     *
     * @param obj  is a string obj.
     * @param mode is returned obj mode.
     * @return identifier of object resource.
     */
    public int getResourceId(String obj, String mode) {
        return context.getResources().getIdentifier(obj, mode, getPackageName());
    }

    /**
     * Returns the current package name
     *
     * @return package
     */
    public String getPackageName() {
        return context.getPackageName();
    }

    /**
     * Gets the orientation
     *
     * @return current orientation mobile.
     */
    public int getOrientation() {
        return context.getResources().getConfiguration().orientation;
    }

    /**
     * Gets the orientation
     *
     * @return current orientation mobile.
     */
    public int getOrientation(Context ctx) {
        return ctx.getResources().getConfiguration().orientation;
    }

    /**
     * Create instance an object from string name.
     *
     * @param className it's a class name
     * @return instance
     */
    public Object createObject(String className) {
        Object object = null;
        try {
            Class classDefinition = Class.forName(className);
            object = classDefinition.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * Type transition slide activities
     */
    public enum TransitionActivityMode {
        LEFT_TO_RIGHT, RIGHT_TO_LEFT
    }

    /**
     * Enum for type explosions
     */
    public enum TypeExplosions {
        ROBOT_NORMAL_EXPLOSION,
        ROBOT_EXPLOSIVE_EXPLOSION
    }

    /**
     * Status increment and decrement
     */
    public enum ActionCounterRobots {
        INCREMENT,
        DECREMENT
    }

}
