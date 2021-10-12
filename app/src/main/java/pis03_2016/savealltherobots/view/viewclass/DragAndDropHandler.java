package pis03_2016.savealltherobots.view.viewclass;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Handler;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import pis03_2016.savealltherobots.R;
import pis03_2016.savealltherobots.controller.GamePlayController;
import pis03_2016.savealltherobots.controller.GridAdapter;
import pis03_2016.savealltherobots.model.LevelInventoryCell;
import pis03_2016.savealltherobots.model.LevelSquare;
import pis03_2016.savealltherobots.model.Robot;
import pis03_2016.savealltherobots.view.activity.GamePlayActivity;


public abstract class DragAndDropHandler {

    /**
     * Init int drag event
     */
    public static final int INIT_START_DRAG = 0;

    /**
     * Is start image overlap on drag action.
     */
    public static View startOverlapOnDrag;
    /**
     * Is robot name start on drag action.
     */
    public static String startRobotOnDrag;
    /**
     * Check if any robot is dragged from GamePlay
     */
    public static boolean anyRobotDraggedOn;
    /**
     * Game controller instance
     */
    private static GamePlayController ctrl = GamePlayController.getInstance();
    /**
     * Is start view overlap on drag action.
     */
    private static View startViewOnDrag;
    /**
     * Is robot dragged from inventory?
     */
    private static boolean isRobotDraggedFromInventory = false;
    /**
     * Check increments robots inventory
     */
    private static boolean robotDraggedFromInventoryIsIncremented = false;

    /**
     * InnerClass:: This class represents the action of the grid-item touch
     */
    public static class TouchableGridRobot implements View.OnTouchListener {

        /**
         * Instance of Grio Adapter
         */
        private GridAdapter adapter;

        /**
         * Current position
         */
        private int position;

        /**
         * Is the current view
         */
        private View view;

        private Context context;

        /**
         * TouchableGridRobot constructor.
         *
         * @param adapter  is the Grid Adapter
         * @param position is the curr position
         */
        public TouchableGridRobot(GridAdapter adapter, int position, Context context) {
            this.adapter = adapter;
            this.position = position;
            this.context = context;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {

            /**
             * Getting square of the vurrent view
             */
            LevelSquare square = adapter.getItem(position);

            /**
             * Only touchable if square is robot and is occupied else not touchable
             */
            if (square.isOccupied() && square.getObject() instanceof Robot) {

                /**
                 * The current view
                 */
                this.view = view;

                /**
                 * Robots are pressed
                 */
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    actionDragEnable();
                    SimpleClickHandler.cleanClipboardVariablesActionClick();
                    GamePlayActivity.initLightBackgroundInventory();
                    SoundHandler.playSound(GamePlayActivity.soundClickRobot);
                }

                return true;
            }
            return false;
        }

        /**
         * Call actions drag enabled
         */
        private void actionDragEnable() {

            /**
             * Current level-square
             */
            LevelSquare square = adapter.getItem(position);

            if (square != null && square.getObject() instanceof Robot) {
                ClipData data = ClipData.newPlainText("simple text", "text");

                /**
                 * Clipboard variables
                 */
                startViewOnDrag = view;
                startOverlapOnDrag = view.findViewById(R.id.PictureOverlapLayer);
                startRobotOnDrag = square.getObject().getClass().getSimpleName();

                /**
                 * Always be false at this point
                 */
                isRobotDraggedFromInventory = false;

                /**
                 * Adding drag shadow
                 */
                View.DragShadowBuilder sb = new DragAndDropHandler.DragShadowBuilder(startOverlapOnDrag);
                view.startDrag(data, sb, startViewOnDrag, INIT_START_DRAG);

                /**
                 * Removing robot on start drag
                 */
                adapter.removeRobotFromSquare(startViewOnDrag);
            }
        }
    }

    /**
     * InnerStaticClass:: It represents a draggable item grid.
     */
    public static class DraggableGridRobot implements View.OnDragListener {

        /**
         * Current level-square
         */
        private final LevelSquare square;

        /**
         * Adapter instance.
         */
        private GridAdapter adapter;

        /**
         * DraggableGridRobot Constructor.
         *
         * @param adapter is the adapter.
         * @param square  is the curr square
         */
        public DraggableGridRobot(GridAdapter adapter, LevelSquare square) {
            this.square = square;
            this.adapter = adapter;
        }

