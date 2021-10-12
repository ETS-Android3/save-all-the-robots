package pis03_2016.savealltherobots.view.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import pis03_2016.savealltherobots.R;
import pis03_2016.savealltherobots.model.PersistentData;
import pis03_2016.savealltherobots.view.viewclass.SimpleClickHandler;
import pis03_2016.savealltherobots.view.viewclass.SoundHandler;
import pis03_2016.savealltherobots.view.viewclass.ViewFunctions;


public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private ViewFunctions func;

    /**
     * Sounds of activity
     */
    private MediaPlayer soundButton, musicIntro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        this.func = ViewFunctions.getInstance(this);

        findViewById(R.id.play_button).setOnTouchListener(this);
        findViewById(R.id.highscores_button).setOnTouchListener(this);
        findViewById(R.id.settings_button).setOnTouchListener(this);
        findViewById(R.id.exit_button).setOnTouchListener(this);

        overrideBackground();
    }

    @Override
    public void onStart() {

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

    }

    /**
     * Prepares things for game play
     *
     * @param view view
     */
    public void play(View view) {
        SoundHandler.playSound(soundButton);
        if (PersistentData.isUserWelcomed()) {
            setClickedAnimation(view);
            func.startPlaying(MainActivity.this);
        } else {
            PersistentData.setIsUserWelcomed(true);
            func.go(MainActivity.this, WelcomeScreenActivity.class, ViewFunctions.TransitionActivityMode.LEFT_TO_RIGHT);
        }

    }

    /**
     * Closes and exit's application
     */
    public void exit(View view) {
        SoundHandler.playSound(soundButton);
        setClickedAnimation(view);

        /**
         * Force finish when exit button is pressed
         */
        SoundHandler.stopAllMusics();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    /**
     * Launch de highscore activity
     */
    public void goHighScoreActivity(View view) {
        SoundHandler.playSound(soundButton);
        setClickedAnimation(view);
        func.go(MainActivity.this, HighscoreScreenActivity.class, ViewFunctions.TransitionActivityMode.LEFT_TO_RIGHT);
    }


    /**
     * Go to Settings Activity.
     *
     * @param view is the context view
     */
    public void goSettingsActivity(View view) {
        SoundHandler.playSound(soundButton);
        setClickedAnimation(view);
        finish();
        func.go(MainActivity.this, SettingsScreenActivity.class, ViewFunctions.TransitionActivityMode.LEFT_TO_RIGHT);
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
        musicIntro = MediaPlayer.create(getApplicationContext(), R.raw.intro_loop);
        SoundHandler.playSoundWithLoop(musicIntro);
        SoundHandler.musicStack.add(musicIntro);
    }

    private void overrideBackground(){
        ImageView lBackground = findViewById(R.id.mainBackground);
        lBackground.setBackgroundResource(PersistentData.getBackgroundResourceId());
        lBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    /**
     * Force finish when back button is pressed
     */
    @Override
    public void onBackPressed() {
        SoundHandler.stopAllMusics();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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