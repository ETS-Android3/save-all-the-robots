package pis03_2016.savealltherobots.model;

/**
 * Set of the necessary data to display an image corresponding to a visual effect over a square in the grid
 */
public class VisualEffectDisplayData {

    /**
     * whether the effect sequence should loop or be played only once
     */
    private final boolean bShouldLoop;
    /**
     * level square in which the attack is displayed
     */
    private LevelSquare square;


    /**
     * type of trap effect
     */
    private EVisualEffectType effectType;

    /**
     * rotation angle with which to display the effect image
     */
    private double rotationAngle;


    /**
     * whether the effect rotation should depend on mobile orientation
     */
    private boolean bUseMobileOrientationDependentRotation;

    /**
     * Constructor
     *
     * @param square                                 the square
     * @param effectType                             the effect type
     * @param rotationAngle                          the rotation angle
     * @param bUseMobileOrientationDependentRotation whether to use mobile orientation dependent
     *                                               rotation
     * @param bShouldLoop                            whether the sequence should loop
     */
    public VisualEffectDisplayData(LevelSquare square, EVisualEffectType effectType, double
            rotationAngle, boolean bUseMobileOrientationDependentRotation, boolean bShouldLoop) {
        this.square = square;
        this.effectType = effectType;
        this.rotationAngle = rotationAngle;
        this.bUseMobileOrientationDependentRotation = bUseMobileOrientationDependentRotation;
        this.bShouldLoop = bShouldLoop;
    }

    /**
     * Returns the square
     *
     * @return the square
     */
    public LevelSquare getSquare() {
        return square;
    }

    /**
     * Returns the effect type
     *
     * @return the effect type
     */
    public EVisualEffectType getEffectType() {
        return effectType;
    }

    /**
     * Returns the rotation angle
     *
     * @return the rotation angle
     */
    public double getRotationAngle() {
        return rotationAngle;
    }


    /**
     * Returns whether to use mobile orientation dependent rotation
     *
     * @return whether to use mobile orientation dependent rotation
     */
    public boolean useMobileOrientationDependentRotation() {
        return bUseMobileOrientationDependentRotation;
    }

    /**
     * Returns whether the effect sequence should loop
     *
     * @return whether the effect sequence should loop
     */
    public boolean shouldLoop() {
        return bShouldLoop;
    }


    /**
     * set of values representing all possible visual effects related to placeable objects
     */
    public enum EVisualEffectType {

        // electric circle
        ELECTRIC_BEAM,

        // fist
        FIST_0,
        FIST_1,

        // flame-thrower
        FLAME,

        // laser cannon
        LASER_ENDPOINT,
        LASER_BEAM,

        // robot explosion (robot dying / explosive robot attack)
        ROBOT_EXPLOSION,

    }


}

