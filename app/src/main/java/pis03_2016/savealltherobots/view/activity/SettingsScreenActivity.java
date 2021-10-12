package pis03_2016.savealltherobots.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.util.Locale;

import pis03_2016.savealltherobots.R;
import pis03_2016.savealltherobots.model.PersistentData;
import pis03_2016.savealltherobots.view.viewclass.SoundHandler;
import pis03_2016.savealltherobots.view.viewclass.ViewFunctions;

public class SettingsScreenActivity extends Activity implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

    protected String lang;
    /**
     * Toggle of music ON/OFF.
     */
    private ToggleButton music;
    /**
     * Toggle of sounds effects ON/OFF.
     */
    private ToggleButton effects;
    /**
     * Toggle if the inventory will be TOP/BOTTOM
     * on portrait mode
     */
    private ToggleButton portrait;
    /**
     * Toggle if the inventory should be LEFT/RIGHT
     * on landscape mode
     */
    private ToggleButton landscape;
    /**
     * Button info
     */
    private Button info;
    /**
     * Instance of ViewFunctions
     */
    private ViewFunctions func;

    /**
     * Sounds of activity
     */
    private MediaPlayer soundButton, musicIntro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        func = ViewFunctions.getInstance(this);

        //Associates the buttons
        music = (ToggleButton) findViewById(R.id.tg_music);
        effects = (ToggleButton) findViewById(R.id.tg_effects);
        portrait = (ToggleButton) findViewById(R.id.tg_portrait);
        landscape = (ToggleButton) findViewById(R.id.tg_landscape);
        info = (Button) findViewById(R.id.btn_info);
        initializeToggleButtons();
        music.setOnTouchListener(this);
        effects.setOnTouchListener(this);
        portrait.setOnTouchListener(this);
        landscape.setOnTouchListener(this);
        if (info != null) {
            info.setOnTouchListener(this);
        }

        Spinner language = (Spinner) findViewById(R.id.sp_language);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_array, R.layout.spinner_layout);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_style_dropdown_item);
        // Apply the adapter to the spinner
        language.setAdapter(adapter);
        //This false disable the initial OnItemSelected action.
        language.setSelection(PersistentData.getLanguageInt(), false);
        language.setOnItemSelectedListener(this);

        // obtain saved data from Shared Preferences
        restoreSettings();
    }

    @Override
    protected void onStart() {
        super.onStart();

        /**
         * Add sounds activity
         */
        SoundHandler.stopAllMusics();
        addActivitySounds();
    }


    /**
     * Sets to each button the correct information
     */
    private void initializeToggleButtons() {
        portrait.setTextOn(getString(R.string.settingsPortraitUp));
        portrait.setTextOff(getString(R.string.settingsPortraitDown));
        landscape.setTextOn(getString(R.string.settingsLandscapeLeft));
        landscape.setTextOff(getString(R.string.settingsLandscapeRight));
    }

    /**
     * Actualizes the data from the SharedPreferences
     */
    private void restoreSettings() {
        music.setChecked(PersistentData.isMusicEnabled());
        effects.setChecked(PersistentData.areEffectsEnabled());

        portrait.setChecked(PersistentData.isPortraitInventoryUp());
        landscape.setChecked(PersistentData.isLandscapeInventoryLeft());
        refreshToggleButtons();
    }

    /**
     * Refresh the toggle buttons information
     */
    private void refreshToggleButtons() {
        boolean checked = portrait.isChecked();

        if (checked) {
            int imgResource = R.drawable.portrait_top;
            portrait.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
        } else {
            int imgResource = R.drawable.portrait_bottom;
            portrait.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
        }

        checked = landscape.isChecked();


        if (checked) {
            int imgResource = R.drawable.landscape_left;
            landscape.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
        } else {
            int imgResource = R.drawable.landscape_right;
            landscape.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    /**
     * Save the status on SharedPreferences and enables/disables the music.
     *
     * @param view music toggle button
     */
    public void onClickToggleMusic(View view) {
        //Editor object is needed to make preference changes.
        SharedPreferences settings = getSharedPreferences(PersistentData.PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        boolean checked = music.isChecked();
        editor.putBoolean("enableMusic", checked);
        // Commit the edits
        editor.commit();

        PersistentData.setMusicEnabled(checked);

        /**
         * Pause music on click button
         */
        if (!PersistentData.isMusicEnabled()) {
            SoundHandler.stopAllMusics();
        } else {
            addActivitySounds();
        }

        SoundHandler.playSound(soundButton);

        setClickedAnimation(view);
    }

    /**
     * Save the status on SharedPreferences and enables/disables the music effects.
     *
     * @param view effects toggle buttom
     */
    public void onClickToggleEffects(View view) {
        SoundHandler.playSound(soundButton);
        //Editor object is needed to make preference changes.
        SharedPreferences settings = getSharedPreferences(PersistentData.PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        boolean checked = effects.isChecked();
        editor.putBoolean("enableEffects", checked);
        // Commit the edits
        editor.commit();

        PersistentData.setEffectsEnabled(checked);

        setClickedAnimation(view);
    }

    /**
     * Save the status on SharedPreferences and establish where goes the menu of robots.
     *
     * @param view portrait toggle button
     */
    public void onClickTogglePortrait(View view) {
        SoundHandler.playSound(soundButton);

        //Editor object is needed to make preference changes.
        SharedPreferences settings = getSharedPreferences(PersistentData.PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        //TRUE means "top", FALSE  means "bottom"
        boolean checked = portrait.isChecked();

        if (checked) {
            int imgResource = R.drawable.portrait_top;
            portrait.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
        } else {
            int imgResource = R.drawable.portrait_bottom;
            portrait.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
        }

        editor.putBoolean("portrait", checked);
        // Commit the edits
        editor.commit();

        PersistentData.setPortraitInventoryUp(checked);
        setClickedAnimation(view);
    }

    /**
     * Save the status on SharedPreferences and establish where goes the menu of robots.
     *
     * @param view landscape toggle button
     */
    public void onClickToggleLandscape(View view) {

        SoundHandler.playSound(soundButton);

        //Editor object is needed to make preference changes.
        SharedPreferences settings = getSharedPreferences(PersistentData.PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        //TRUE means "left", FALSE  means "right"
        boolean checked = landscape.isChecked();

        if (checked) {
            int imgResource = R.drawable.landscape_left;
            landscape.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
        } else {
            int imgResource = R.drawable.landscape_right;
            landscape.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
        }

        editor.putBoolean("landscape", checked);
        // Commit the edits
        editor.commit();

        PersistentData.setLandscapeInventoryLeft(checked);
        setClickedAnimation(view);
    }

    /**
     * Configures the language of the application
     *
     * @param parent   parent
     * @param view     view
     * @param position position
     * @param id       id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        SoundHandler.playSound(soundButton);

        lang = "en";
        switch (position) {
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
        //Editor object is needed to make preference changes.
        SharedPreferences settings = getSharedPreferences(PersistentData.PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("language", position);
        // Commit the edits
        editor.commit();

        PersistentData.setLanguageInt(position);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //Change the language and refresh the activity
                setLocale(lang);
            }
        }).start();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Changes the language of the application.
     *
     * @param lang string
     */
    protected void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.locale = locale;
        resources.updateConfiguration(config, dm);
        finish();
        func.go(this, SettingsScreenActivity.class, ViewFunctions.TransitionActivityMode.RIGHT_TO_LEFT);
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

    /**
     * This returns to the main activity
     */
    @Override
    public void onBackPressed() {
        func.goAndFinish(this, MainActivity.class, ViewFunctions.TransitionActivityMode.RIGHT_TO_LEFT);
    }

    /**
     * Starts the Credits Activity
     *
     * @param view view
     */
    public void onClickInfo(View view) {
        Intent intent = new Intent(SettingsScreenActivity.this, CreditsActivity.class);
        setClickedAnimation(view);
        startActivity(intent);
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