        /**
         * On drag method
         *
         * @param view  is the curr view
         * @param event is the action event
         * @return true if start-drag is possible and successful completion
         */
        @Override
        public boolean onDrag(View view, DragEvent event) {

            /**
             * Action draggable event
             */
            int action = event.getAction();

            switch (action) {

                /**
                 * Starting drag event
                 */
                case DragEvent.ACTION_DRAG_STARTED:

                    anyRobotDraggedOn = true;

                    return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

                /**
                 * Action drop
                 */
                case DragEvent.ACTION_DROP:

                    if (square != null) {

                        if (!square.isOccupied()) {

                            /**
                             * Disable add robot when time is end
                             */
                            if (!GamePlayActivity.timeLeft.getText().equals("0")) {
                                if (isRobotDraggedFromInventory)
                                    GamePlayActivity.updateNumRemainingRobots(startRobotOnDrag, ViewFunctions.ActionCounterRobots.DECREMENT);
                                adapter.addRobotToSquare(view, startRobotOnDrag);

                                /**
                                 * Lower opacity if there are no robots of this type
                                 */
                                if (isRobotDraggedFromInventory) {
                                    LevelInventoryCell cell = ctrl.getGameData().getCurrentLevel().getInventory().getAtFromClassName(startRobotOnDrag);
                                    GamePlayActivity.lowerOpactiyCell(cell, startRobotOnDrag);
                                }
                            } else if (isRobotDraggedFromInventory)
                                GamePlayActivity.addRobotToInventaryFromGrid(startViewOnDrag);
                            else
                                adapter.addRobotToSquare(startViewOnDrag, startRobotOnDrag);

                        } else {
                            /**
                             * Adding bg red error
                             */
                            ImageView image = (ImageView) view.findViewById(R.id.PictureErrorLayer);
                            GamePlayActivity.staticInstance.showSetTimeOutError(image);

                            /**
                             * Robot is dragged from inventory
                             */
                            if (isRobotDraggedFromInventory) {

                                GamePlayActivity.addRobotToInventaryFromGrid(startViewOnDrag);

                            } else {
                                adapter.addRobotToSquare(startViewOnDrag, startRobotOnDrag);
                            }
                        }
                    }
                    break;

                /**
                 * Ending drag event
                 */
                case DragEvent.ACTION_DRAG_ENDED:

                    anyRobotDraggedOn = false;

                    if (!event.getResult()) {

                        if (!isRobotDraggedFromInventory) {
                            GamePlayActivity.addRobotToInventaryFromGrid(startViewOnDrag);
                        }
                    }

                    break;
            }
            return true;
        }
    }

    /**
     * InnerStaticClass:: It represents a touch item inventory.
     */
    public static class TouchableInventoryRobot implements View.OnTouchListener {

        /**
         * Handler of touch
         */
        private final Handler handler = new Handler();
        /**
         * Square has pressed or not
         */
        private boolean isPressed;
        /**
         * Is the current view
         */
        private View view;
        /**
         * Run the drag
         */
        private final Runnable runnableDown = new Runnable() {
            public void run() {
                actionDragEnable();
            }
        };
        /**
         * Robot has been clicked with simpleClickHandler
         */
        private boolean isRobotSimpleClickSelected = false;
        /**
         * Run the move
         */
        private final Runnable runnableMove = new Runnable() {
            public void run() {
                SimpleClickHandler.cleanClipboardVariablesActionClick();
                isRobotSimpleClickSelected = false;
            }
        };

        /**
         * OnTouch
         *
         * @param view  is the curr view
         * @param event is the action event
         * @return bool
         */
        @Override
        public boolean onTouch(View view, MotionEvent event) {

            this.view = view;

            /**
             * Living with simple functionality
             */
            if (!isRobotSimpleClickSelected) {
                SimpleClickHandler.selectRobotFromInventary(view);
                isRobotSimpleClickSelected = true;
            }

            /**
             * Robots are moving
             */
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                handler.postDelayed(runnableMove, 150);
            }

            /**
             * Robots are pressed
             */
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                handler.postDelayed(runnableDown, 100);
                isPressed = true;
            }

