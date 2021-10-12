package pis03_2016.savealltherobots.controller;

import android.content.Context;

import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

import pis03_2016.savealltherobots.R;
import pis03_2016.savealltherobots.model.LevelGrid;
import pis03_2016.savealltherobots.model.LevelSquare;
import pis03_2016.savealltherobots.model.MathLibrary;
import pis03_2016.savealltherobots.model.PlaceableObject;
import pis03_2016.savealltherobots.model.Robot;
import pis03_2016.savealltherobots.model.Trap;
import pis03_2016.savealltherobots.model.VisualEffectDisplayData;
import pis03_2016.savealltherobots.view.activity.GamePlayActivity;
import pis03_2016.savealltherobots.view.viewclass.DragAndDropHandler;
import pis03_2016.savealltherobots.view.viewclass.GifImageView;
import pis03_2016.savealltherobots.view.viewclass.SoundHandler;
import pis03_2016.savealltherobots.view.viewclass.ViewFunctions;


/**
 * This class represents the grid adapter and inflate picture squares
 */
public final class GridAdapter extends BaseAdapter {

    /**
     * Angle to correctly display images in landscape
     */
    private static final float LANDSCAPE_CORRECTION_ANGLE = 270;
    /**
     * Change to true when screen position is changed
     */
    public static boolean isInitialized = false;
    /**
     * This is the only instance of this class (Singleton Pattern Object)
     */
    private static GridAdapter instance = null;
    /**
     * It's the view-position on each calling method.
     */
    private static int inflaterCount = 0;
    /**
     * Adapter attributes.
     */
    private final LayoutInflater inflater;
    /**
     * Instance of view-functions-class
     */
    private final ViewFunctions func;
    /**
     * Application context
     */
    private final Context context;
    /**
     * Map to relate each square with its visual representation (its view)
     */
    private HashMap<View, LevelSquare> viewsAndSquaresMap;
    /**
     * Inflater indices for landscape
     */
    private ArrayList<Integer> inflaterLandscapeIndices;
    /**
     * grid object
     */
    private LevelGrid grid;

    /**
     * Instance of game play activity
     */
    private GamePlayActivity gamePlayObject;

    /**
     * Constructor
     *
     * @param context is the context of calling activity
     */
    public GridAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.func = ViewFunctions.getInstance(context);
        this.context = context;
        this.gamePlayObject = (GamePlayActivity) context;

        establishLandscapeIndices();

