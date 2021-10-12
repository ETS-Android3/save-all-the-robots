package pis03_2016.savealltherobots.model;


import java.util.ArrayList;

/**
 * Trap that attacks its adjacent squares, without taking into account the diagonal ones
 */
public class Trap_FlameThrower extends Trap {


    /**
     * Constructor that creates trap orientation based on passed orientation value
     *
     * @param orientation orientation
     */
    public Trap_FlameThrower(Orientation orientation) {
        super(orientation);
    }

    @Override
    protected ArrayList<RelativePosition> obtainUnorientedRelativePositions() {
        ArrayList<RelativePosition> unorientedRelativePositions = new ArrayList<>();
        unorientedRelativePositions.add(new RelativePosition(0, 1, RelativePosition.EMode.DEFAULT));
        unorientedRelativePositions.add(new RelativePosition(0, -1, RelativePosition.EMode.DEFAULT));
        unorientedRelativePositions.add(new RelativePosition(1, 0, RelativePosition.EMode.DEFAULT));
        unorientedRelativePositions.add(new RelativePosition(-1, 0, RelativePosition.EMode.DEFAULT));
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
         * for the flame-thrower, a flame needs to be displayed at the trap's central square
         * for each attacked square that is not occupied by a trap or a block, with the corresponding angle so the
         * flame image occupies a big part of the attacked square
         */

        if (attackedSquare.isOccupied() && (attackedSquare.getObject() instanceof Trap ||
                attackedSquare.getObject() instanceof Block)) {
            // no flame should be displayed in squares with a trap or a block
            return null;
        }

        // calculate orientation for display effect depending on relative positioning of the
        // attacking and the attacked square
        RelativePosition differencePosition = LevelSquare.obtainRelativePositionBetweenSquares(trapSquare, attackedSquare);
        Orientation orientation = Orientation.obtainOrientationFromDifferenceRelativePosition
                (differencePosition);


        ArrayList<VisualEffectDisplayData> dataUnits = new ArrayList<>();
        dataUnits.add(new VisualEffectDisplayData(attackedSquare, VisualEffectDisplayData
                .EVisualEffectType.FLAME, orientation.obtainRotationAngle(), false, true));

        return dataUnits;
    }


}
