package pis03_2016.savealltherobots.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import pis03_2016.savealltherobots.R;
import pis03_2016.savealltherobots.controller.GamePlayController;
import pis03_2016.savealltherobots.controller.GridAdapter;
import pis03_2016.savealltherobots.model.LevelGrid;
import pis03_2016.savealltherobots.model.LevelInventory;
import pis03_2016.savealltherobots.model.LevelInventoryCell;
import pis03_2016.savealltherobots.model.LevelSquare;
import pis03_2016.savealltherobots.model.PersistentData;
import pis03_2016.savealltherobots.model.Robot;
import pis03_2016.savealltherobots.model.VisualEffectDisplayData;
import pis03_2016.savealltherobots.view.viewclass.DragAndDropHandler;
import pis03_2016.savealltherobots.view.viewclass.GifImageView;
import pis03_2016.savealltherobots.view.viewclass.SimpleClickHandler;
import pis03_2016.savealltherobots.view.viewclass.SoundHandler;
import pis03_2016.savealltherobots.view.viewclass.TimerHandler;
import pis03_2016.savealltherobots.view.viewclass.ViewFunctions;


public class GamePlayActivity extends AppCompatActivity {

    /**
     * Time left view
     */
    public static TextView timeLeft;
    /**
     * Frame having light at this time
     */
    public static FrameLayout currentLightBackground = null;
    /**
     * Static instance of activity.
     */
    public static GamePlayActivity staticInstance;
    /**
     * Is the grid
     */
    public static GridView gridView;
    /**
     * Boolean clipboard for restart on orientation
     */
    public static boolean onRestartClipboardFromOrientation = false;
    /**
     * Check if the game already started
     */
    public static boolean onRestartClipboardFromBackPressed = false;
    /**
     * Whether the user has pressed the back button while playing
     */
    public static boolean hasPressedBackButton = false;
    /**
     * Array from all views the grid
     */
    public static ArrayList<View> gamePlayViews = new ArrayList<>();
    /**
     * Has game time left yet
     */
    public static boolean hasLeftTime;
    /**
     * Is new level generated?
     */
    public static boolean isNextLevelReady;
    /**
     * This adapter is a specific driver that inflate the matrix and provides control methods
     */
    private static GridAdapter adapter;
    /**
     * Start time of game.
     */
    private static long startTime;
    /**
     * Instance of controller.
     */
    private static GamePlayController ctrl = GamePlayController.getInstance();
    /**
     * Instance of FunctionClass
     */
    private static ViewFunctions func;
    /**
     * Instance of levelgrid
     */
    private static LevelGrid grid;
    /**
     * Is the image button for done
     */
    private static ImageView done;
    /**
     * Time is less than  seconds
     */
    private static boolean timeIsRunningOut;

    /**
     * I'ts the initial background opacity for error in assign robot
     */
    private final int INIT_ERROR_OPACITY = 7;
    /**
     * I'ts the final background opacity for error in assign robot
     */
    private final int FINAL_ERROR_OPACITY = 0;
    /**
     * Handler for runnables
     */
    private Handler handler = new Handler();
    /**
     * Array of robots from current level
     */
    private ArrayList<LinearLayout> inventoryCells = new ArrayList<>();
    /**
     * Array of images from current level
     */
    private ArrayList<ImageView> inventoryImages = new ArrayList<>();
    /**
     * Are the robots from current level
     */
    private LevelInventory currentInventory = ctrl.getGameData().getCurrentLevel().getInventory();
    /**
     * Are the robots from current level
     */
    private ArrayList<LevelInventoryCell> arrayRobotsFromInventory = currentInventory.getCells();
    /**
     * Check if inventory is in top position or bottom
     */
    private boolean isPortraitInventoryUp = PersistentData.getPortraitInventoryUp();
    /**
     * Check if inventory is in left position or roght
     */
    private boolean isPortraitInventoryLeft = PersistentData.getLandscapeInventoryLeft();
    /**
     * Is the countdown time level
     */
    private CountDownTimer countDownTimer;

    /**
     * Activity sounds
     */
    public static MediaPlayer soundPlaceRobot, soundClickRobot, musicGameLoop, soundExplosion, soundTrapsAttack, soundClock;