        this.viewsAndSquaresMap = new HashMap<>();

    }

    /**
     * Getter of this instance using lazy creation.
     */
    public static GridAdapter getInstance(Context context) {
        inflaterCount = 0;
        if (instance == null) {
            instance = new GridAdapter(context);
        }
        return instance;
    }

    /**
     * Leads to show the passed square's trap as deactivated
     *
     * @param affectedSquare square to affect
     */
    public static void showTrapAtSquareAsDeactivated(LevelSquare affectedSquare) {
        GridAdapter adapter = GridAdapter.getInstance(GamePlayActivity.staticInstance);
        View view = adapter.obtainViewOfSquare(affectedSquare);
        if (view != null) {
            adapter.addOverlapTransparency(view);
        }
    }

    /**
     * Leads to show the passed square's trap as reactivated after deactivation
     *
     * @param affectedSquare square to affect
     */
    public static void showTrapAtSquareAsReactivated(LevelSquare affectedSquare) {
        GridAdapter adapter = GridAdapter.getInstance(GamePlayActivity.staticInstance);
        View view = adapter.obtainViewOfSquare(affectedSquare);
        if (view != null) {
            adapter.removeOverlapTransparency(view);
        }
    }

    /**
     * Establish an array of indices that translate the squares indices when in landscape mode:
     * this is required because the inflater inflates differently depending on the mobile orientation,
     * but we want the squares to be physically visualized in the same place, taking into account
     * that the model is orientation-independent
     */
    private void establishLandscapeIndices() {
        ArrayList<ArrayList<Integer>> baseMatrix = new ArrayList<>();
        int k = 0;
        for (int i = 0; i < LevelGrid.NUM_ROWS; i++) {
            baseMatrix.add(new ArrayList<Integer>());
            for (int j = 0; j < LevelGrid.NUM_COLS; j++) {
                baseMatrix.get(i).add(k++);
            }
        }

        ArrayList<ArrayList<Integer>> landscapeMatrix =
                MathLibrary.obtainInvertedRowsMatrix(
                        MathLibrary.obtainTransposeMatrix(baseMatrix));

        inflaterLandscapeIndices = new ArrayList<>();
        for (int i = 0; i < landscapeMatrix.size(); i++) {
            for (int j = 0; j < landscapeMatrix.get(0).size(); j++) {
                inflaterLandscapeIndices.add(landscapeMatrix.get(i).get(j));
            }
        }
    }

    /**
     * It gets the number of squares the grid contains.
     *
     * @return the number of squares 6 x 8 = 48
     */
    @Override
    public int getCount() {
        return grid.getSquares().size();
    }

    /**
     * Gets an item identified by an index.
     *
     * @param index the index of an item in array
     * @return the square-item
     */
    @Override
    public LevelSquare getItem(int index) {
        return grid.getAt(index);
    }

    /**
     * Gets an item identified by an index.
     *
     * @param index the index of an item in array
     * @return the integer of drawable image id
     */
    @Override
    public long getItemId(int index) {
        return grid.getAt(index).drawableId;
    }

    /**
     * Returns the grid
     *
     * @return grid.
     */
    public LevelGrid getGrid() {
        return grid;
    }

    /**
     * Sets the grid
     *
     * @param grid the grid to set
     */
    public void setGrid(LevelGrid grid) {
        this.grid = grid;
    }

    /**
     * Returns the views and squares map
     *
     * @return the views and squares map
     */
    public HashMap<View, LevelSquare> getViewsAndSquaresMap() {
        return viewsAndSquaresMap;
    }

    /**
     * Gets the view of the defined object in the xml
     *
     * @param pos       the square position
     * @param view      the single element square
     * @param viewGroup the group total squares
     * @return view squares
     */
    @Override
    public View getView(final int pos, View view, final ViewGroup viewGroup) {

        /**
         * The view is only filled if it's empty
         */
        if (view == null) {
            view = inflater.inflate(R.layout.grid_item, viewGroup, false);
            ImageView picture = (ImageView) view.findViewById(R.id.PictureOverlapLayer);
            picture.setImageResource(android.R.color.transparent);

            if (inflaterCount < LevelGrid.GRID_SIZE) {

                /**
                 * Recovery images onRestart Activity GamePlay.
                 */
                int index = (func.getOrientation() == Configuration.ORIENTATION_LANDSCAPE) ?
                        inflaterLandscapeIndices.get(pos) : pos;
                LevelSquare square = grid.getAt(index);

                /*if (func.getOrientation() == Configuration.ORIENTATION_LANDSCAPE){
                    square = grid.getAt(square.getCol() * LevelGrid.NUM_ROWS + square.getRow());
                }*/

                // associate view and square for fast access in the future
                viewsAndSquaresMap.put(view, square);

                /**
                 * Add view to aray for future disable on level ending
                 */
                GamePlayActivity.gamePlayViews.add(view);

                /**
                 * set an image for the square object
                 */
                if (square != null && square.getObject() != null && !isInitialized) {
                    configureImageForSquareObject(view, square);
                }

                /**
                 * Square already occupied.
                 * Only action recovery in this case.
                 */
                if (square != null && square.isOccupied() && isInitialized) {
                    restoreSquareObject(view, square);
                }

                /**
                 * Increment the view count on each method call.
                 */
                inflaterCount++;

                /**
                 * Is true when count > grid size
                 */
                if (inflaterCount >= LevelGrid.GRID_SIZE - 1) {
                    isInitialized = true;
                }

                /**
                 * Adding touch actions to curr view
                 */
                view.setOnTouchListener(new DragAndDropHandler.TouchableGridRobot(this, index, context));

                /**
                 * Adding drag & drop actions to current view
                 */
                view.setOnDragListener(new DragAndDropHandler.DraggableGridRobot(this, square));
            }

        }
        return view;
    }

    /**
     * Restore object of square when screen is changed.
     *
     * @param view   the current square view
     * @param square the square of grid
     */
    private void restoreSquareObject(View view, LevelSquare square) {
        configureImageForSquareObject(view, square);
    }

    /**
     * Configures the image of an object on the square corresponding to the passed view
     *
     * @param view   the current square view
     * @param square the square object
     */
    public void configureImageForSquareObject(View view, LevelSquare square) {
        ImageView picture = (ImageView) view.findViewById(R.id.PictureOverlapLayer);
        setOverlapImage(picture, obtainDrawableIdForObject(square.getObject()));

        // rotate image depending on mobile orientation and trap orientation, if it's a trap image
        if (!(square.getObject() instanceof Robot)) {

            float mobileOrientationAngle = obtainMobileOrientationBaseAngleForImages(view.getContext());
            float extraRotationAngle = 0;

            if (square.getObject() instanceof Trap) {
                extraRotationAngle = ((Trap) square.getObject()).getOrientation()
                        .obtainRotationAngle();

            }

            picture.setRotation(mobileOrientationAngle + extraRotationAngle);
        }
    }

    /**
     * Returns the base angle for images depending on current mobile orientation
     *
     * @return the rotation angle
     */
    private float obtainMobileOrientationBaseAngleForImages(Context context) {

        // for portrait or undefined, angle is left to be 0
        float angle = 0;

        if (func.getOrientation(context) == Configuration.ORIENTATION_LANDSCAPE) {
            angle = 270;
        }
        return angle;
    }

    /**
     * Returns the drawable id that corresponds to the passed object
     *
     * @param object object to find drawable id for
     * @return the drawable id
     */
    private int obtainDrawableIdForObject(PlaceableObject object) {
        return func.getResourceId(object.getClass().getSimpleName().toLowerCase(), "drawable");
    }

    /**
     * Change background of the slot at run time and relates the obj with the view.
     *
     * @param view  is the context of calling activity.
     * @param robot String to identify the robot type or condition
     */
    public void addRobotToSquare(View view, String robot) {
        SoundHandler.playSound(GamePlayActivity.soundPlaceRobot);
        ImageView picture = (ImageView) view.findViewById(R.id.PictureOverlapLayer);
        if (robot != null) {
            int drawableId = func.getResourceId(robot.toLowerCase(), "drawable");
            setOverlapImage(picture, drawableId);
            PlaceableObject obj = (PlaceableObject) func.createObject(ViewFunctions.SATR_PREFIX.concat(".model.").concat(robot));
            grid.installObjectAt(obj, getViewsAndSquaresMap().get(view));
        }
        GamePlayActivity.showDoneButtonIfAllRobotsPlaced();
    }

    /**
     * Remove placeable object from square.
     *
     * @param currentView view
     */
    public void removeRobotFromSquare(View currentView) {
        LevelSquare square = getViewsAndSquaresMap().get(currentView);
        grid.uninstallObjectAt(square);
        cleanStartSquare(currentView);
    }

    /**
     * Add medium opacity factor in current square.
     *
     * @param view view it's the current square.
     */
    public void addOverlapTransparency(View view) {
        ImageView picture = (ImageView) view.findViewById(R.id.PictureOverlapLayer);
        setOverlapAlpha(picture, 0.6f);
    }

    /**
     * Make image on view of square completely transparent
     *
     * @param view current square view
     */
    public void makeOverlapInvisible(View view) {
        ImageView picture = (ImageView) view.findViewById(R.id.PictureOverlapLayer);
        setOverlapAlpha(picture, 0.0f);
    }

    /**
     * Add max opacity factor in current square.
     *
     * @param view @param view it's the current square.
     */
    public void removeOverlapTransparency(View view) {
        ImageView picture = (ImageView) view.findViewById(R.id.PictureOverlapLayer);
        setOverlapAlpha(picture, 1.0f);
    }

    /**
     * Clean the start square in a change robot from grid.
     *
     * @param view it's the current square.
     */
    public void cleanStartSquare(View view) {
        ImageView picture = (ImageView) view.findViewById(R.id.PictureOverlapLayer);
        setOverlapAlpha(picture, 1.0f);
        setOverlapImage(picture, android.R.color.transparent);
    }

    /**
     * Sets the overlap to a defined factor between 0.0 and 1.0
     *
     * @param overlap it's the ImageView tag.
     * @param factor  it's the opacity factor.
     */
    private void setOverlapAlpha(ImageView overlap, float factor) {
        overlap.setAlpha(factor);
    }

    /**
     * Add image in overlap layer from the square.
     *
     * @param overlap    it's the ImageView tag.
     * @param drawableId it's the int id of drawable
     */
    private void setOverlapImage(ImageView overlap, int drawableId) {
        overlap.setImageResource(drawableId);
    }

    /**
     * Background red error.
     *
     * @param picture it's the current square
     * @param factor  it's the opacity factor
     */
    public void showBackgroundSquareError(ImageView picture, float factor) {
        setOverlapAlpha(picture, factor / 10);
        setOverlapImage(picture, R.color.red);
    }

    /**
     * Displays a visual effect based on a data unit
     *
     * @param dataUnit data unit
     * @param type     type of explosion
     */
    public void displayEffect(VisualEffectDisplayData dataUnit, ViewFunctions.TypeExplosions type) {

        int drawableId = ViewFunctions.obtainDrawableIdForVisualEffectType(dataUnit.getEffectType());

        LevelSquare square = dataUnit.getSquare();
        View view = obtainViewOfSquare(square);

        if (view != null) {

            // hide static image when showing effects if needed
            if (ViewFunctions.shouldHideStaticImageAtSquareWhenDisplayingEffects(dataUnit.getSquare())) {
                ImageView staticImage = (ImageView) view.findViewById(R.id.PictureOverlapLayer);
                staticImage.setVisibility(View.INVISIBLE);
            }

            // obtain the rotation angle for the effect
            float rotationAngle = (float) dataUnit.getRotationAngle() + ((dataUnit.useMobileOrientationDependentRotation()) ?
                    obtainMobileOrientationBaseAngleForImages(view.getContext()) :
                    (func.getOrientation() == Configuration.ORIENTATION_LANDSCAPE) ? LANDSCAPE_CORRECTION_ANGLE : 0);

            GifImageView overlap = null;

            if (type == ViewFunctions.TypeExplosions.ROBOT_NORMAL_EXPLOSION) {
                overlap = (GifImageView) view.findViewById(R.id.PictureOverlapLayerRobotNormalExplosion);
            }

            if (type == ViewFunctions.TypeExplosions.ROBOT_EXPLOSIVE_EXPLOSION) {
                overlap = (GifImageView) view.findViewById(R.id.PictureOverlapLayerRobotExplosiveExplosion);
            }

            if (overlap != null) {
                gamePlayObject.addDisplayEffectsBackground(overlap, rotationAngle, drawableId);
            }
        }
    }

    /**
     * Returns the view associated to the passed square
     *
     * @param square square
     * @return view
     */
    public View obtainViewOfSquare(LevelSquare square) {
        for (View view : viewsAndSquaresMap.keySet()) {
            if (viewsAndSquaresMap.get(view).equals(square)) {
                return view;
            }
        }
        return null;
    }

    /**
     * Displays an array of visual effect based on the passed data units
     *
     * @param dataUnits data units
     */
    public void displayEffects(ArrayList<VisualEffectDisplayData> dataUnits) {

        int currentEffect = 1;

        for (VisualEffectDisplayData dataUnit : dataUnits) {

            int drawableId = ViewFunctions.obtainDrawableIdForVisualEffectType(dataUnit.getEffectType());

            LevelSquare square = dataUnit.getSquare();
            View view = obtainViewOfSquare(square);

            if (view != null) {
                // hide static image when showing effects if needed
                if (ViewFunctions.shouldHideStaticImageAtSquareWhenDisplayingEffects(dataUnit.getSquare())) {
                    ImageView staticImage = (ImageView) view.findViewById(R.id.PictureOverlapLayer);
                    staticImage.setVisibility(View.INVISIBLE);
                }

                // obtain the rotation angle for the effect
                float rotationAngle = (float) dataUnit.getRotationAngle() + ((dataUnit.useMobileOrientationDependentRotation()) ?
                        obtainMobileOrientationBaseAngleForImages(view.getContext()) :
                        (func.getOrientation() == Configuration.ORIENTATION_LANDSCAPE) ? LANDSCAPE_CORRECTION_ANGLE : 0);

                GifImageView overlap = getOverlapEffect(view, currentEffect);
                if (overlap != null) {
                    gamePlayObject.addDisplayEffectsBackground(overlap, rotationAngle, drawableId);
                }
            }
            currentEffect++;
        }
    }

    /**
     * Get ImageGif for current display effect.
     *
     * @param view          is the current view
     * @param currentEffect is the current effect
     * @return effect
     */
    private GifImageView getOverlapEffect(View view, int currentEffect) {
        int id = func.getResourceId(ViewFunctions.OVERLAP_EFFECT_PREFIX.concat(String.valueOf(currentEffect)), "id");
        return (GifImageView) view.findViewById(id);
    }

    /**
     * Makes invisible the robot image at the passed square
     *
     * @param square square with the robot
     */
    public void makeRobotInvisibleAtSquare(LevelSquare square) {
        View view = obtainViewOfSquare(square);
        if (view != null) {
            ImageView picture = (ImageView) view.findViewById(R.id.PictureOverlapLayer);
            picture.setVisibility(View.INVISIBLE);
        }
    }
}