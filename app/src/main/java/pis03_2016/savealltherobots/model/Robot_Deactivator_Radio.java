package pis03_2016.savealltherobots.model;

import java.util.ArrayList;

/**
 * Robot that can deactivate the traps around it
 */
public class Robot_Deactivator_Radio extends Robot_Deactivator {


    /**
     * Constructor
     */
    public Robot_Deactivator_Radio() {
        super();
    }

    /**
     * Returns the deactivation relative positions
     *
     * @return the deactivation relative positions
     */
    @Override
    protected ArrayList<RelativePosition> obtainDeactivationRelativePositions() {

        ArrayList<RelativePosition> deactivationRelativePositions = new ArrayList<>();

        // the adjacent and diagonal positions are reached for deactivation by radio robot

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {

                if (!(i == 0 && j == 0)) {
                    deactivationRelativePositions.add(new RelativePosition(i, j, RelativePosition.EMode.DEFAULT));
                }
            }
        }

        return deactivationRelativePositions;
    }

}
