package pis03_2016.savealltherobots.view.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import pis03_2016.savealltherobots.R;
import pis03_2016.savealltherobots.model.PersistentData;
import pis03_2016.savealltherobots.view.viewclass.SoundHandler;
import pis03_2016.savealltherobots.view.viewclass.ViewFunctions;

public class HighscoreScreenActivity extends AppCompatActivity {


    /**
     * View items
     */
    private TextView txtHighscore, txtSavedRobots, txtCompletedLevels;

    /**
     * Instance of ViewFunctions
     */
    private ViewFunctions func = ViewFunctions.getInstance(this);

    /**
     * Sounds of activity
     */
    private MediaPlayer musicIntro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore_screen);

        txtHighscore = (TextView) findViewById(R.id.txtVarHighscoreHS);
        txtSavedRobots = (TextView) findViewById(R.id.txtVarSavedRobotsHS);
        txtCompletedLevels = (TextView) findViewById(R.id.txtVarCompletedLevelsHS);
    }

    @Override
    protected void onStart() {
        super.onStart();

        /**
         * Add sounds activity
         */
        SoundHandler.stopAllMusics();
        addActivitySounds();

        showData();
    }


    /**
     * Displays de data
     */
    public void showData() {
        //Editor object is needed to make preference changes.
        SharedPreferences settings = getSharedPreferences(PersistentData.PREFERENCES_FILE, Context.MODE_PRIVATE);
        txtHighscore.setText(String.valueOf(settings.getLong("highScore", 0)));
        txtSavedRobots.setText(String.valueOf(settings.getInt("highScoreSavedRobotsAmount", 0)));
        txtCompletedLevels.setText(String.valueOf(settings.getInt("highScoreCompletedLevelsAmount", 0)));
    }

    /**
     * Adding activity sounds
     */
    private void addActivitySounds() {
        musicIntro = MediaPlayer.create(getApplicationContext(), R.raw.intro_loop);
        SoundHandler.playSoundWithLoop(musicIntro);
        SoundHandler.musicStack.add(musicIntro);
    }

    /**
     * This returns to the main activity
     */
    @Override
    public void onBackPressed() {
        func.goAndFinish(this, MainActivity.class, ViewFunctions.TransitionActivityMode.RIGHT_TO_LEFT);
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
