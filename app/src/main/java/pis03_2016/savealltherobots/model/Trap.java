package pis03_2016.savealltherobots.model;

import java.util.ArrayList;

import pis03_2016.savealltherobots.model.Orientation.EOrientationValue;


/**
 * Trap object capable of attacking robots
 */
abstract public class Trap extends PlaceableObject implements Trapable {


    /**
     * whether the trap is enabled
     */
    private boolean enabled;


    /**
     * orientation of the trap
     */
    private Orientation orientation;


    /**
     * Constructor that creates a trap with the passed orientation
     *
     * @param orientation trap orientation
     */
    public Trap(Orientation orientation) {
        super();
        this.orientation = orientation;
        this.enabled = true;
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
     * Return the orientation of the trap
     *
     * @return orientation of the trap
     */
    public Orientation getOrientation() {
        return orientation;
    }


    /**
     * Obtains and returns the attack relative positions
     *
     * @return the attack relative positions
     */
    public ArrayList<RelativePosition> obtainAttackRelativePositions() {

        ArrayList<RelativePosition> attackRelativePositions = obtainUnorientedRelativePositions();

        return adaptRelativePositionsToOrientation(attackRelativePositions);

    }

    /**
     * Obtains the set of relative positions that the trap can attack without taking orientation into account
     *
     * @return the unoriented relative positions
     */
    protected abstract ArrayList<RelativePosition> obtainUnorientedRelativePositions();


    /**
     * Adapts the relative positions of each subclass to trap orientation
     *
     * @param relativePositions relative positions
     * @return RelativePositions
     */
    private ArrayList<RelativePosition> adaptRelativePositionsToOrientation(ArrayList<RelativePosition> relativePositions) {

        if (orientation.is(EOrientationValue.NEUTRAL) || orientation.is(EOrientationValue.UP)) {
            return relativePositions;
        }

        ArrayList<RelativePosition> adaptedRelativePositions = new ArrayList<>();

        for (int i = 0; i < relativePositions.size(); i++) {
            adaptedRelativePositions.add(orientation.adaptRelativePositionToOrientation(relativePositions.get(i)));
        }

        return adaptedRelativePositions;
    }


}