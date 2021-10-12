package pis03_2016.savealltherobots.model;


import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

import pis03_2016.savealltherobots.R;
import pis03_2016.savealltherobots.controller.GridAdapter;

/**
 * Grid of squares where game elements are placed
 */
public class LevelGrid {

    /**
     * amount of squares per row
     */
    public static final int NUM_ROWS = 8;

    /**
     * amount of squares per column
     */
    public static final int NUM_COLS = 6;

    /**
     * Grid size
     */
    public static final int GRID_SIZE = NUM_ROWS * NUM_COLS;

    /**
     * array of installed robots
     */
    private ArrayList<Robot> robots;

    /**
     * matrix of squares forming the grid
     */
    private ArrayList<ArrayList<LevelSquare>> squares;

    /**
     * list of squares (basically the same as the matrix, but expressed as a single row of elements)
     */
    private List<LevelSquare> squareList;

    /**
     * hash map that links for every square the list of trap-like objects that reach them
     */

    private HashMap<LevelSquare, ArrayList<Trapable>> squaresTrapMap;

    /**
     * ArrayList that stores the squares with a robot placed.
     */
    private ArrayList<LevelSquare> squaresWithRobot;

    /**
     * ArrayList that stores the squares with a trap-like object placed
     */
    private ArrayList<LevelSquare> squaresWithTrapable;


    /**
     * Squares with exploding robots
     */
    private ArrayList<LevelSquare> explodingRobotsSquares;


    /**
     * Constructor without parameters
     */
    public LevelGrid() {
        squaresWithRobot = new ArrayList<>();
        robots = new ArrayList<>();
        squaresWithTrapable = new ArrayList<>();
        squaresTrapMap = new HashMap<>();
        constructSquares();
    }

    /**
     * Returns the squares with a robot
     *
     * @return the squares with a robot
     */
    public ArrayList<LevelSquare> getSquaresWithRobots() {
        return squaresWithRobot;
    }

    /**
     * Returns the squares with exploding robots
     *
     * @return the squares with exploding robots
     */
    public ArrayList<LevelSquare> getExplodingRobotsSquares() {

        if (explodingRobotsSquares == null || explodingRobotsSquares.size() == 0) {
            explodingRobotsSquares = obtainExplodingRobotsSquares();
        }

        return explodingRobotsSquares;
    }

    /**
     * Empties the squares-traps map
     */
    public void emptySquaresTrapMap() {
        squaresTrapMap.clear();
    }

    /**
     * Grid squares sub-constructor.
     **/
    private void constructSquares() {
        this.squareList = new ArrayList<>(NUM_ROWS * NUM_COLS);
        this.squares = new ArrayList<>(NUM_ROWS);
        int i = 0;
        for (int row = 0; row < NUM_ROWS; row++) {
            squares.add(new ArrayList<LevelSquare>(NUM_COLS));
            for (int col = 0; col < NUM_COLS; col++) {
                LevelSquare square = new LevelSquare(i, row, col, R.drawable.level_square);
                squares.get(row).add(col, square);
                this.squareList.add(square);
                i++;
            }
        }
    }


    /**
     * Returns the robots
     *
     * @return the robots
     */
    public ArrayList<Robot> getRobots() {
        return robots;
    }

    /**
     * Returs the list of squares.
     *
     * @return arraylist
     */
    public ArrayList<LevelSquare> getSquares() {
        return (ArrayList<LevelSquare>) squareList;
    }


    /**
     * Return the LevelSquare object from row and col position.
     *
     * @param row is the row of square
     * @param col is the col of square
     * @return the LevelSquare object
     */
    public LevelSquare getAt(int row, int col) {

        if (row < squares.size() && col < squares.get(0).size() && row >= 0 && col >= 0) {
            return squares.get(row).get(col);
        }

        return null;
    }

    /**
     * Return the LevelSquare object from index of list position.
     *
     * @param pos is the position of square in array.
     * @return the LevelSquare object
     */
    public LevelSquare getAt(int pos) {
        return squareList.get(pos);
    }


