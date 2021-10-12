package pis03_2016.savealltherobots.model;

import java.util.ArrayList;

import pis03_2016.savealltherobots.controller.GamePlayController;


/**
 * All items can be unlock playing
 */
public class UnlockableItems {

    /**
     * The state of the last game
     */
    private GameState gameState;
    /**
     * Array of the rest of items to unlock during the game (traps and robots)
     */
    private Class[] unlockableArray = {Trap_LaserCannon.class, Robot_Steel.class, Trap_FlameThrower.class, Robot_Gold.class, Trap_ElectricCircle.class, Robot_Diamond.class,
            Robot_Explosive.class, Trap_QuadrupleLaserCannon.class, Robot_Deactivator_Radio.class};

    /**
     * Index to know wich item is the next one to unlock gived a level.
     */
    private int index;

    /**
     * Constructor of the class.
     * Initializes the index value.
     */
    public UnlockableItems(GameState gameState) {
        index = gameState.getCompletedLevelsAmount();
        this.gameState = gameState;
    }

    /**
     * Function that adds the next unlockable item to the game state arrays.
     */
    public void unlockNextItem() {

        index = GamePlayController.getInstance().getGameData().getGameState().getCompletedLevelsAmount() - 1;

        if (index < unlockableArray.length) {
            Class c = unlockableArray[index];

            if (Trap.class.isAssignableFrom(c)) {
                ArrayList<Class> trapsArray = gameState.getAllowedTrapClasses();
                trapsArray.add(c);
                gameState.addToUnlockedItemsList(c);
            } else if (Robot.class.isAssignableFrom(c)) {
                ArrayList<Class> robotsArray = gameState.getAllowedRobotClasses();
                robotsArray.add(c);
                gameState.addToUnlockedItemsList(c);
            }
        }
    }

    /**
     * GETTERS AND SETTERS
     *
     * @return the index
     */
    public int getIndex() {
        return index;
    }


    public Class[] getUnlockableArray() {
        return unlockableArray;
    }

}
