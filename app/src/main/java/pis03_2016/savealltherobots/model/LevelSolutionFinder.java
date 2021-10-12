package pis03_2016.savealltherobots.model;

import java.util.ArrayList;


/**
 * Class dedicated to determining whether levels have a valid solution
 * or are impossible to resolve
 */
public class LevelSolutionFinder {


    /* Note: attributes here are static so they can be accessed by strongly recursive functions without
    consuming memory unnecessarily */

    /**
     * Value used to consider a very complex level not solvable if a huge number of variations
     * have not found a solution for it
     */
    static final long maximumAllowedVariationChecks = Long.MAX_VALUE / ((long) (1E15)) - 1;

    /**
     * Level to seek a solution for
     */
    static Level level;
    /**
     * Normal robots to consider for level solution
     */
    static ArrayList<Robot> normalRobots;
    /**
     * Special robots to consider for level solution
     */
    static ArrayList<Robot> specialRobots;
    /**
     * Squares to consider for level solution
     */
    static ArrayList<LevelSquare> squares;
    /**
     * number of variations checked for solution at a certain moment
     */
    static long checkedVariations;


    /**
     * Determines whether the passed level has a valid solution
     * or is impossible to resolve and therefore succeed
     *
     * @param level level to check
     * @return whether the passed level can be resolved
     */
    public static boolean doesLevelHaveAValidSolution(Level level) {

        /**
         * LEVEL SOLUTION FINDER ALGORITHM EXPLANATION:
         *
         * The level solution finder has to determine whether a passed level has a valid
         * solution (i.e., a player would have a chance to succeed resolving it) or not.
         *
         * It's important to differentiate between optimum solution finding problems and the
         * one here, since the concept of "unique optimum solution" makes no sense here: any distribution
         * of robots that manages to save them all proves the level as solvable, so once we find
         * a solution, we return that we found a solution: all valid solutions are equally optimum,
         * because they all prove that a user could eventually resolve the level successfully. That's
         * why it would be inadequate to use here some branch and bound traditional elements, such as
         * inferior and superior height values, which are strongly related with the concept of optimality
         * and finding new local optimums to eventually unveil the global one. Strictly talking, our
         * solution finder algorithm is not a branch and bound algorithm, but it uses some heuristics
         * like many of those algorithms do (not the heights ones, as already stated).
         *
         * A level consists in a set of inventory robots and a grid with traps and blocks. As said,
         * a level is considered solvable when all the robots, once placed in the level grid, can
         * survive the attacks of the traps installed in the grid squares, ignoring the destiny of
         * exploding robots, since they always explode destroying themselves (so the user has to place
         * them carefully to avoid it damaging other robots).
         *
         * The initial heuristic of the algorithm can only be applied when there are not explosive robots
         * in the level. It consists in finding all the squares which do not receive a single trap attack by
         * start and place in those squares as many normal robots as possible, since they will be secure there
         * even though no special resistant robot protects them. As said, this cannot be done if there are
         * explosive robots in the inventory, because they could ruin the supposed safety of a square with their
         * explosion.
         *
         * The next stage is to locate all the remaining non-occupied squares. On some (or all) of those squares where
         * all the robots that have not been placed yet will be installed in. In the trivial case in which there are less
         * empty squares than robots left to place, we exit considering the level unsolvable. Otherwise, we need to test 'k'
         * variations without element repetition until we find a solution, where 'k' is the number of special robots
         * to place (we will cover later why 'k' does not include the normal robots that haven't been placed).
         * Said with other words, we have to progressively choose as many squares from the total remaining empty
         * squares as the amount of special robots the inventory has (we will later explain why).
         *
         * Those variations are generated with one of the algorithms that takes less computational time for that task:
         * Heap's algorithm, that obtains (in our case, recursively) all the variations of a set of elements by generating
         * each new variation simply interchanging two elements from the previous one.
         *
         * Every time a variation is generated, we construct in a very simple way a grid robot distribution from it, 
         * and test if that robot distribution permits to solve the level and therefore prove the existence of a level
         * solution. This is done by associating to each robot that has not been placed yet the first square of the
         * variation that has not been occupied yet.
         *
         * Afterwards, we check, using level logic functions, whether all the special robots placed can survive. Then
         * we can check if there are enough non-attacked (i.e., completely safe) squares for the normal robots that
         * are left to be placed. We can do this here without the need of generating permutations for those normal robots
         * as well because of the following rule: once all traps and damaging robots (i.e., the explosive ones) are placed,
         * if they cannot be moved, all the squares that are safe for normal robots at that point, will still be safe in the
         * future. That's why we separate special robots and normal ones in the algorithm and first place all the special ones
         * through variations: we cannot guarantee that normal robots will be safe if special robots can still damage them
         * (the explosive ones) or protect them (the resistant ones) later, but we can guarantee it if we invert the process,
         * like we do in this algorithm. This saves lots of computational cycles, specially in those levels with a big amount
         * of normal robots to be placed.
         *
         * Despite of the implemented heuristics, the nature of the algorithm is to grow in computational steps with a factorial
         * tendency for a linear increase in the input. Thanks to the structure and steps described above, however, this algorithm
         * can still be computed in a reasonably small time for the levels of the complexity that the level generation builds,
         * but with levels with a larger amount of special robots and empty squares it would be significantly slower. We have established,
         * therefore, a maximum number of checked variations that if was surpassed would lead us to consider that the level has no
         * solution, even though that it would not be certain since not all the possible robot distributions would have been tested,
         * yet the algorithm would have already consumed the maximum computational steps we could tolerate. This is, however, not
         * a problem in the context of our level generator, as we have said, because it would only happen with levels quite more
         * complex than the ones the game offers, even at high difficulties.
         *
         */

        checkedVariations = 0;

        // static assignations are done in order to avoid dedicating memory passing
        // it as a parameter in the variation function, that is called a massive amount of times
        LevelSolutionFinder.level = level;

        // create all the robot objects to be used
        ArrayList<Robot> robots = level.createAllInventoryRobots();

        // establish which traps attack which squares at start (with no robots placed)
        level.establishTrapsAttackingEachSquare();

        // obtain those squares that without the need
        // of any special robot protection are already safe for normal robots;
        // there won't be any if there are explosive robots!
        ArrayList<LevelSquare> safeSquares = (level.areThereExplosiveRobots()) ? new ArrayList<LevelSquare>() : level.obtainUnattackedEmptySquares();

        // if there are at least enough safe squares for all the robots,
        // even if we don't consider resistant robots protecting others, there's a solution
        if (safeSquares.size() >= robots.size()) {

            // empty the traps per square register since it will be regenerated afterwards when
            // robots are placed and we don't want duplicities
            level.getGrid().emptySquaresTrapMap();

            return true;
        }

        // divide robots in two arrays, one for the normal ones and another one for the special ones;
        // install all the normal robots that for sure can be already safe right away
        normalRobots = new ArrayList<>();
        specialRobots = new ArrayList<>();
        int usedSafeSquares = 0;
        for (Robot robot : robots) {
            if (robot.getClass() == Robot.class) {
                if (usedSafeSquares < safeSquares.size()) {
                    level.getGrid().installObjectAt(robot, safeSquares.get(usedSafeSquares));
                    usedSafeSquares++;
                } else normalRobots.add(robot);
            } else specialRobots.add(robot);
        }

        // empty the traps per square register since it will be regenerated afterwards when
        // robots are placed and we don't want duplicities
        level.getGrid().emptySquaresTrapMap();

        // obtain all empty squares
        squares = level.obtainAllNonOccupiedSquares();

        // a level with more robots to place than empty squares can't be successfully solved
        if (robots.size() - usedSafeSquares > squares.size()) {
            return false;
        }

        // create and array with indices in the form [0,1,...,squareAmount]
        // to be used for variation calculus
        int squareIndices[] = new int[squares.size()];
        for (int i = 0; i < squares.size(); i++) {
            squareIndices[i] = i;
        }

        // check variations of squares and special robots in them to try to find a solution;
        // return if any of the combinations gave a valid solution for the level
        boolean doesLevelHaveASolution = checkSquareIndicesVariations(squareIndices, squares.size());

        // uninstall all the normal robots placed at start
        for (int i = 0; i < usedSafeSquares; i++) {
            level.getGrid().uninstallObjectAt(safeSquares.get(i));
        }

        return doesLevelHaveASolution;
    }


