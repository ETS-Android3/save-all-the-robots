package pis03_2016.savealltherobots.view.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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
import pis03_2016.savealltherobots.view.viewclass.SimpleClickHandler;
import pis03_2016.savealltherobots.view.viewclass.SoundHandler;
import pis03_2016.savealltherobots.view.viewclass.ViewFunctions;

public class GameOverActivity extends AppCompatActivity implements View.OnTouchListener {

    private TextView txtScore, txtHighscore, txtSavedRobots, txtCompletedLevels;
    private ImageView imgHighscore;
    private ViewFunctions func;
    private Button btnPlayAgain;

    /**
     * Sounds of activity
     */
    private MediaPlayer soundButton, gameOverSound;

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
        setContentView(R.layout.activity_game_over);

        /**
         * Instance of function class
         */
        func = ViewFunctions.getInstance(this);

        txtScore = (TextView) findViewById(R.id.txtVarScoreGO);
        txtHighscore = (TextView) findViewById(R.id.txtVarHighScoreGO);
        txtSavedRobots = (TextView) findViewById(R.id.txtVarSavedRobotsGO);
        txtCompletedLevels = (TextView) findViewById(R.id.txtVarCompletedLevelsGO);
        imgHighscore = (ImageView) findViewById(R.id.imgNewRecord);

        progressBarCircular = (ProgressBar) findViewById(R.id.progressBar);

        btnPlayAgain = (Button) findViewById(R.id.again_button);
        btnPlayAgain.setOnTouchListener(this);

        findViewById(R.id.menu_button).setOnTouchListener(this);
        btnPlayAgain.setOnTouchListener(this);

        refreshData();

    }

    @Override
    protected void onStart() {
        super.onStart();

        /**
         * Add sounds activity
         */
        SoundHandler.stopAllMusics();
        addActivitySounds();

        /**
         * Clean clipboards
         */
        GamePlayActivity.onRestartClipboardFromOrientation = false;
        GamePlayActivity.onRestartClipboardFromBackPressed = false;
        SimpleClickHandler.cleanClipboardVariablesActionClick();

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
                GamePlayController.getInstance().resetGameData();
                GamePlayController.getInstance().prepareNextLevel(GameOverActivity.this);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        /**
                         * The button is hidden while the level isn't generated
                         */
                        while (!GamePlayActivity.isNextLevelReady) {
                            btnPlayAgain.setVisibility(View.GONE);
                        }
                        /**
                         * Activate button when level is generated
                         */
                        progressBarCircular.setVisibility(View.GONE);
                        btnPlayAgain.setVisibility(View.VISIBLE);
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
        //Editor object is needed to make preference changes.
        SharedPreferences settings = getSharedPreferences(PersistentData.PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        //highScore
        long currentScore = GamePlayController.getInstance().getGameData().getGameState().getScore();
        String auxiliar = String.valueOf(currentScore);
        txtScore.setText(String.valueOf(currentScore));
        if (currentScore > PersistentData.getHighscore()) {
            editor.putLong("highScore", currentScore);
            txtHighscore.setText(auxiliar);
            PersistentData.setHighscore(currentScore);
        } else {
            txtHighscore.setText(String.valueOf(settings.getLong("highScore", currentScore)));
        }

        //Saved Robots Amount
        int aux = GamePlayController.getInstance().getGameData().getGameState().getSavedRobotsAmount();
        txtSavedRobots.setText(String.valueOf(aux));
        if (aux > PersistentData.getHighscoreSavedRobotsAmount()) {
            editor.putInt("highScoreSavedRobotsAmount", aux);
            PersistentData.setHighscoreSavedRobotsAmount(aux);
        }


        //Completed Levels Amount
        aux = GamePlayController.getInstance().getGameData().getGameState().getCompletedLevelsAmount();
        txtCompletedLevels.setText(String.valueOf(aux));
        if (aux > PersistentData.getHighscoreCompletedLevelsAmount()) {
            editor.putInt("highScoreCompletedLevelsAmount", aux);
            PersistentData.setHighscoreCompletedLevelsAmount(aux);
        }

        // Commit the edits
        editor.commit();

        if (GamePlayController.getInstance().getGameData().getGameState().isNewRecordAchieved()) {
            imgHighscore.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Goes from the GameOver Activity to the Main Menu
     */
    public void goMenu(View view) {
        SoundHandler.playSound(soundButton);
        setClickedAnimation(view);
        func.goAndFinish(GameOverActivity.this, MainActivity.class, ViewFunctions.TransitionActivityMode.LEFT_TO_RIGHT);
    }

    /**
     * Let the user start a new game from the GameOver Activity
     */
    public void playAgain(View view) {
        SoundHandler.playSound(soundButton);
        if (GamePlayActivity.isNextLevelReady) {
            func.go(GameOverActivity.this, GamePlayActivity.class, ViewFunctions.TransitionActivityMode.LEFT_TO_RIGHT);
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
        gameOverSound = MediaPlayer.create(getApplicationContext(), R.raw.game_over);
        SoundHandler.playSound(gameOverSound);
    }

    @Override
    public void onBackPressed() {
        func.goAndFinish(GameOverActivity.this, MainActivity.class, ViewFunctions.TransitionActivityMode.LEFT_TO_RIGHT);
    }
}
