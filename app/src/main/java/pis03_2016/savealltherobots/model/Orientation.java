package pis03_2016.savealltherobots.model;

/**
 * Orientation of a certain game elements
 */
public class Orientation {


    /**
     * Orientation value
     */
    private EOrientationValue value;


    /**
     * Constructor that configures orientation value
     *
     * @param value orientation value
     */
    Orientation(EOrientationValue value) {
        this.value = value;
    }

    /**
     * obtains the orientation that corresponds to a relative position representing the
     * difference in horizontal and vertical integer values between two entities in a plane
     *
     * @param differencePosition the difference relative position
     * @return the orientation object
     */
    public static Orientation obtainOrientationFromDifferenceRelativePosition(RelativePosition differencePosition) {

        Orientation orientation = new Orientation(EOrientationValue.NEUTRAL);

        // vertical-only cases
        if (differencePosition.getHorizontalCoord() == 0) {

            // attacked square is just above trap's square
            if (differencePosition.getVerticalCoord() > 0) {
                orientation.setValue(Orientation.EOrientationValue.UP);
            }

            // attacked square is just below trap's square
            else if (differencePosition.getVerticalCoord() < 0) {
                orientation.setValue(Orientation.EOrientationValue.DOWN);
            }
        }

        // horizontal-only cases
        else if (differencePosition.getVerticalCoord() == 0) {

            // attacked square is just left to trap's square
            if (differencePosition.getHorizontalCoord() < 0) {
                orientation.setValue(Orientation.EOrientationValue.LEFT);
            }

            // attacked square is just right to trap's square
            else if (differencePosition.getHorizontalCoord() > 0) {
                orientation.setValue(Orientation.EOrientationValue.RIGHT);
            }
        }


        // diagonal cases
        else {
            if (differencePosition.getHorizontalCoord() < 0) {

                if (differencePosition.getVerticalCoord() > 0)
                    orientation.setValue(Orientation.EOrientationValue.UP_LEFT);

                else if (differencePosition.getVerticalCoord() < 0)
                    orientation.setValue(Orientation.EOrientationValue.DOWN_LEFT);
            } else if (differencePosition.getHorizontalCoord() > 0) {

                if (differencePosition.getVerticalCoord() > 0)
                    orientation.setValue(Orientation.EOrientationValue.UP_RIGHT);

                else if (differencePosition.getVerticalCoord() < 0)
                    orientation.setValue(Orientation.EOrientationValue.DOWN_RIGHT);
            }
        }

        return orientation;
    }

    /**
     * Returns the orientation value
     *
     * @return the orientation value
     */
    public EOrientationValue getValue() {
        return value;
    }

    /**
     * Sets the orientation value
     *
     * @param value orientation value
     */
    public void setValue(EOrientationValue value) {
        this.value = value;
    }

    /**
     * Returns the rotation angle associated to this orientation
     *
     * @return the rotation angle
     */
    public float obtainRotationAngle() {

        switch (value) {
            case DOWN:
                return 180;

            case RIGHT:
                return 90;

            case LEFT:
                return 270;

            case UP_RIGHT:
                return 45;

            case UP_LEFT:
                return 315;

            case DOWN_RIGHT:
                return 135;

            case DOWN_LEFT:
                return 225;

            // UP or NEUTRAL
            default:
                return 0;
        }
    }

    /**
     * Returns whether the passed orientation value is the same as this orientation's value
     *
     * @param valueToCompare value to compare with this orientation
     * @return whether orientation is the same as the passed one
     */
    public boolean is(EOrientationValue valueToCompare) {
        return value == valueToCompare;
    }

    /**
     * Adapt the position to this orientation
     *
     * @param relativePosition relative position to adapt
     * @return relative position once adapted
     */
    RelativePosition adaptRelativePositionToOrientation(RelativePosition relativePosition) {
        int oldHorizontalCoord = relativePosition.getHorizontalCoord();
        int oldVerticalCoord = relativePosition.getVerticalCoord();

        switch (value) {
            case DOWN:
                relativePosition.setVerticalCoord(-oldVerticalCoord);
                break;

            case RIGHT:
                relativePosition.setHorizontalCoord(oldVerticalCoord);
                relativePosition.setVerticalCoord(-oldHorizontalCoord);
                break;

            case LEFT:
                relativePosition.setHorizontalCoord(-oldVerticalCoord);
                relativePosition.setVerticalCoord(oldHorizontalCoord);
                break;

            default:
                // non-basic orientations (such as up-left) aren't expected to be treated
                break;

        }

        return relativePosition;
    }

    /**
     * Enum set of values an orientation can have
     */
    public enum EOrientationValue {

        // basic values
        UP,
        DOWN,
        RIGHT,
        LEFT,
        NEUTRAL,

        // combined values
        UP_RIGHT,
        UP_LEFT,
        DOWN_RIGHT,
        DOWN_LEFT
    }


}