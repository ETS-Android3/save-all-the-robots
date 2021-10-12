package pis03_2016.savealltherobots.model;

import java.util.ArrayList;

/**
 * Robot capable of deactivating traps
 */
public abstract class Robot_Deactivator extends Robot {


    /**
     * relative positions of the squares whose traps should deactivate
     */
    protected ArrayList<RelativePosition> deactivationRelativePositions;

    public Robot_Deactivator() {
        super();
        deactivationRelativePositions = obtainDeactivationRelativePositions();
    }

    /**
     * Returns the deactivation relative positions
     *
     * @return the deactivation relative positions
     */
    public ArrayList<RelativePosition> getDeactivationRelativePositions() {
        return deactivationRelativePositions;
    }

    /**
     * Obtain the relative positions of squares whose traps should deactivate
     *
     * @return the relative positions
     */
    protected abstract ArrayList<RelativePosition> obtainDeactivationRelativePositions();

}