    /**
     * Checks all the variations of squares and robots in the first of those squares (as many as special robots are there);
     * this method uses Heap's algorithm (1962, it generates next variation with a two-elements swap from the previous one,
     * so it's faster than other brute force approaches) to generate the square indices variations. It also
     * uses calls to functions to determine whether for a specific variation the level has a valid solution,
     * in which case returns true
     *
     * @param recursiveCounter value used for recursively generating variations
     * @param squareIndices    array of square indices to consider for a row of variations
     * @return whether an invoked or generated variation gives a valid
     */
    private static boolean checkSquareIndicesVariations(int[] squareIndices, int recursiveCounter) {

        // if counter is 1, a new variation has been generated and we check if it solves the level
        if (recursiveCounter == 1) {
            checkedVariations++;
            return checkIfVariationSolvesTheLevel(squareIndices);
        } else {

            boolean isCounterEven = recursiveCounter % 2 == 0;

            // iterate so elements in the array can be progressively swapped so eventually
            // all the variations are generated, unless a solution is found at some point
            for (int i = 0; i < recursiveCounter; i++) {

                // consider level not solvable if an astronomic amount
                // of variations haven't shown a solution for it
                if (checkedVariations >= maximumAllowedVariationChecks) {
                    return false;
                }

                // generate variations with a smaller counter
                else if (checkSquareIndicesVariations(squareIndices, recursiveCounter - 1)) {
                    return true;
                }

                int j = (isCounterEven) ? i : 0;

                // swap the last element with the ith element
                int temp = squareIndices[recursiveCounter - 1];
                squareIndices[recursiveCounter - 1] = squareIndices[j];
                squareIndices[j] = temp;
            }


        }

        return false;
    }