    /**
     * Returns a square based on another square and a relative position
     *
     * @param square           the base square
     * @param relativePosition the relative position
     * @return the square if found or null otherwise
     */
    public LevelSquare obtainSquareFromRelativePosition(LevelSquare square, RelativePosition
            relativePosition) {

        int relativeRow = -relativePosition.getVerticalCoord();
        int relativeCol = relativePosition.getHorizontalCoord();

        return getAt(square.getRow() + relativeRow, square.getCol() + relativeCol);
    }


    /**
     * Links the passed trap-like object to the passed squares it reaches in the map
     *
     * @param squares  the list of squares
     * @param trapable the trap-like object object
     */
    public void addTrapableToSquaresMap(ArrayList<LevelSquare> squares, Trapable trapable) {
        for (LevelSquare square : squares) {

            if (squaresTrapMap.containsKey(square)) {
                squaresTrapMap.get(square).add(trapable);
            } else {
                ArrayList<Trapable> newTrapables = new ArrayList<>();
                newTrapables.add(trapable);
                squaresTrapMap.put(square, newTrapables);
                //Log.i("MAP", String.valueOf(square.getCol())+" "+String.valueOf(square.getRow()) );
            }
        }
    }

    /**
     * Installs the passed object in the passed square position
     *
     * @param obj      object to install
     * @param position position in the array of squares
     */
    public void installObjectAt(PlaceableObject obj, int position) {
        installObjectAt(obj, getAt(position));
    }

    /**
     * Installs the passed object in the passed square
     *
     * @param obj    object to install
     * @param square square where to install
     */
    public void installObjectAt(PlaceableObject obj, LevelSquare square) {
        square.installObject(obj);

        // if the object to install is a robot, add it to the robot array
        if (obj instanceof Robot) {

            robots.add((Robot) obj);
            squaresWithRobot.add(square);

            // if the robot is a deactivation one, disable the traps it reaches
            if (obj instanceof Robot_Deactivator) {
                deactivateTrapsWith(square);
            }

            // if the robot is an explosive one, check if it should be deactivated
            if (obj instanceof Robot_Explosive) {
                for (LevelSquare surroundingSquare : obtainSquaresSurroundingSquare(square)) {
                    if (surroundingSquare.isOccupied() && surroundingSquare.getObject() instanceof Robot_Deactivator) {
                        deactivateTrapsWith(surroundingSquare);
                    }
                }
            }
        }

        // if the object is a trap-like one, register it in the array of squares with that kind
        // of objects
        if (obj instanceof Trapable) {
            squaresWithTrapable.add(square);
        }
    }


    /**
     * Returns the squares surrounding a given square
     *
     * @param square square to check
     * @return the surrounding squares
     */
    private ArrayList<LevelSquare> obtainSquaresSurroundingSquare(LevelSquare square) {

        ArrayList<LevelSquare> surroundingSquares = new ArrayList<>();

        int row = square.getRow();
        int col = square.getCol();

        for (int relativeRow = -1; relativeRow <= 1; relativeRow++) {
            for (int relativeCol = -1; relativeCol <= 1; relativeCol++) {
                if (!(relativeRow == 0 && relativeCol == 0)) {
                    LevelSquare surroundingSquare = getAt(row + relativeRow, col + relativeCol);
                    if (surroundingSquare != null) {
                        surroundingSquares.add(surroundingSquare);
                    }
                }
            }
        }
        return surroundingSquares;
    }


