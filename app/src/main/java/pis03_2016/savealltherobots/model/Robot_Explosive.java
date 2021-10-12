package pis03_2016.savealltherobots.model;


import java.util.ArrayList;

/**
 * Robot that acts as a explosive trap that reaches adjacent and diagonal squares
 */
public class Robot_Explosive extends Robot implements Trapable {


    /**
     * Whether it is enabled
     */
    private boolean enabled;

    /**
     * Constructor
     */
    public Robot_Explosive() {
        super();
        // the explosive robot is destroyed even if no external trap attacks it, its own explosion destroys it
        resistance = -1;
        this.enabled = true;
    }

    /**
     * Obtains and returns the attack relative positions
     *
     * @return the attack relative positions
     */
    @Override
    public ArrayList<RelativePosition> obtainAttackRelativePositions() {
        ArrayList<RelativePosition> attackRelativePositions = new ArrayList<>();

        // the adjacent and diagonal positions are reached

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {

                if (!(i == 0 && j == 0)) {
                    attackRelativePositions.add(new RelativePosition(i, j, RelativePosition.EMode.DEFAULT));
                }
            }
        }

        return attackRelativePositions;
    }


    /**
     * Returns whether the trap is enabled
     *
     * @return whether the trap is enabled
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether the trap is enabled
     *
     * @param enabled enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    /**
     * Obtain the data telling how to display an attack effect of this trap on the passed square object
     *
     * @param attackedSquare the square being attacked
     * @param trapSquare     the square where the trap is placed
     * @return the effect display data units
     */
    @Override
    public ArrayList<VisualEffectDisplayData> obtainAttackDisplayDataAtSquare(LevelSquare attackedSquare, LevelSquare trapSquare) {

        /**
         * for the explosive robot, an explosion needs to be displayed at each attacked square
         */

        ArrayList<VisualEffectDisplayData> dataUnits = new ArrayList<>();
        dataUnits.add(new VisualEffectDisplayData(attackedSquare, VisualEffectDisplayData
                .EVisualEffectType.ROBOT_EXPLOSION, new Orientation(Orientation.EOrientationValue
                .NEUTRAL).obtainRotationAngle(), true, false));

        return dataUnits;

    }


}
