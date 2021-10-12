package pis03_2016.savealltherobots.model;

import java.util.ArrayList;

import pis03_2016.savealltherobots.model.RelativePosition.EMode;


/**
 * Trap attacking the square in front of it
 */
public class Trap_Fist extends Trap {


    /**
     * amount of squares the fist can reach
     */
    protected int fistSquaresScope;


    /**
     * Constructor
     *
     * @param orientation orientation
     */
    public Trap_Fist(Orientation orientation) {

        super(orientation);

        fistSquaresScope = 1;
    }

    /**
     * Obtains the set of relative positions that the trap can attack without taking orientation into account
     *
     * @return the unoriented relative positions
     */
    protected ArrayList<RelativePosition> obtainUnorientedRelativePositions() {
        ArrayList<RelativePosition> unorientedRelativePositions = new ArrayList<>();

        for (int i = 1; i <= fistSquaresScope; i++) {
            unorientedRelativePositions.add(new RelativePosition(0, i, EMode.DEFAULT));
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
         * the fist punching animation takes place is fragmented in two sequences, one for the
         * start square and the other for the attacked square, that are played synchronized
         */

        float angle = getOrientation().obtainRotationAngle();

        ArrayList<VisualEffectDisplayData> dataUnits = new ArrayList<>();

        // sequence for the trap start square
        dataUnits.add(new VisualEffectDisplayData(trapSquare, VisualEffectDisplayData
                .EVisualEffectType.FIST_0, angle, true, false));

        // sequence for the square in front of the start square
        dataUnits.add(new VisualEffectDisplayData(attackedSquare, VisualEffectDisplayData
                .EVisualEffectType.FIST_1, angle, true, false));

        return dataUnits;
    }


}