package pis03_2016.savealltherobots.model;

import java.util.ArrayList;


/**
 * Interface for objects that can act as traps
 */
public interface Trapable {


    /**
     * Returns whether the trap is enabled
     *
     * @return whether the trap is enabled
     */
    boolean isEnabled();

    /**
     * Sets whether the trap is enabled
     *
     * @param enabled enabled
     */
    void setEnabled(boolean enabled);


    /**
     * Obtain the data telling how to display an attack effect of this trap on the passed square object
     *
     * @param attackedSquare the square being attacked
     * @param trapSquare     the square where the trap is placed
     * @return the effect display data units
     */
    ArrayList<VisualEffectDisplayData> obtainAttackDisplayDataAtSquare(LevelSquare attackedSquare, LevelSquare trapSquare);


    /**
     * Obtains and returns the attack relative positions
     *
     * @return the attack relative positions
     */
    ArrayList<RelativePosition> obtainAttackRelativePositions();


}
