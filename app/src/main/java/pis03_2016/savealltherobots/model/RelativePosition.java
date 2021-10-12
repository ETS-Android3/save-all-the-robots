package pis03_2016.savealltherobots.model;


/**
 * Class that provides Positions based on abstract Placeable position
 */
public class RelativePosition {


    /**
     * relative horizontal coord
     */
    private int horizontalCoord;
    /**
     * relative vertical coord
     */
    private int verticalCoord;
    /**
     * mode of treatment of the relative position
     */
    private EMode mode;

    /**
     * Constructor
     *
     * @param horizontalCoord horizontalCoord
     * @param verticalCoord   verticalCoord
     * @param mode            mode
     */
    public RelativePosition(int horizontalCoord, int verticalCoord, EMode mode) {
        this.horizontalCoord = horizontalCoord;
        this.verticalCoord = verticalCoord;
        this.mode = mode;
    }

    /**
     * Returns the horizontal coord
     *
     * @return the horizontal coord
     */
    int getHorizontalCoord() {
        return horizontalCoord;
    }


    /**
     * Sets the horizontal coord
     *
     * @param horizontalCoord horizontalCoord
     */
    void setHorizontalCoord(int horizontalCoord) {
        this.horizontalCoord = horizontalCoord;
    }


    /**
     * Returns the vertical coord
     *
     * @return the vertical coord
     */
    int getVerticalCoord() {
        return verticalCoord;
    }

    /**
     * Sets the vertical coord
     *
     * @param verticalCoord verticalCoord
     */
    void setVerticalCoord(int verticalCoord) {
        this.verticalCoord = verticalCoord;
    }


    /**
     * Returns the mode
     *
     * @return The mode
     */
    public EMode getMode() {
        return mode;
    }


    /**
     * set of mode values of treatment of the relative position
     */
    public enum EMode {
        DEFAULT,
        CONTINUOUS_UNTIL_BLOCKED
    }
}