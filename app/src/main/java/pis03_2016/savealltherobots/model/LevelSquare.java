package pis03_2016.savealltherobots.model;

/**
 * Square where game elements can be placed
 */
public class LevelSquare {


    /**
     * Id of drawable name
     */
    public final int drawableId;
    private int id;
    /**
     * Square row
     */
    private int row;
    /**
     * Square column
     */
    private int col;
    /**
     * whether the square is occupied
     */
    private boolean occupied;

    /**
     * object installed in the square, null if none
     */
    private PlaceableObject object;

    /**
     * LevelSquare constructor
     *
     * @param row        is the row of square
     * @param col        is the column onf square
     * @param drawableId is the id or R image.
     */
    public LevelSquare(int id, int row, int col, int drawableId) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.drawableId = drawableId;
        this.occupied = false;
    }

    /**
     * Returns as a relative position representing horizontal and vertical number of squares to
     * move (taking sense into account, with to right and upwards being considered the positive
     * senses) to go from the start square to the end one
     *
     * @param startSquare one of the squares
     * @param endSquare   the other square
     * @return the distance relative position
     */
    public static RelativePosition obtainRelativePositionBetweenSquares(LevelSquare startSquare,
                                                                        LevelSquare endSquare) {

        return new RelativePosition(endSquare.getCol() - startSquare.getCol(), startSquare.getRow() - endSquare.getRow(), RelativePosition.EMode.DEFAULT);
    }

    /**
     * Returns the id
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets a new id
     *
     * @param id id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the row of this square
     *
     * @return the row of square.
     */
    public int getRow() {
        return row;
    }

    /**
     * Sets a new row
     *
     * @param row row
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * @return the column of square.
     */
    public int getCol() {
        return col;
    }

    /**
     * Sets new column
     *
     * @param col col
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * Returns the drawable id
     *
     * @return the drawable id
     */
    public int getDrawableId() {
        return drawableId;
    }

    /**
     * Returns whether the square is occupied
     *
     * @return whether the square is occupied
     */
    public boolean isOccupied() {
        return occupied;
    }

    /**
     * Sets whether the square is occupied
     *
     * @param occupied occupied
     */
    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    /**
     * Returns the object installed in the square, null if none
     *
     * @return object installed in the square, null if none
     */
    public PlaceableObject getObject() {
        return object;
    }

    /**
     * Sets the object installed in the square
     *
     * @param object object
     */
    public void setObject(PlaceableObject object) {
        this.object = object;
    }

    /**
     * Installs the passed object in the square, if possible
     *
     * @param object object
     * @return whether the robot was successfully installed
     */
    public boolean installObject(PlaceableObject object) {
        if (!isOccupied()) {
            setObject(object);
            setOccupied(true);
            return true;
        }
        return false;
    }

    /**
     * Uninstalls and returns the object placed in the square, if possible
     *
     * @return the uninstalled object or null if none
     */
    public PlaceableObject uninstallObject() {
        if (object != null) {
            PlaceableObject uninstalledObject = object;
            setObject(null);
            setOccupied(false);
            return uninstalledObject;
        }
        return null;
    }

    /**
     * Converts to String format
     *
     * @return string format
     */
    public String toString() {
        return "(id >> " + id + " row >> " + row + " col >> " + col + ") ";
    }


}