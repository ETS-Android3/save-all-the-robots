package pis03_2016.savealltherobots.view.activity;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import pis03_2016.savealltherobots.R;
import pis03_2016.savealltherobots.controller.GamePlayController;
import pis03_2016.savealltherobots.view.viewclass.GifImageView;
import pis03_2016.savealltherobots.view.viewclass.SoundHandler;
import pis03_2016.savealltherobots.view.viewclass.ViewFunctions;



public class WelcomeScreenActivity extends AppCompatActivity implements View.OnTouchListener {

    /**
     * Instance of view functions
     */
    private ViewFunctions func;

    /**
     * Factor to scale the gif depending on screen device size
     */

    /**
     * Views variables
     */
    private GifImageView tutorial;
    private Button btnStart;
    private TextView constructNextLevelLoaderText;

    /**
     * Sounds of activity
     */
    private MediaPlayer soundButton, musicIntro;

    /**
     * It's handler for level preparing
     */
    private Handler handler = new Handler();

    /**
     * Circular progress bar
     */
    private ProgressBar progressBarCircular;
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
        setContentView(R.layout.activity_welcome_screen);

        /**
         * Assign views and functions
         */
        func = ViewFunctions.getInstance(this);
        tutorial = (GifImageView) findViewById(R.id.tutorialGif);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnTouchListener(this);
        progressBarCircular = (ProgressBar) findViewById(R.id.progressBar);
        constructNextLevelLoaderText = (TextView) findViewById(R.id.constructNextLevelLoaderText);

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
         * Load gif from tutorial
         */
        tutorial.loadGIFResource(R.drawable.tutorial);

        /**
         * Adapting gif size to device screen
         */
        scaleGif();

        /**
         * Run to start game
         */
        handler.postDelayed(prepareNextLevel, ViewFunctions.SECOND);
    }

    /**
     * Add new width and height gif tutorial
     */
    private void scaleGif() {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        float scalingGifFactor = (func.getOrientation() == Configuration.ORIENTATION_PORTRAIT) ? 2.6f : 1;

        int scaledHeight = (int) (height / scalingGifFactor);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, scaledHeight);
        tutorial.setLayoutParams(params);
    }

    /**
     * Let the user start a new game from the GameOver Activity
     */
    public void playNow(View view) {
        SoundHandler.playSound(soundButton);
        setClickedAnimation(view);
        if (GamePlayActivity.isNextLevelReady) {
            func.goAndFinish(WelcomeScreenActivity.this, GamePlayActivity.class, ViewFunctions.TransitionActivityMode.LEFT_TO_RIGHT);
        }
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
                 * Generating start level
                 */
                GamePlayController.getInstance().resetGameData();
                GamePlayController.getInstance().prepareNextLevel(WelcomeScreenActivity.this);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        /**
                         * The button is hidden while the level isn't generated
                         */
                        while (!GamePlayActivity.isNextLevelReady) {
                            btnStart.setVisibility(View.GONE);
                        }
                        /**
                         * Activate button when level is generated
                         */
                        progressBarCircular.setVisibility(View.GONE);
                        btnStart.setVisibility(View.VISIBLE);
                        constructNextLevelLoaderText.setText("Press start button");
                    }
                });

            }
        };
        Thread thread = new Thread(runner);
        thread.start();
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
            setClickedAnimation(v);
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
        musicIntro = MediaPlayer.create(getApplicationContext(), R.raw.intro_loop);
        SoundHandler.playSoundWithLoop(musicIntro);
        SoundHandler.musicStack.add(musicIntro);
    }

    /**
     * This returns to the main activity
     */
    @Override
    public void onBackPressed() {
        func.goAndFinish(WelcomeScreenActivity.this, MainActivity.class, ViewFunctions.TransitionActivityMode.RIGHT_TO_LEFT);
    }


    @Override
    public void onPause() {
        super.onPause();
        SoundHandler.stopSound(musicIntro);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        SoundHandler.playSound(musicIntro);
    }
}