    /**
     * Desactivate traps with deactivation robot at the passed trap
     *
     * @param square square of the deactivation robot
     */
    private void deactivateTrapsWith(LevelSquare square) {

        Robot_Deactivator deactivationRobot = (Robot_Deactivator) square.getObject();

        for (RelativePosition relativePosition : deactivationRobot.getDeactivationRelativePositions()) {

            LevelSquare affectedSquare = obtainSquareFromRelativePosition(square, relativePosition);

            // if the found square where the deactivation can affect has as trap-like object, it gets deactivated
            if (affectedSquare != null && affectedSquare.isOccupied() && affectedSquare.getObject() instanceof Trapable) {
                ((Trapable) affectedSquare.getObject()).setEnabled(false);

                // use the adapter to produce a visual change
                GridAdapter.showTrapAtSquareAsDeactivated(affectedSquare);
            }
        }
    }


    /**
     * Reactivate traps with deactivation robot just uninstalled from the passed trap
     *
     * @param square            square of the deactivation robot
     * @param deactivationRobot deactivation robot being uninstalled
     */
    private void reactivateTrapsWith(LevelSquare square, Robot_Deactivator deactivationRobot) {

        for (RelativePosition relativePosition : deactivationRobot.getDeactivationRelativePositions()) {

            LevelSquare affectedSquare = obtainSquareFromRelativePosition(square, relativePosition);

            // if the found square where the deactivation can affect has as trap-like object, it gets deactivated
            if (affectedSquare != null && affectedSquare.isOccupied() && affectedSquare.getObject() instanceof Trapable) {
                ((Trapable) affectedSquare.getObject()).setEnabled(true);

                // use the adapter to produce a visual change
                GridAdapter.showTrapAtSquareAsReactivated(affectedSquare);
            }
        }
    }


    /**
     * Uninstalls the object of the passed square and returns it
     *
     * @param square square to take the object from
     * @return the uninstalled object, or null if non-existent
     */
    public PlaceableObject uninstallObjectAt(LevelSquare square) {

        PlaceableObject obj = square.uninstallObject();

        // if the object to uninstall is a robot, remove it from the robot array, and the square
        // from squares with robot array
        if (obj instanceof Robot) {
            robots.remove(obj);
            squaresWithRobot.remove(square);

            // disable deactivation if uninstalling a deactivation robot
            if (obj instanceof Robot_Deactivator) {
                reactivateTrapsWith(square, (Robot_Deactivator) obj);
            }
        }

        if (obj instanceof Trapable) {
            squaresWithTrapable.remove(square);
        }

        return obj;
    }


    /**
     * Removes a square from the array of squares that contains a robot.
     *
     * @param position position
     */
    public void removePositionFromSquaresWithRobot(int position) {
        squaresWithRobot.remove(getAt(position));
    }


    /**
     * Given a square that contains a trap-like object, registers which squares of the grid are
     * attacked by it
     *
     * @param square square of an attacking trap
     */
    private void registerSquaresAttackedByTrapOfSquare(LevelSquare square) {

        ArrayList<RelativePosition> relativePositions = ((Trapable) square.getObject())
                .obtainAttackRelativePositions();
        ArrayList<LevelSquare> squaresAttackedByTrap = new ArrayList<>();

        for (RelativePosition relativePosition : relativePositions) {


            LevelSquare attackedSquare = obtainSquareFromRelativePosition(square,
                    relativePosition);

            //When an absolute position should be out of the grid, it is Null
            if (attackedSquare != null) {

                // proceed differently depending on relative position's working mode
                switch (relativePosition.getMode()) {

                    // for normal traps, just take into account the found attack square
                    case DEFAULT:
                        squaresAttackedByTrap.add(attackedSquare);
                        break;

                    // for continuous until blocked traps, squares are attacked in a row in
                    // forward direction until a square has something that blocks the attack
                    case CONTINUOUS_UNTIL_BLOCKED:

                        boolean isBlocked = false;

                        while (!isBlocked && attackedSquare != null) {
                            squaresAttackedByTrap.add(attackedSquare);

                            // a square is consider to be blocking a continuous trap attack if it
                            // has an object placed on it
                            isBlocked = attackedSquare.getObject() != null;

                            // next square is found
                            attackedSquare = obtainSquareFromRelativePosition(attackedSquare,
                                    relativePosition);
                        }

                        break;

                    default:
                        break;
                }
            }
        }

        addTrapableToSquaresMap(squaresAttackedByTrap, (Trapable) square.getObject());
    }

