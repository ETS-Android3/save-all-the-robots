package pis03_2016.savealltherobots.view.viewclass;


import android.media.MediaPlayer;
import java.util.ArrayList;
import pis03_2016.savealltherobots.model.PersistentData;


public final class SoundHandler {

    /**
     * Playing musics array
     */
    public static ArrayList<MediaPlayer> musicStack = new ArrayList<>();

    /**
     * Reproduce sounds in application
     *
     * @param sound is the sound for reproduce
     */
    public static void playSound(MediaPlayer sound) {
        if (sound != null && PersistentData.areEffectsEnabled()) {
            sound.start();
        }
    }

    /**
     * Reproduce sounds in application
     *
     * @param sound is the sound for reproduce
     */
    public static void playSoundWithLoop(MediaPlayer sound) {
        if (sound != null && PersistentData.isMusicEnabled()) {
            sound.start();
            sound.setLooping(true);
        }
    }

    /**
     * Stops sounds in application
     *
     * @param sound is the sound for reproduce
     */
    public static void stopSound(MediaPlayer sound) {
        if (sound != null) {
            sound.stop();
        }
    }

    /**
     * Stop all actual musics
     */
    public static void stopAllMusics() {

        for (MediaPlayer mp : musicStack) {
            stopSound(mp);
        }
        if (!musicStack.isEmpty()) {
            musicStack.clear();
        }
    }
}
