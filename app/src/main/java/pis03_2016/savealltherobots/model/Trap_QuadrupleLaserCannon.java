package pis03_2016.savealltherobots.model;

import java.util.ArrayList;

/**
 * Trap which is a combination of four laser cannons all-in-one
 */
public class Trap_QuadrupleLaserCannon extends Trap_LaserCannon {

    /**
     * Constructor that creates trap orientation based on passed orientation value
     *
     * @param orientation orientation
     */
    public Trap_QuadrupleLaserCannon(Orientation orientation) {
        super(orientation);
    }

    /**
     * Obtains the set of relative positions that the trap can attack without taking orientation into account
     *
     * @return the unoriented relative positions
     */
    @Override
    protected ArrayList<RelativePosition> obtainUnorientedRelativePositions() {
        ArrayList<RelativePosition> unorientedRelativePositions = new ArrayList<>();
        unorientedRelativePositions.add(new RelativePosition(0, 1, RelativePosition.EMode.CONTINUOUS_UNTIL_BLOCKED));
        unorientedRelativePositions.add(new RelativePosition(0, -1, RelativePosition.EMode.CONTINUOUS_UNTIL_BLOCKED));
        unorientedRelativePositions.add(new RelativePosition(1, 0, RelativePosition.EMode.CONTINUOUS_UNTIL_BLOCKED));
        unorientedRelativePositions.add(new RelativePosition(-1, 0, RelativePosition.EMode.CONTINUOUS_UNTIL_BLOCKED));
        return unorientedRelativePositions;
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
         *
         * The quadruple laser cannon uses the same effects like the laser cannon,
         * with the distinction that the orientation depends on which of the four
         * cannons is shooting
         */

        VisualEffectDisplayData.EVisualEffectType effectType = VisualEffectDisplayData.EVisualEffectType.LASER_BEAM;

        if (attackedSquare.isOccupied()) {
            effectType = VisualEffectDisplayData.EVisualEffectType.LASER_ENDPOINT;
        }

        // orientation depends on which of the four cannons is shooting
        Orientation orientation = new Orientation(Orientation.EOrientationValue.NEUTRAL);
        if (attackedSquare.getRow() < trapSquare.getRow()) {
            orientation.setValue(Orientation.EOrientationValue.UP);
        } else if (attackedSquare.getRow() > trapSquare.getRow()) {
            orientation.setValue(Orientation.EOrientationValue.DOWN);
        } else if (attackedSquare.getCol() > trapSquare.getCol()) {
            orientation.setValue(Orientation.EOrientationValue.RIGHT);
        } else if (attackedSquare.getCol() < trapSquare.getCol()) {
            orientation.setValue(Orientation.EOrientationValue.LEFT);
        }

        ArrayList<VisualEffectDisplayData> dataUnits = new ArrayList<>();
        dataUnits.add(new VisualEffectDisplayData(attackedSquare, effectType, orientation
                .obtainRotationAngle(), true, true));

        return dataUnits;
    }
}
