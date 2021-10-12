package pis03_2016.savealltherobots.model;


import java.util.ArrayList;

/**
 * Trap that can reach all squares around it
 */
public class Trap_ElectricCircle extends Trap {


    /**
     * Constructor that creates trap orientation based on passed orientation value
     *
     * @param orientation orientation
     */
    public Trap_ElectricCircle(Orientation orientation) {
        super(orientation);
    }

    /**
     * Returns the deactivation relative positions
     *
     * @return the deactivation relative positions
     */
    @Override
    protected ArrayList<RelativePosition> obtainUnorientedRelativePositions() {
        ArrayList<RelativePosition> unorientedRelativePositions = new ArrayList<>();

        // the adjacent and diagonal positions are reached

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {

                if (!(i == 0 && j == 0)) {
                    unorientedRelativePositions.add(new RelativePosition(i, j, RelativePosition.EMode.DEFAULT));
                }
            }
        }

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
         * for the electric circle, a spike needs to be displayed at the trap's central square
         * for each attacked square that is not occupied by a trap or a block, with the corresponding angle so the
         * flame image occupies a big part of the attacked square
         */

        if (attackedSquare.isOccupied() && (attackedSquare.getObject() instanceof Trap ||
                attackedSquare.getObject() instanceof Block)) {
            // no spike should be displayed in squares with a trap or a block
            return null;
        }


        // calculate orientation for display effect depending on relative positioning of the
        // attacking and the attacked square
        RelativePosition differencePosition = LevelSquare.obtainRelativePositionBetweenSquares(trapSquare, attackedSquare);
        Orientation orientation = Orientation.obtainOrientationFromDifferenceRelativePosition
                (differencePosition);

        ArrayList<VisualEffectDisplayData> dataUnits = new ArrayList<>();
        dataUnits.add(new VisualEffectDisplayData(attackedSquare, VisualEffectDisplayData.EVisualEffectType.ELECTRIC_BEAM, orientation
                .obtainRotationAngle(), false, true));

        return dataUnits;
    }

}
