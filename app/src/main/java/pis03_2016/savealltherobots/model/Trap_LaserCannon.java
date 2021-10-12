package pis03_2016.savealltherobots.model;


import java.util.ArrayList;

/**
 * Cannon trap that throws a beam that goes forward until something blocks it
 */
public class Trap_LaserCannon extends Trap {


    /**
     * Constructor that creates trap orientation based on passed orientation value
     *
     * @param orientation orientation
     */
    public Trap_LaserCannon(Orientation orientation) {

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

        // beam is continuous until blocked by something

        unorientedRelativePositions.add(new RelativePosition(0, 1, RelativePosition.EMode.CONTINUOUS_UNTIL_BLOCKED));

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
         * for the laser cannon, the effect on a attacked square is the laser beam
         * unless this is blocked by something, in which case it is the laser endpoint
         */

        VisualEffectDisplayData.EVisualEffectType effectType = VisualEffectDisplayData.EVisualEffectType.LASER_BEAM;

        if (attackedSquare.isOccupied()) {
            effectType = VisualEffectDisplayData.EVisualEffectType.LASER_ENDPOINT;
        }

        ArrayList<VisualEffectDisplayData> dataUnits = new ArrayList<>();
        dataUnits.add(new VisualEffectDisplayData(attackedSquare, effectType, getOrientation()
                .obtainRotationAngle(), true, true));

        return dataUnits;
    }


}