    /**
     * Establishes which traps attack each square
     */
    public void establishTrapsAttackingEachSquare() {

        emptySquaresTrapMap();

        for (LevelSquare square : squaresWithTrapable) {
            registerSquaresAttackedByTrapOfSquare(square);
        }
    }

    /**
     * Returns whether all the robots can survive trap attacks
     *
     * @return Whether all robots survive
     */
    public boolean canAllRobotsSurvive() {

        // Check every square with a robot
        for (int i = 0; i < squaresWithRobot.size(); i++) {

            if (squaresTrapMap.containsKey(squaresWithRobot.get(i))) {

                LevelSquare square = squaresWithRobot.get(i);

                if (!canRobotOfSquareSurvive(square)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Obtains and returns the squares of the robots that can't survive so explode
     *
     * @return the exploding robots' squares
     */
    public ArrayList<LevelSquare> obtainExplodingRobotsSquares() {

        ArrayList<LevelSquare> explodingRobotsSquares = new ArrayList<>();

        for (LevelSquare square : squaresWithRobot) {
            if (squaresTrapMap.containsKey(square)) {

                // check out how many of the square trap-like objects are actually enabled
                int enabledTrapablesAmount = obtainNumberOfTrapsAttackingSquare(square);

                // if there are enabled traps attacking
                if (enabledTrapablesAmount > 0) {
                    // if robot cannot resist the amount of traps, prepare to show its explosion
                    if (!((Robot) square.getObject()).canResistTrapAmount(enabledTrapablesAmount)) {
                        explodingRobotsSquares.add(square);
                    }
                }
            }
        }

        return explodingRobotsSquares;
    }

    /**
     * Obtains all traps display data units usable to show their attacks effects
     *
     * @return the trap display data units
     */
    public ArrayList<VisualEffectDisplayData> obtainTrapsAttackDisplayUnits() {

        ArrayList<VisualEffectDisplayData> attackDisplayDataUnits = new ArrayList<>();

        for (LevelSquare square : squaresTrapMap.keySet()) {

            for (Trapable trapable : squaresTrapMap.get(square)) {

                if (trapable.isEnabled()) {
                    ArrayList<VisualEffectDisplayData> trapableDataUnits = trapable
                            .obtainAttackDisplayDataAtSquare
                                    (square, obtainSquareOfTrapable(trapable));

                    if (trapableDataUnits != null && trapableDataUnits.size() > 0) {
                        attackDisplayDataUnits.addAll(trapableDataUnits);
                    }
                }

            }
        }

        return attackDisplayDataUnits;

    }

    /**
     * Returns the square where the passed trap-like object is placed
     *
     * @param trapable the trap-like object
     * @return the square where the object is placed
     */
    private LevelSquare obtainSquareOfTrapable(Trapable trapable) {
        for (LevelSquare square : squaresWithTrapable) {
            if (square.getObject() != null && square.getObject().equals(trapable)) {
                return square;
            }
        }

        return null;
    }

    /**
     * Get any robot placeabled.
     *
     * @return bool.
     */
    public boolean isAnyRobotPlaced() {
        for (LevelSquare obj : squareList) {
            if (obj.isOccupied() && obj.getObject() instanceof Robot) {
                return true;
            }
        }
        return false;
    }


    /**
     * Returns the display data units for the explosive robots exploding at their own squares
     *
     * @return VisualEffectDisplayData array
     */
    public ArrayList<VisualEffectDisplayData> obtainExplosiveRobotsSelfExplosionDisplayUnits() {
        ArrayList<VisualEffectDisplayData> dataUnits = new ArrayList<>();

        for (LevelSquare square : squaresWithRobot) {
            if (square.getObject() instanceof Robot_Explosive) {
                if (((Robot_Explosive) square.getObject()).isEnabled() || obtainNumberOfTrapsAttackingSquare(square) > 0) {
                    dataUnits.add(new VisualEffectDisplayData(square, VisualEffectDisplayData.EVisualEffectType.ROBOT_EXPLOSION, 0, true, false));
                }
            }
        }

        return dataUnits;
    }

    /**
     * Returns all the squares that are not occupied
     *
     * @return all the squares that are not occupied
     */
    public ArrayList<LevelSquare> obtainAllNonOccupiedSquares() {

        ArrayList<LevelSquare> nonOccupiedSquares = new ArrayList<>();

        for (LevelSquare square : squareList) {
            if (!square.isOccupied()) {
                nonOccupiedSquares.add(square);
            }
        }

        return nonOccupiedSquares;
    }


    /**
     * Returns whether the robot installed at the passed square can survive there
     *
     * @param square level square to check
     * @return whether the square robot can survive
     */
    public boolean canRobotOfSquareSurvive(LevelSquare square) {

        // check out how many of the square trap-like objects are actually enabled
        int enabledTrapablesAmount = obtainNumberOfTrapsAttackingSquare(square);

        // consider that the robot survives if it's explosive or can resist the attacks in its trap
        return (square.getObject() instanceof Robot_Explosive) || ((Robot) square.getObject()).canResistTrapAmount(enabledTrapablesAmount);
    }


    /**
     * Returns the squares that are not attacked at all and that are still non-occupied
     *
     * @return the squares that are not attacked at all and that are still non-occupied
     */
    public ArrayList<LevelSquare> obtainNonAttackedEmptySquares() {

        ArrayList<LevelSquare> safeFreeSquares = new ArrayList<>();

        for (LevelSquare square : squareList) {

            if (!square.isOccupied() && obtainNumberOfTrapsAttackingSquare(square) == 0) {
                safeFreeSquares.add(square);
            }
        }

        return safeFreeSquares;
    }


    /**
     * Returns the trap-like objects attacking the passed square
     *
     * @param square square to check
     * @return the traps attacking the square
     */
    public ArrayList<Trapable> obtainTrapsAttackingSquare(LevelSquare square) {

        ArrayList<Trapable> trapables = squaresTrapMap.get(square);
        ArrayList<Trapable> attackingTrapables = new ArrayList<>();

        if (trapables != null) {
            for (Trapable trapable : trapables) {
                if (trapable.isEnabled()) {
                    attackingTrapables.add(trapable);
                }
            }
        }
        return attackingTrapables;
    }

    /**
     * Returns the amount of enabled traps attacking the passed square
     *
     * @param square square to check
     * @return the amount of attacking traps
     */
    private int obtainNumberOfTrapsAttackingSquare(LevelSquare square) {

        // Note: this method is not just obtaining the size of the method that gets
        // the array of traps attacking a square because it involves creating arrays
        // to return them while here we just need the amount and this method is called
        // so many times that it would not be so efficient if it had to create arrays
        // each time

        ArrayList<Trapable> trapables = squaresTrapMap.get(square);

        // check out how many of the square trap-like objects are actually enabled
        int enabledTrapablesAmount = 0;

        if (trapables != null) {
            for (Trapable trapable : trapables) {
                if (trapable.isEnabled()) {
                    enabledTrapablesAmount++;
                }
            }
        }

        return enabledTrapablesAmount;
    }


    /**
     * Randomly find and return a non-occupied square
     *
     * @return the found square, or null if none
     */
    public LevelSquare randomlyObtainNonOccupiedSquare() {

        LevelSquare square;
        boolean obtainedANonOccupiedSquare;

        // randomly find a non-occupied square
        int index = LevelGenerator.obtainRandomIntBetween(0, squareList.size() - 1);
        int checkedSquaresAmount = 0;
        do {

            square = getAt(index);
            index++;
            checkedSquaresAmount++;

            if (index >= squareList.size()) {
                index = 0;
            }

            obtainedANonOccupiedSquare = square != null && !square.isOccupied();

        } while (!obtainedANonOccupiedSquare && checkedSquaresAmount <= squareList.size());

        return square;
    }


    /**
     * Returns all squares whose traps cannot attack any squares
     *
     * @return all squares whose traps cannot attack any squares
     */
    public ArrayList<LevelSquare> obtainSquaresOfTrapablesThatCannotAttackAnySquare() {

        ArrayList<LevelSquare> foundSquares = new ArrayList<>();


        // for every square with a trap
        for (LevelSquare trapSquare : squaresWithTrapable) {

            if (!canSquareAttackAtLeastOneValidSquare(trapSquare)) {
                foundSquares.add(trapSquare);
            }
        }


        return foundSquares;
    }


    /**
     * Returns whether the passed square's trap can attack any valid square (empty or with a robot)
     *
     * @param trapSquare square to check
     * @return whether the passed squares trap can attack any valid square (empty or with a robot)
     */
    public boolean canSquareAttackAtLeastOneValidSquare(LevelSquare trapSquare) {

        /*ArrayList<LevelSquare> attackedSquares = registerSquaresAttackedByTrapOfSquare(trapSquare, false);

        if (attackedSquares != null){
            for (LevelSquare attackedSquare : attackedSquares){
                if ((!attackedSquare.isOccupied()) || (attackedSquare.getObject() instanceof Robot)){
                    return true;
                }
            }
        }

        return false;*/

        ArrayList<RelativePosition> relativePositions = ((Trapable) trapSquare.getObject())
                .obtainAttackRelativePositions();

        for (RelativePosition relativePosition : relativePositions) {
            LevelSquare attackedSquare = obtainSquareFromRelativePosition(trapSquare, relativePosition);
            if (attackedSquare != null && (!attackedSquare.isOccupied() || attackedSquare.getObject() instanceof Robot)) {
                return true;
            }
        }

        return false;

        /*if (trapSquare.isOccupied() && trapSquare.getObject() instanceof Trapable) {

            Trapable trapable = (Trapable) trapSquare.getObject();

            boolean doesTrapAttackAtLeastOneValidSquare = false;

            // check for every squares with at least an attack if it is attacked by the trap (being occupied by a robot or empty)
            for (LevelSquare square : squaresTrapMap.keySet()) {

                ArrayList<Trapable> trapables = squaresTrapMap.get(square);

                if (trapables != null && trapables.size() != 0) {
                    if (trapables.contains(trapable)) {
                        if (!square.isOccupied() || square.getObject() instanceof Robot) {
                            doesTrapAttackAtLeastOneValidSquare = true;
                            break;
                        }
                    }
                }

                if (doesTrapAttackAtLeastOneValidSquare) break;
            }

            return doesTrapAttackAtLeastOneValidSquare;
        }

        return false;*/
    }


    /**
     * Returns whether the passed square is the first one attacked by the passed laser cannon trap
     *
     * @param square      square to check
     * @param laserCannon cannon to check
     * @return whether the passed square is the first one attacked by the cannon
     */
    public boolean isSquareTheFirstOneAttackedByLaser(LevelSquare square, Trap_LaserCannon laserCannon) {

        LevelSquare laserCannonSquare = null;

        // find the cannon square
        for (LevelSquare squareWithTrap : squaresWithTrapable) {
            if (squareWithTrap.getObject().equals(laserCannon)) {
                laserCannonSquare = squareWithTrap;
                break;
            }
        }

        if (laserCannonSquare != null) {

            // find the relative position between the squares, if they are next to each other either
            // vertically or horizontally then it means that the square to check is the first one
            // attacked by the laser cannon
            RelativePosition differenceRelativePosition =
                    LevelSquare.obtainRelativePositionBetweenSquares(laserCannonSquare, square);

            if (Math.abs(differenceRelativePosition.getHorizontalCoord()) == 1 || Math.abs(differenceRelativePosition.getVerticalCoord()) == 1) {
                return true;
            }
        }

        return false;

    }


}