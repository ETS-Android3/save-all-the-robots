package pis03_2016.savealltherobots.model;

import java.lang.reflect.Constructor;

import java.util.ArrayList;

/**
 * inventory of a game level
 */
public class LevelInventory {

    /**
     * array of robot-representing cells that form the inventory
     */
    ArrayList<LevelInventoryCell> cells;


    /**
     * Constructor without parameters
     */
    public LevelInventory() {
        this.cells = new ArrayList<>();
    }

    /**
     * ç
     * Constructor with passed cells
     *
     * @param cells cells
     */
    public LevelInventory(ArrayList<LevelInventoryCell> cells) {
        this.cells = cells;
    }


    /**
     * Adds a cell to the inventory
     *
     * @param cell cell to add to the inventory
     */
    public void addCell(LevelInventoryCell cell) {
        cells.add(cell);
    }

    /**
     * Returns array of inventary cells
     *
     * @return cells
     */
    public ArrayList<LevelInventoryCell> getCells() {
        return this.cells;
    }

    /**
     * Returns cell from class name.
     *
     * @param Classname is a name of class
     * @return LevelInventoryCell obj.
     */
    public LevelInventoryCell getAtFromClassName(String Classname) {
        for (LevelInventoryCell cell : cells) {
            if (cell.getRobotClass().getSimpleName().equalsIgnoreCase(Classname)) {
                return cell;
            }
        }
        return null;
    }

    /**
     * Returns whether all robots have been placed or not
     *
     * @return Whether there are robots remaining in the inventory
     */
    public boolean hasRobotsRemaining() {

        for (LevelInventoryCell cell : cells) {
            if (cell.getRemainingRobotAmount() != 0) {
                return true;
            }
        }

        return false;
    }


    /**
     * Creates all the robot objects based on inventory cells data
     *
     * @return the created robots
     */
    public ArrayList<Robot> createAllRobots() {

        Class[] paramTypes = new Class[]{};
        Object[] params = new Object[]{};

        Robot robot;

        ArrayList<Robot> robots = new ArrayList<>();

        for (LevelInventoryCell cell : cells) {
            for (int i = 0; i < cell.getTotalRobotAmount(); i++) {

                // get a reflection constructor to create an object of the class
                try {
                    Constructor<?> constructor = ( (Class<?>) cell.getRobotClass()).getConstructor(paramTypes);
                    robot = (Robot) constructor.newInstance(params);

                    robots.add(robot);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return robots;
    }

    /**
     * Returns whether the inventory has explosive robots
     *
     * @return whether the inventory has explosive robots
     */
    public boolean areThereExplosiveRobots() {

        for (LevelInventoryCell cell : cells) {
            if (cell.getRobotClass() == Robot_Explosive.class) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the total robot number in the inventory when it is full
     *
     * @return the total robot number
     */
    public int obtainTotalRobotNumber() {
        int robotNumber = 0;

        for (LevelInventoryCell cell : cells) {
            robotNumber += cell.getTotalRobotAmount();
        }

        return robotNumber;
    }

    /**
     * Returns the total robot number not placed in the inventory when it is full
     *
     * @return the total robot number
     */
    public int obtainTotalRobotNumberNotPlaced() {
        int robotNumberNotPlaced = 0;

        for (LevelInventoryCell cell : cells) {
            robotNumberNotPlaced += cell.getRemainingRobotAmount();
        }

        return robotNumberNotPlaced;
    }


    /**
     * Obtains the cell with the passed robot class, or null if non-existent
     *
     * @param robotClass class to look for
     * @return the cell with the passed robot class, or null if non-existent
     */
    public LevelInventoryCell obtainCellWithClass(Class robotClass) {
        for (LevelInventoryCell cell : cells) {
            if (cell.getRobotClass().equals(robotClass)) {
                return cell;
            }
        }
        return null;
    }

}