            /**
             * Robots are placed on the grid
             */
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (isPressed) {
                    isPressed = false;
                    handler.removeCallbacks(runnableDown);
                    handler.removeCallbacks(runnableMove);
                    isRobotSimpleClickSelected = false;
                }
            }

            return true;
        }

        /**
         * Actions to make on drag item
         */
        private void actionDragEnable() {

            if (isPressed) {

                String robotName = (String) view.getTag();
                LevelInventoryCell cell = ctrl.getGameData().getCurrentLevel().getInventory().getAtFromClassName(robotName);

                if (cell.isStillSomeRobots()) {

                    /**
                     * Decrement num-robots and init light bg click
                     */
                    GamePlayActivity.initLightBackgroundInventory();

                    startRobotOnDrag = robotName;
                    startViewOnDrag = view;

                    ClipData data = ClipData.newPlainText("simple text", "text");

                    View.DragShadowBuilder sb = new DragAndDropHandler.DragShadowBuilder(view);
                    view.startDrag(data, sb, view, 0);

                    GamePlayActivity.lowerOpactiyCell(cell, robotName);

                    isRobotDraggedFromInventory = true;
                    robotDraggedFromInventoryIsIncremented = false;
                }
            }
        }
    }

    /**
     * InnerStaticClass:: It represents a draggable item inventory.
     */
    public static class DraggableInventoryRobot implements View.OnDragListener {

        /**
         * Adapter instance.
         */
        private GridAdapter adapter;

        /**
         * DraggableInventoryRobot Constructor
         *
         * @param adapter is the adapter
         */
        public DraggableInventoryRobot(GridAdapter adapter) {
            this.adapter = adapter;
        }

        /**
         * onDrag method.
         *
         * @param view  is the curr view
         * @param event is the event drag
         * @return true if start-drag is possible and successful completion
         */
        @Override
        public boolean onDrag(View view, DragEvent event) {

            int action = event.getAction();

            switch (action) {

                /**
                 * Starting drag event
                 */
                case DragEvent.ACTION_DRAG_STARTED:

                    anyRobotDraggedOn = true;

                    return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

                /**
                 * Action drop
                 */
                case DragEvent.ACTION_DROP:
                    String robotName = (String) view.getTag();

                    /**
                     * Case in which the start and goal are the same
                     */
                    if (robotName.equals(startRobotOnDrag)) {
                        if (!isRobotDraggedFromInventory)
                            GamePlayActivity.updateNumRemainingRobots(robotName, ViewFunctions.ActionCounterRobots.INCREMENT);
                        GamePlayActivity.addRobotToInventaryFromGrid(view);
                        GamePlayActivity.showDoneButtonIfAllRobotsPlaced();

                        /**
                         * Case in which the start and goal are not equals
                         */
                    } else {
                        /**
                         * Adding bg red error
                         */
                        GamePlayActivity.showInventoryNotMatchesError(view);
                        /**
                         * Clipboard robot is dragged from inventory
                         */
                        if (isRobotDraggedFromInventory) {

                            GamePlayActivity.addRobotToInventaryFromGrid(startViewOnDrag);
                            GamePlayActivity.showDoneButtonIfAllRobotsPlaced();

                            /**
                             * Normal case where the robot comes from the grid
                             */
                        } else {
                            adapter.addRobotToSquare(startViewOnDrag, startRobotOnDrag);
                        }
                    }
                    break;

                /**
                 * Ending drag event
                 */
                case DragEvent.ACTION_DRAG_ENDED:

                    anyRobotDraggedOn = false;

                    /**
                     * Drop failed actions. The robot goes nowhere.
                     */
                    if (!event.getResult()) {
                        if (isRobotDraggedFromInventory) {
                            if (!robotDraggedFromInventoryIsIncremented) {
                                GamePlayActivity.addRobotToInventaryFromGrid(startViewOnDrag);
                                robotDraggedFromInventoryIsIncremented = true;
                            }
                        }
                    }
                    break;
            }
            return true;
        }
    }

    /**
     * InnerStaticClass:: Customize shadow on drag
     */
    public static class DragShadowBuilder extends View.DragShadowBuilder {

        /**
         * Is the news class factor
         */
        private Point scaleFactor;

        /**
         * DragShadowBuilder Constructor.
         *
         * @param view is the curr view
         */
        public DragShadowBuilder(View view) {
            super(view);
        }

        @Override
        public void onProvideShadowMetrics(Point size, Point touch) {
            /**
             * Base sizes
             */
            int width;
            int height;
            /**
             * New shadow size
             */
            width = getView().getWidth() * 2;
            height = getView().getHeight() * 2;
            /**
             * Get touch in the middle of image
             */
            size.set(width, height);
            scaleFactor = size;
            touch.set(width / 2, height / 2);
        }

        /**
         * Draw shadow method
         *
         * @param canvas canvas
         */
        @Override
        public void onDrawShadow(Canvas canvas) {
            canvas.scale(scaleFactor.x / (float) getView().getWidth(), scaleFactor.y / (float) getView().getHeight());
            getView().draw(canvas);
        }
    }
}