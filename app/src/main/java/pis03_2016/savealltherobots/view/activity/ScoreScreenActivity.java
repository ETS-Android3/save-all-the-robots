package pis03_2016.savealltherobots.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import pis03_2016.savealltherobots.R;
import pis03_2016.savealltherobots.controller.GamePlayController;
import pis03_2016.savealltherobots.model.PersistentData;
import pis03_2016.savealltherobots.model.Robot_Deactivator_Radio;
import pis03_2016.savealltherobots.model.Robot_Diamond;
import pis03_2016.savealltherobots.model.Robot_Explosive;
import pis03_2016.savealltherobots.model.Robot_Gold;
import pis03_2016.savealltherobots.model.Robot_Steel;
import pis03_2016.savealltherobots.model.Trap_ElectricCircle;
import pis03_2016.savealltherobots.model.Trap_FlameThrower;
import pis03_2016.savealltherobots.model.Trap_LaserCannon;
import pis03_2016.savealltherobots.model.Trap_QuadrupleLaserCannon;
import pis03_2016.savealltherobots.model.UnlockableItems;
import pis03_2016.savealltherobots.view.viewclass.SimpleClickHandler;
import pis03_2016.savealltherobots.view.viewclass.SoundHandler;
import pis03_2016.savealltherobots.view.viewclass.ViewFunctions;


public class ScoreScreenActivity extends Activity implements View.OnTouchListener {

    private TextView itemDescription, txtScore, txtSavedRobots, txtCompletedLevels, constructNextLevelLoaderText, subTitle;
    private ImageView itemImage;
    private Button btnNextLevel;
    private ViewFunctions func;
    private UnlockableItems unlockableItems;

    /**
     * Sounds of activity
     */
    private MediaPlayer soundButton, soundVictory;

    /**
     * Circular progress bar
     */
    private ProgressBar progressBarCircular;

    /**
     * It's handler for level preparing
     */
    private Handler handler = new Handler();
    /**
     * Delay default timer
     */
    private Runnable prepareNextLevel = new Runnable() {
        public void run() {
            handler.removeCallbacks(prepareNextLevel);
            constructNextLevel();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_screen);

        func = ViewFunctions.getInstance(this);
        unlockableItems = GamePlayController.getInstance().getGameData().getGameState().getUnlockedItems();
        txtScore = (TextView) findViewById(R.id.txtVarScoreSS);
        txtSavedRobots = (TextView) findViewById(R.id.txtVarSavedRobotsSS);
        constructNextLevelLoaderText = (TextView) findViewById(R.id.constructNextLevelLoaderText);
        txtCompletedLevels = (TextView) findViewById(R.id.txtVarCompletedLevelsSS);
        itemImage = (ImageView) findViewById(R.id.unlockedItem);
        itemDescription = (TextView) findViewById(R.id.txtItemDescription);
        subTitle = (TextView) findViewById(R.id.txtItemUnlocked);

        progressBarCircular = (ProgressBar) findViewById(R.id.progressBar);

        btnNextLevel = (Button) findViewById(R.id.btnNextLevel);
        btnNextLevel.setOnTouchListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        /**
         * Add sounds activity
         */
        SoundHandler.stopAllMusics();
        addActivitySounds();

        GamePlayActivity.isNextLevelReady = false;

        /**
         * Clean clipboards
         */
        GamePlayActivity.onRestartClipboardFromOrientation = false;
        GamePlayActivity.onRestartClipboardFromBackPressed = false;
        SimpleClickHandler.cleanClipboardVariablesActionClick();

        refreshData();

        handler.postDelayed(prepareNextLevel, ViewFunctions.SECOND);
    }

