package pis03_2016.savealltherobots.model;

/**
 * level inventory cell, which has a robot class, a total amount of it and a remaining amount
 */
public class LevelInventoryCell {

    /**
     * robot class this cell provides
     */
    private Class robotClass;

    /**
     * total quantity of robots this cell provides
     */
    private int totalRobotAmount;

    /**
     * remaining quantity of robots left in the cell
     */
    private int remainingRobotAmount;

    /**
     * Constructor
     *
     * @param robotClass       is a class robot
     * @param totalRobotAmount is robot amount
     */
    public LevelInventoryCell(Class robotClass, int totalRobotAmount) {
        this.robotClass = robotClass;
        this.totalRobotAmount = totalRobotAmount;
        // at the beginning, there are as many robots remaining as the total amount
        this.remainingRobotAmount = totalRobotAmount;
    }

    /**
     * Returns the robot class
     *
     * @return the robot class
     */
    public Class getRobotClass() {
        return robotClass;
    }

    /**
     * Returns the total robot amount
     *
     * @return the total robot amount
     */
    public int getTotalRobotAmount() {
        return totalRobotAmount;
    }

    /**
     * Decrements the total robot amount
     */
    public void dcrTotalRobotAmount() {
        totalRobotAmount--;
        if (remainingRobotAmount > totalRobotAmount) {
            dcrRemainingRobotAmount();
        }
    }


    /**
     * Returns the remaining robot amount
     *
     * @return the remaining robot amount
     */
    public int getRemainingRobotAmount() {
        return remainingRobotAmount;
    }

    /**
     * Decrements the remaining robot amount
     */
    public void dcrRemainingRobotAmount() {
        this.remainingRobotAmount--;
    }

    /**
     * Increments the remaining robot amount
     */
    public void inrRemainingRobotAmount() {
        this.remainingRobotAmount++;
    }

    /**
     * Indicates whether robots are
     */
    public boolean isStillSomeRobots() {
        return remainingRobotAmount > 0;
    }

}