    /**
     * Delay default display robots dead function
     */
    private Runnable runShowDeadRobots = new Runnable() {
        public void run() {
            displayRobotsAsDead();
            handler.removeCallbacks(runShowDeadRobots);
        }
    };
    /**
     * Delay default explossion robots function
     */
    private Runnable runExplosions = new Runnable() {
        public void run() {
            displayRobotsExploding();
            handler.removeCallbacks(runExplosions);
        }
    };
    private Runnable stopAttackSound = new Runnable() {

        @Override
        public void run() {
            SoundHandler.stopSound(soundTrapsAttack);
        }
    };

    /**
     * On create
     *
     * @param savedInstanceState .
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        func = ViewFunctions.getInstance(this);


        if ((isPortraitInventoryUp && func.getOrientation() == Configuration.ORIENTATION_PORTRAIT) ||
                (isPortraitInventoryLeft && func.getOrientation() == Configuration.ORIENTATION_LANDSCAPE)) {
            setContentView(R.layout.activity_game_play);
        } else {
            setContentView(R.layout.activity_game_play_inventory_bottom);
        }

        /**
         * Static instance of me
         */
        GamePlayActivity.staticInstance = (this);
    }

    /**
     * On Start
     */
    @Override
    public void onStart() {
        super.onStart();

        /**
         * Add sounds activity
         */
        SoundHandler.stopAllMusics();
        addActivitySounds();

        if (func.getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        /**
         * Initialize variables that are always false
         */
        isNextLevelReady = false;
        timeIsRunningOut = false;
        hasPressedBackButton = false;

        /**
         * Instantiate the xml views.
         */
        gridView = (GridView) findViewById(R.id.gridview);
        timeLeft = (TextView) findViewById(R.id.TimeLeft);
        timeLeft.setText(String.valueOf(startTime / ViewFunctions.SECOND));

        /**
         * Getting the grid
         */
        grid = ctrl.getGameData().getCurrentLevel().getGrid();

        /**
         * Getting the adapter instance, setting its grid and adding the adapter to view
         */
        adapter = GridAdapter.getInstance(this);
        adapter.setGrid(grid);
        gridView.setAdapter(adapter);

        /**
         * Add square onClick listener
         */
        addAdapterListenerSquares(gridView, adapter);

        /**
         * Show the inventory of current level on the screen.
         */
        showInventory();

        /**
         * Add the configuration for Drag & Drop Inventory
         */
        dragAndDropHandlerConfiguration(adapter);

        /**
         * Adding to configuration for time left curr level
         */
        timeLeftHandlerConfiguration();

        /**
         * Ends the current timer and finalize current level
         */
        done = (ImageView) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTimerAndFinalizeLevel();
            }
        });

        /**
         * Always true in onStart
         */
        hasLeftTime = true;

        /**
         * Balancing robots execute in background
         */
        balanceRobots();

    }

    /**
     * Lower opacity cell
     *
     * @param obj  is an cell object
     * @param name is image of cell
     */
    public static void lowerOpactiyCell(LevelInventoryCell obj, String name) {
        int idImageView = func.getResourceId(name, "id");
        ImageView image = (ImageView) staticInstance.findViewById(idImageView);
        if (!obj.isStillSomeRobots()) {
            image.setAlpha(0.4f);
        } else {
            addRobotToInventaryFromGrid(image);
        }
    }

    /**
     * Show not matches inventory error.
     *
     * @param view is the current view
     */
    public static void showInventoryNotMatchesError(View view) {
        int idView = func.getResourceId(ViewFunctions.ROBOT_BG_PREFIX.concat(view.getTag().toString().toLowerCase()), "id");
        ImageView image = (ImageView) staticInstance.findViewById(idView);
        GamePlayActivity.staticInstance.showSetTimeOutError(image);
    }

    /**
     * Updates the number of remaining robots and also shows it on the screen
     *
     * @param robot is a name of the current robot.
     */
    public static void updateNumRemainingRobots(String robot, ViewFunctions.ActionCounterRobots action) {

        /**
         * Basic control view variables.
         */
        String cellRobotNum = ViewFunctions.ROBOT_CONT_PREFIX.concat(robot.toLowerCase());
        int idViewNum = func.getResourceId(cellRobotNum, "id");
        TextView robotCounter = (TextView) GamePlayActivity.staticInstance.findViewById(idViewNum);

        /**
         * Search to find the robot and its index decrease wether cell isn't null
         */
        LevelInventoryCell cell = ctrl.getGameData().getCurrentLevel().getInventory().getAtFromClassName(robot);

        if (cell != null) {

            if (action == ViewFunctions.ActionCounterRobots.DECREMENT) {
                if (cell.getRemainingRobotAmount() > 0)
                    cell.dcrRemainingRobotAmount();
            }
            if (action == ViewFunctions.ActionCounterRobots.INCREMENT) {
                if (cell.getRemainingRobotAmount() < cell.getTotalRobotAmount())
                    cell.inrRemainingRobotAmount();
            }

            String totalRobots = String.valueOf(cell.getTotalRobotAmount());
            String remainingRobots = String.valueOf(cell.getRemainingRobotAmount());
            //robotCounter.setText(remainingRobots + " / " + totalRobots);
            robotCounter.setText(remainingRobots);

            /**
             * Add or remove lowe opacity
             */
            lowerOpactiyCell(cell, robot);
        }
    }

    /**
     * Initialize light bg inventory
     */
    public static void initLightBackgroundInventory() {
        if (currentLightBackground != null) {
            currentLightBackground.setBackgroundResource(R.color.transparent);
        }
    }

    /**
     * Returning a robot to inventory from the matrix
     *
     * @param view is the current view.
     */
    public static void addRobotToInventaryFromGrid(View view) {
        view.setAlpha(1f);
    }

    /**
     * Evaluation actions after game-time is over
     */
    public static void evaluateLevelResult() {

        done.setVisibility(View.GONE);

        hasLeftTime = false;

        SoundHandler.stopAllMusics();

        SoundHandler.stopSound(soundClock);
        SoundHandler.playSound(soundTrapsAttack);

        timeLeft.setText("0");
        GamePlayController.TIME_LEFT = -1;

        /**
         * Remove all listeners
         */
        deactivateRobotListeners();

        /**
         * Adding red inventory bg if all robots not placed on the grid
         */

        boolean hasRobotsRemaining = ctrl.getGameData().getCurrentLevel().getInventory().hasRobotsRemaining();

        if (hasRobotsRemaining) {
            initLightBackgroundInventory();
            staticInstance.findViewById(R.id.Inventary).setBackgroundResource(R.drawable.bg_inventary_failure);
        }

        // determine if level is successful when updating the game state with new score and other
        // data
        boolean isSuccessful = GamePlayController.getInstance().updateGameState() && !DragAndDropHandler.anyRobotDraggedOn;

        // the attacks appear when the game timer ends
        displayTrapsAttack();

        // at the same time, explosive robots begin exploding
        displayExplosiveRobotsSelfExplosion();

        if (!GamePlayActivity.onRestartClipboardFromOrientation) {
            if (isSuccessful) {
                /**
                 * Timer go successful
                 */
                func.goWithTimeOut(staticInstance, ScoreScreenActivity.class, ViewFunctions.SECOND * 2, ViewFunctions.TransitionActivityMode.RIGHT_TO_LEFT);
                staticInstance.handler.postDelayed(staticInstance.stopAttackSound, ViewFunctions.SECOND * 2);
            } else {
                /**
                 * if any robot is placed on the grid
                 */
                if (grid.isAnyRobotPlaced()) {
                    /**
                     * Timer between the attack and the explosion
                     */
                    staticInstance.handler.postDelayed(staticInstance.runExplosions, ViewFunctions.SECOND);
                    /**
                     * Timer go game over
                     */
                } else {
                    func.goWithTimeOut(staticInstance, GameOverActivity.class, ViewFunctions.SECOND * 2, ViewFunctions.TransitionActivityMode.RIGHT_TO_LEFT);
                    staticInstance.handler.postDelayed(staticInstance.stopAttackSound, ViewFunctions.SECOND * 2);
                }
            }
        }
        GamePlayActivity.onRestartClipboardFromOrientation = true;
    }

    /**
     * Actions after backButton pressed
     */
    public static void stopByForce() {

        hasLeftTime = false;
        SoundHandler.stopSound(soundClock);

        timeLeft.setText("0");
        GamePlayController.TIME_LEFT = -1;

        deactivateRobotListeners();

        SoundHandler.stopAllMusics();
        func.goAndFinish(staticInstance, MainActivity.class, ViewFunctions.TransitionActivityMode.RIGHT_TO_LEFT);
    }

    /**
     * Shows the effects of each enabled trap-like object attacking
     */
    private static void displayTrapsAttack() {

        ArrayList<VisualEffectDisplayData> attackDisplayDataUnits = ctrl.obtainTrapsAttackDisplayUnits();

        // map to fill with the effect data units that each square contains
        HashMap<LevelSquare, ArrayList<VisualEffectDisplayData>> squaresAndEffectsMap = new HashMap<>();

        for (VisualEffectDisplayData dataUnit : attackDisplayDataUnits) {

            LevelSquare square = dataUnit.getSquare();

            if (squaresAndEffectsMap.containsKey(square)) {
                squaresAndEffectsMap.get(square).add(dataUnit);
            } else {
                ArrayList<VisualEffectDisplayData> dataUnits = new ArrayList<>();
                dataUnits.add(dataUnit);
                squaresAndEffectsMap.put(square, dataUnits);
            }
        }

        // DEBUG
        int dataNum = 0;

        // display all effects associated to each square
        for (LevelSquare keySquare : squaresAndEffectsMap.keySet()) {
            ArrayList<VisualEffectDisplayData> dataUnits = squaresAndEffectsMap.get(keySquare);
            // DEBUG
            dataNum += squaresAndEffectsMap.get(keySquare).size();
            adapter.displayEffects(dataUnits);
        }

        // DEBUG
        System.out.println(dataNum);
    }

    /**
     * Shows explosive robots exploding at their square
     */
    private static void displayExplosiveRobotsSelfExplosion() {
        for (VisualEffectDisplayData dataUnit : ctrl.obtainExplosiveRobotsSelfExplosionDisplayUnits()) {
            adapter.displayEffect(dataUnit, ViewFunctions.TypeExplosions.ROBOT_NORMAL_EXPLOSION);
            // hide the exploded explosive robot
            adapter.makeRobotInvisibleAtSquare(dataUnit.getSquare());
        }
    }

    /**
     * Shows the explosion of the robots that can't survive the attacks of traps
     */
    public static void displayRobotsExploding() {


        // squares of the robots that explode
        ArrayList<LevelSquare> explodingRobotsSquares = ctrl.obtainExplodingRobotsSquares();

        if (!explodingRobotsSquares.isEmpty()) {
            SoundHandler.stopSound(soundTrapsAttack);
            SoundHandler.playSound(soundExplosion);
        } else {
            staticInstance.handler.postDelayed(staticInstance.stopAttackSound, ViewFunctions.SECOND * 2);
        }

        /**
         * Traverse exploding robots' squares, and display an explosion for each of them
         */
        for (LevelSquare square : explodingRobotsSquares) {
            adapter.displayEffect(((Robot) (square.getObject())).obtainExplosionDataUnit
                    (square), ViewFunctions.TypeExplosions.ROBOT_EXPLOSIVE_EXPLOSION);
        }
        /**
         * Time in the explossion and the dead
         */
        staticInstance.handler.postDelayed(staticInstance.runShowDeadRobots, ViewFunctions.SECOND);
    }

    /**
     * Shows as dead the robots that can't survive the attacks of traps
     */
    public static void displayRobotsAsDead() {

        //squares of the robots that explode
        ArrayList<LevelSquare> explodingRobotsSquares = ctrl.obtainExplodingRobotsSquares();

        /**
         * Traverse array adding robot_dead into square
         */
        for (LevelSquare square : explodingRobotsSquares) {
            View view = GridAdapter.getInstance(staticInstance).obtainViewOfSquare(square);
            adapter.addRobotToSquare(view, "Robot_Dead");
        }

        /**
         * Timer go succesful
         */
        func.goWithTimeOut(staticInstance, GameOverActivity.class, ViewFunctions.SECOND * 2, ViewFunctions.TransitionActivityMode.RIGHT_TO_LEFT);
    }

    /**
     * Deactivate listeners when level is finished
     */
    private static void deactivateRobotListeners() {

        /**
         * Remove listener items squares
         */
        gridView.setOnItemClickListener(null);
        gridView.setOnItemLongClickListener(null);
        gridView.setOnDragListener(null);

        /**
         * Check the sizes of the arrays for safety
         */
        if (staticInstance.inventoryImages.size() == staticInstance.inventoryCells.size()) {
            /**
             * Only adding listeners if array-sizes are equals
             */
            for (ImageView obj : staticInstance.inventoryImages) {
                obj.setOnLongClickListener(null);
                obj.setOnDragListener(null);
            }
            /**
             * Error exists. Sizes are different.
             * Printing the error console.
             */
        } else {
            Log.i("Error >> ", "No size matches");
        }

        /**
         * Remove listeners step by step
         */
        for (View v : gamePlayViews) {
            v.setOnClickListener(null);
            v.setOnTouchListener(null);
            v.setOnLongClickListener(null);
        }
    }

    /**
     * Shows the done button when all robots are placed on the grid
     */
    public static void showDoneButtonIfAllRobotsPlaced() {

        boolean hasRobotsRemaining = ctrl.getGameData().getCurrentLevel().getInventory().hasRobotsRemaining();
        if (hasLeftTime)
            done.setVisibility((hasRobotsRemaining) ? View.GONE : View.VISIBLE);
        else
            done.setVisibility(View.GONE);
    }

    /**
     * Actions to do in each clock
     *
     * @param time is the time left game
     */
    public static void actionsOnTimerClock(String time) {

        // set umber text on each clock
        timeLeft.setText(String.valueOf(time));

        // activate alarm when time less than ten
        if (Integer.parseInt(time) < 5 && !timeIsRunningOut) {
            SoundHandler.playSound(soundClock);
        }

        // stop clock when time is zero
        if (Integer.parseInt(time) == 0) {
            SoundHandler.stopSound(soundClock);
        }
    }

    /**
     * Add onClick listeners on the squares.
     *
     * @param gridView it's an main layout matrix.
     * @param adapter  it's the controller view-model matrix and grid inflater.
     */
    private void addAdapterListenerSquares(final GridView gridView, final GridAdapter adapter) {
        /**
         * This is where the action begins simple click actions.
         */
        gridView.setOnItemClickListener(new SimpleClickHandler.ClickableInventoryRobot(this, adapter));
    }

    /**
     * Adding to the current level inventory.
     */
    private void showInventory() {

        int cont = 0;
        for (LevelInventoryCell cell : arrayRobotsFromInventory) {

            String robotName = cell.getRobotClass().getSimpleName();

            /**
             * Select id robot from resources
             */
            String cellRobotName = ViewFunctions.ROBOT_VIEW_PREFIX.concat(robotName.toLowerCase());
            int idView = func.getResourceId(cellRobotName, "id");

            /**
             * Adding this robot to inventory
             */
            inventoryCells.add((LinearLayout) findViewById(idView));
            inventoryCells.get(cont++).setVisibility(View.VISIBLE);

            /**
             * Select counter robots from resources
             */
            String cellRobotNum = ViewFunctions.ROBOT_CONT_PREFIX.concat(robotName.toLowerCase());
            int idViewNum = func.getResourceId(cellRobotNum, "id");

            /**
             * Adding total robots and robots left to inventory.
             */
            TextView robotCounter = (TextView) findViewById(idViewNum);
            String totalRobots = String.valueOf(cell.getTotalRobotAmount());
            String remainingRobots = String.valueOf(cell.getRemainingRobotAmount());

            //robotCounter.setText(remainingRobots + " / " + totalRobots);
            robotCounter.setText(remainingRobots);

            /**
             * Lower opacity if there are no robots of this type
             */
            int idImageView = func.getResourceId(robotName, "id");
            lowerOpactiyCell(cell, robotName);

            /**
             * Fill imageview array
             */
            inventoryImages.add((ImageView) findViewById(idImageView));
        }
    }

    /**
     * Configure drag and drop manager.
     *
     * @param adapter is the curr adapter.
     */
    private void dragAndDropHandlerConfiguration(GridAdapter adapter) {
        /**
         * Check the sizes of the arrays for safety
         */
        if (inventoryImages.size() == inventoryCells.size()) {
            /**
             * Only adding listeners if array-sizes are equals
             */
            for (ImageView obj : inventoryImages) {
                obj.setOnTouchListener(new DragAndDropHandler.TouchableInventoryRobot());
                obj.setOnDragListener(new DragAndDropHandler.DraggableInventoryRobot(adapter));
            }
            /**
             * Error exists. Sizes are different.
             * Printing the error console.
             */
        } else {
            Log.i("Error >> ", "No size matches");
        }
    }

    /**
     * User's selecting the robot from the inventary
     *
     * @param view is the item application xml view
     */
    public void selectRobotFromInventary(View view) {
        SimpleClickHandler.selectRobotFromInventary(view);
    }

    /**
     * Asyncronous setTimeout(function) to show square-error.
     *
     * @param image is the current GridAdapter Square.
     */
    public void showSetTimeOutError(final ImageView image) {
        new Thread() {
            public void run() {
                for (int i = INIT_ERROR_OPACITY; i >= FINAL_ERROR_OPACITY; i--) {
                    try {
                        final int finalI = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.showBackgroundSquareError(image, (float) finalI);
                            }
                        });
                        Thread.sleep(60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * Configure time left timer.
     */
    private void timeLeftHandlerConfiguration() {
        startTime = ctrl.getGameData().getCurrentLevel().getTotalTime();
        if (GamePlayController.TIME_LEFT > -1) {
            startTime = GamePlayController.TIME_LEFT;
        }
        countDownTimer = new TimerHandler.TimeGameLeft(startTime);
        countDownTimer.start();
    }

    @Override
    public void onBackPressed() {
        if (hasLeftTime) {
            cancelTimerAndFinalizeLevelWhenPressedBackButton();
        }
    }

    /**
     * Ends the current timer and finalize current level
     */
    private void cancelTimerAndFinalizeLevel() {
        countDownTimer.onFinish();
        countDownTimer.cancel();
    }

    /**
     * Individually call for each square effect run in background
     *
     * @param overlap       is the overlap square layer
     * @param rotationAngle is the rotation angle
     * @param drawableId    is the id for each drawable image
     */
    public void addDisplayEffectsBackground(final GifImageView overlap, final float rotationAngle, final int drawableId) {
        overlap.setVisibility(View.VISIBLE);
        overlap.setRotation(rotationAngle);

        //This method implements the runnable thread
        overlap.loadGIFResource(drawableId, GamePlayActivity.this);
    }

    /**
     * Incremented or decremented by robots balanced
     */
    public void balanceRobots() {
        new Thread() {
            public void run() {
                while (hasLeftTime) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                // Check if number robots is balanced
                                int balance = ctrl.getGameData().getCurrentLevel().checkIfRobotsGoneExists();

                                // Robots aren't balanced in favor of the matrix
                                if (balance == 1) {
                                    GamePlayActivity.updateNumRemainingRobots(DragAndDropHandler.startRobotOnDrag, ViewFunctions.ActionCounterRobots.INCREMENT);
                                }

                                // Robots aren't balanced in favor of the inventory
                                if (balance == -1) {
                                    GamePlayActivity.updateNumRemainingRobots(DragAndDropHandler.startRobotOnDrag, ViewFunctions.ActionCounterRobots.DECREMENT);
                                }
                            }
                        });
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * Ends the current timer and finalize current level
     */
    private void cancelTimerAndFinalizeLevelWhenPressedBackButton() {
        /**
         * Exit backPressed Dialog
         */
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        hasPressedBackButton = true;
                        cancelTimerAndFinalizeLevel();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(GamePlayActivity.this);
        builder.setMessage(R.string.quit_message).setPositiveButton(R.string.yes, dialogClickListener).setNegativeButton(R.string.no, dialogClickListener).show();
    }

    /**
     * Adding activity sounds
     */
    private void addActivitySounds() {

        soundPlaceRobot = MediaPlayer.create(getApplicationContext(), R.raw.robot_placed);
        soundClickRobot = MediaPlayer.create(getApplicationContext(), R.raw.selection_robot);
        soundExplosion = MediaPlayer.create(getApplicationContext(), R.raw.explosion);
        soundTrapsAttack = MediaPlayer.create(getApplicationContext(), R.raw.traps_attack);
        musicGameLoop = MediaPlayer.create(getApplicationContext(), R.raw.music_loop);
        soundClock = MediaPlayer.create(getApplicationContext(), R.raw.clock);
        SoundHandler.playSoundWithLoop(musicGameLoop);
        SoundHandler.musicStack.add(musicGameLoop);
    }

    @Override
    public void onPause() {
        super.onPause();
        SoundHandler.stopSound(musicGameLoop);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        SoundHandler.playSound(musicGameLoop);
    }
}