    /**
     * Prepare next level function
     */
    private void constructNextLevel() {

        /**
         * Generating new level in background
         */
        Runnable runner = new Runnable() {

            @Override
            public void run() {
                /**
                 * Generating new level
                 */
                GamePlayController.getInstance().prepareNextLevel(ScoreScreenActivity.this);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        /**
                         * The button is hidden while the level isn't generated
                         */
                        while (!GamePlayActivity.isNextLevelReady) {
                            btnNextLevel.setVisibility(View.GONE);
                        }
                        /**
                         * Activate button when level is generated
                         */
                        progressBarCircular.setVisibility(View.GONE);
                        btnNextLevel.setVisibility(View.VISIBLE);
                        constructNextLevelLoaderText.setText(R.string.goToNextLevel);
                    }
                });

            }
        };
        Thread thread = new Thread(runner);
        thread.start();
    }

    /**
     * Actualizes the data to store on the SharedPreferences if it's necessary,
     * it also writes the textViews with the correct information.
     */
    public void refreshData() {

        refreshItemDisplayProperties();

        //Editor object is needed to make preference changes.
        SharedPreferences settings = getSharedPreferences(PersistentData.PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        //highScore
        long currentScore = GamePlayController.getInstance().getGameData().getGameState().getScore();
        txtScore.setText(String.valueOf(currentScore));
        if (currentScore > settings.getLong("highScore", 0)) {
            editor.putLong("highScore", currentScore);
            PersistentData.setHighscore(currentScore);
        }

        //Saved Robots Amount
        int aux = GamePlayController.getInstance().getGameData().getGameState().getSavedRobotsAmount();
        txtSavedRobots.setText(String.valueOf(aux));
        if (aux > settings.getInt("highScoreSavedRobotsAmount", 0)) {
            editor.putInt("highScoreSavedRobotsAmount", aux);
            PersistentData.setHighscoreSavedRobotsAmount(aux);
        }


        //Completed Levels Amount
        aux = GamePlayController.getInstance().getGameData().getGameState().getCompletedLevelsAmount();
        txtCompletedLevels.setText(String.valueOf(aux));
        if (aux > settings.getInt("highScoreCompletedLevelsAmount", 0)) {
            editor.putInt("highScoreCompletedLevelsAmount", aux);
            PersistentData.setHighscoreCompletedLevelsAmount(aux);
        }

        // Commit the edits
        editor.commit();
    }

    private void refreshItemDisplayProperties() {

        int index = unlockableItems.getIndex();

        if (index < unlockableItems.getUnlockableArray().length) {

            Class unlockableClass = unlockableItems.getUnlockableArray()[index];

            if (unlockableClass == Trap_LaserCannon.class) {
                itemDescription.setText(R.string.description_trapLaserCannon);
                itemImage.setImageResource(R.drawable.trap_lasercannon);
            } else if (unlockableClass == Robot_Steel.class) {
                itemDescription.setText(R.string.description_robotSteel);
                itemImage.setImageResource(R.drawable.robot_steel);
            } else if (unlockableClass == Robot_Gold.class) {
                itemDescription.setText(R.string.description_robotGold);
                itemImage.setImageResource(R.drawable.robot_gold);
            } else if (unlockableClass == Trap_FlameThrower.class) {
                itemDescription.setText(R.string.description_trapFlameThrower);
                itemImage.setImageResource(R.drawable.trap_flamethrower);
            } else if (unlockableClass == Robot_Diamond.class) {
                itemDescription.setText(R.string.description_robotDiamond);
                itemImage.setImageResource(R.drawable.robot_diamond);
            } else if (unlockableClass == Robot_Explosive.class) {
                itemDescription.setText(R.string.description_robotExplosive);
                itemImage.setImageResource(R.drawable.robot_explosive);
            } else if (unlockableClass == Trap_ElectricCircle.class) {
                itemDescription.setText(R.string.description_trapSpikesCircle);
                itemImage.setImageResource(R.drawable.trap_electriccircle);
            } else if (unlockableClass == Trap_QuadrupleLaserCannon.class) {
                itemDescription.setText(R.string.description_trapLaserCannon);
                itemImage.setImageResource(R.drawable.trap_quadruplelasercannon);
            } else if (unlockableClass == Robot_Deactivator_Radio.class) {
                itemDescription.setText(R.string.description_robotRadio);
                itemImage.setImageResource(R.drawable.robot_deactivator_radio);
            }
        } else {
            subTitle.setText(R.string.allUnlockedTitle);
            itemDescription.setText(R.string.allUnlocked);
            itemImage.setVisibility(View.GONE);
            itemDescription.setGravity(Gravity.CENTER);
            subTitle.setGravity(Gravity.CENTER);
        }
    }

    @Override
    public void onBackPressed() {
    }

    public void onClickNextLevel(View view) {
        SoundHandler.playSound(soundButton);
        if (GamePlayActivity.isNextLevelReady) {
            func.go(ScoreScreenActivity.this, GamePlayActivity.class, ViewFunctions.TransitionActivityMode.LEFT_TO_RIGHT);
            finish();
        }
        setClickedAnimation(view);
    }


    /**
     * Starts an animation that reduces and resize a view like a spring
     *
     * @param view view
     */
    public void setClickedAnimation(View view) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.clicked_button);
        view.startAnimation(animation);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pressed_button);
            v.startAnimation(animation);
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cancel_clicked);
            v.startAnimation(animation);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cancel_clicked);
            v.startAnimation(animation);
        } else if (event.getAction() == MotionEvent.ACTION_BUTTON_RELEASE) {
            return true;
        }
        return false;
    }

    /**
     * Adding activity sounds
     */
    private void addActivitySounds() {
        soundButton = MediaPlayer.create(getApplicationContext(), R.raw.generic_button);
        soundVictory = MediaPlayer.create(getApplicationContext(), R.raw.level_victory);
        SoundHandler.playSound(soundVictory);
    }
}
