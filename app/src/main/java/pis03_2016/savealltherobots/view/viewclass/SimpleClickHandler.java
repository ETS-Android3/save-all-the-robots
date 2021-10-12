package pis03_2016.savealltherobots.view.viewclass;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import pis03_2016.savealltherobots.R;
import pis03_2016.savealltherobots.controller.GamePlayController;
import pis03_2016.savealltherobots.controller.GridAdapter;
import pis03_2016.savealltherobots.model.LevelInventoryCell;
import pis03_2016.savealltherobots.model.LevelSquare;
import pis03_2016.savealltherobots.view.activity.GamePlayActivity;


public abstract class SimpleClickHandler {

    /**
     * The robot has been selected from the inventory
     */
    public static boolean robotSelectedFromInventary = false;
    /**
     * Instance of adapter
     */
    private static GridAdapter adapter;
    /**
     * Controller instance
     */
    private static GamePlayController ctrl = GamePlayController.getInstance();
    /**
     * Functions instance
     */
    private static ViewFunctions func;
    /**
     * Application activity context
     */
    private static Activity context;
    /**
     * It's the robot type selected
     */
    private static String robotSelected = null;


    /**
     * Add robot to square from inventory
     *
     * @param view is the current view
     */
    private static void addRobotToSquareFromInventory(View view) {

        adapter.addRobotToSquare(view, robotSelected);

        GamePlayActivity.updateNumRemainingRobots(robotSelected, ViewFunctions.ActionCounterRobots.DECREMENT);

        LevelInventoryCell cell = ctrl.getGameData().getCurrentLevel().getInventory().getAtFromClassName(robotSelected);

        /**
         * Lower opacity if there are no robots of this type
         */
        GamePlayActivity.lowerOpactiyCell(cell, robotSelected);

    }

    /**
     * Clean global variables for selection robots.
     */
    public static void cleanClipboardVariablesActionClick() {
        robotSelected = null;
        robotSelectedFromInventary = false;
    }

    /**
     * @return whether some robot is selected.
     */
    private static boolean isRobotSelected() {
        return robotSelected != null;
    }


    /**
     * @return whether some robot is selected from Inventary.
     */
    private static boolean isRobotSelectedFromInventary() {
        return robotSelectedFromInventary;
    }

    /**
     * Selects a robot from the inventory clicked a view
     *
     * @param view view
     */
    public static void selectRobotFromInventary(View view) {

        LevelInventoryCell cell = ctrl.getGameData().getCurrentLevel().getInventory().getAtFromClassName(view.getTag().toString());
        GamePlayActivity.initLightBackgroundInventory();

        /**
         * Click in inventory when not robot selected from grid.
         */
        if (cell.isStillSomeRobots()) {
            selectionRobot(view);
        }
    }


    /**
     * Actions for selection a robot
     *
     * @param view is the current view
     */
    private static void selectionRobot(View view) {

        SoundHandler.playSound(GamePlayActivity.soundClickRobot);

        /**
         * Light in the selected inventory
         */
        int idView = func.getResourceId(ViewFunctions.ROBOT_LIGHT_PREFIX.concat(view.getTag().toString().toLowerCase()), "id");
        FrameLayout frame = (FrameLayout) context.findViewById(idView);

        frame.setBackgroundResource(R.drawable.bg_inventary_selected);
        GamePlayActivity.currentLightBackground = frame;

        /**
         * Now there robot selected inventory
         */
        robotSelected = view.getTag().toString();
        robotSelectedFromInventary = true;

    }

    /**
     * InnerClass:: This class represents the action of the simple click
     */
    public static class ClickableInventoryRobot implements AdapterView.OnItemClickListener {

        /**
         * Instance adapter
         */
        private GridAdapter adapter;

        /**
         * ClickableInventoryRobot Constructor
         *
         * @param context is the app context
         * @param adapter is the adpter instance
         */
        public ClickableInventoryRobot(Activity context, GridAdapter adapter) {

            this.adapter = adapter;

            SimpleClickHandler.adapter = adapter;
            SimpleClickHandler.func = ViewFunctions.getInstance(context);
            SimpleClickHandler.context = context;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            /**
             * Current square
             */
            LevelSquare square = adapter.getViewsAndSquaresMap().get(view);//getItem(position);
            ImageView image = (ImageView) view.findViewById(R.id.PictureErrorLayer);

            /**
             * Has more robots?
             */
            boolean hasRobotsRemaining = ctrl.getGameData().getCurrentLevel().getInventory().hasRobotsRemaining();

            /**
             * Inventory cell robot selected
             */
            LevelInventoryCell cell = ctrl.getGameData().getCurrentLevel().getInventory().getAtFromClassName(robotSelected);

            /**
             * Has more robots of current type?
             */
            boolean isStillSomeRobots = false;

            if (cell != null) {
                isStillSomeRobots = cell.isStillSomeRobots();
            }

            /**
             * Some robot has been selected
             */
            if (isRobotSelected() && hasRobotsRemaining) {
                /**
                 * Robot inventory has been selected
                 */
                if (isRobotSelectedFromInventary()) {

                    /**
                     * The square isn't occupied
                     */
                    if (!square.isOccupied() && isStillSomeRobots) {

                        // add robot to square from intentory
                        addRobotToSquareFromInventory(view);

                        // add new value for variable
                        isStillSomeRobots = cell.isStillSomeRobots();

                        // We checked the value again
                        if (!isStillSomeRobots) {
                            GamePlayActivity.initLightBackgroundInventory();
                            cleanClipboardVariablesActionClick();
                        }

                        /**
                         * The square is already occupied.
                         */
                    } else {
                        GamePlayActivity.staticInstance.showSetTimeOutError(image);
                    }
                }
            }
        }
    }
}
