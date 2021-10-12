package pis03_2016.savealltherobots.view.viewclass;

import android.app.Activity;
import android.os.CountDownTimer;

import pis03_2016.savealltherobots.controller.GamePlayController;
import pis03_2016.savealltherobots.view.activity.GamePlayActivity;


public abstract class TimerHandler {

    /**
     * InnerStaticClass:: Represents the splash screen timer
     */
    public static class SplashScreenTimer extends CountDownTimer {

        /**
         * Interval of time
         */
        private static final long INTERVAL = ViewFunctions.SECOND;

        /**
         * Splash time
         */
        private static final long TIME_SPLASH = INTERVAL * 2;

        /**
         * Activity from
         */
        private Activity start;

        /**
         * Activity to
         */
        private Class goal;

        /**
         * Function Class Instance
         */
        private ViewFunctions func;

        /**
         * SplashScreenTimer Constructor
         *
         * @param start start
         * @param goal  goal
         */
        public SplashScreenTimer(Activity start, Class goal) {
            super(TIME_SPLASH, INTERVAL);
            this.start = start;
            this.goal = goal;
            this.func = ViewFunctions.getInstance(start);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //Nothing to do
        }

        @Override
        public void onFinish() {
            func.go(start, goal, ViewFunctions.TransitionActivityMode.LEFT_TO_RIGHT);
            start.finish();
        }
    }

    /**
     * InnerStaticClass:: Represents the timer of main game.
     */
    public static class TimeGameLeft extends CountDownTimer {

        /**
         * Interval of time
         */
        private static final long INTERVAL = ViewFunctions.SECOND;

        /**
         * TimeLeft Constructor.
         *
         * @param startTime The number of millis in the future from the call
         */
        public TimeGameLeft(long startTime) {
            super(startTime, INTERVAL);
        }

        /**
         * Is called in the time interval
         *
         * @param timeLeftS is the time left.
         */
        @Override
        public void onTick(long timeLeftS) {
            GamePlayController.TIME_LEFT = timeLeftS;
            GamePlayActivity.actionsOnTimerClock(String.valueOf((int) (timeLeftS / INTERVAL)));
            GamePlayActivity.showDoneButtonIfAllRobotsPlaced();
        }

        /**
         * Is called when timeLeft is 0.
         */
        @Override
        public void onFinish() {
            if (!GamePlayActivity.hasPressedBackButton) {
                GamePlayActivity.evaluateLevelResult();
            } else {
                GamePlayActivity.stopByForce();
            }
        }
    }

}