    /**
     * Given the square indices passed as an array, returns whether placing robots by order at the first
     * n (robot amount) squares of the passed indices permit to solve the level successfully
     *
     * @param squareIndices square indices forming the variation
     * @return whether the variation solves the level
     */
    private static boolean checkIfVariationSolvesTheLevel(int[] squareIndices) {

        boolean doesVariationSolveTheLevel = false;

        // install special robots at the squares associated with the current variation
        for (int i = 0; i < specialRobots.size(); i++) {
            level.getGrid().installObjectAt(specialRobots.get(i), squares.get(squareIndices[i]));
        }

        // establish which traps attack each square with the current robot distribution
        level.establishTrapsAttackingEachSquare();

        boolean bCanAllSpecialRobotsSurvive = true;

        // check if those special robots can survive
        for (int i = 0; i < specialRobots.size(); i++) {
            bCanAllSpecialRobotsSurvive = level.canRobotOfSquareSurvive(squares.get(squareIndices[i]));
            if (bCanAllSpecialRobotsSurvive) break;
        }


        // if special robots can survive, check if there are enough empty squares left for the normal ones
        if (bCanAllSpecialRobotsSurvive) {

            doesVariationSolveTheLevel = normalRobots.size() <= level.obtainUnattackedEmptySquaresAmount();
        }


        // before returning, clean the scene: empty arrays and uninstall the robots from the squares so they can be
        // placed again in another variation
        level.getGrid().emptySquaresTrapMap();
        for (int i = 0; i < specialRobots.size(); i++) {
            level.getGrid().uninstallObjectAt(squares.get(squareIndices[i]));
        }

        return doesVariationSolveTheLevel;
    }
}
