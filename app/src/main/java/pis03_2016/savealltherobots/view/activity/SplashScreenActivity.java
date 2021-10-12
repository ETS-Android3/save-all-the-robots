package pis03_2016.savealltherobots.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.util.Random;

import pis03_2016.savealltherobots.R;
import pis03_2016.savealltherobots.model.PersistentData;
import pis03_2016.savealltherobots.view.viewclass.TimerHandler;


public class SplashScreenActivity extends AppCompatActivity {

    /**
     * On Create
     *
     * @param savedInstanceState .
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        loadPersistentData(this);
        setSplashableBackground();
    }

    /**
     * Loading data from SharedPreferences
     *
     * @param activity is the current activity
     */
    private void loadPersistentData(final Activity activity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PersistentData.loadPersistentData(activity);
            }
        }).start();
    }

    private void setSplashableBackground(){
        int resourceId = getRandomBackgroundResourceId();

        ImageView lBackground = findViewById(R.id.splashableBackground);

        lBackground.setBackgroundResource(resourceId);

        PersistentData.setBackgroundResourceId(resourceId);

        lBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);

        startAnimation();
    }

    /**
     * Init animation.
     */
    public void startAnimation() {
        CountDownTimer timer = new TimerHandler.SplashScreenTimer(SplashScreenActivity.this, MainActivity.class);
        timer.start();
    }

    private int getRandomBackgroundResourceId(){
        // 9 possible bg images
        int index = new Random().nextInt(8 - 1 + 1) + 1;
        // Return the drawable resource id
        return getResources().getIdentifier("splashable"+index, "drawable", getPackageName());
    }
}