package pis03_2016.savealltherobots.model;

import android.content.Context;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Class that helps to create a level from a txt predefined file
 */
public final class FileHandler {


    /**
     * Fills the passed level with data based on the information in the passed file
     *
     * @param context  action context
     * @param level    level to fill
     * @param filename name of the file whose data is used to fill the level
     */
    public static void fillLevelFromFile(Context context, Level level, String filename) {

        // in a file we count lines starting at one
        int lineIndex = 1;

        ArrayList<String> lines;
        try {
            lines = obtainFileLines(context, filename);

            // check each line of the file
            for (String line : lines) {

                // while in the grid-related lines, add objects on squares
                if (lineIndex <= LevelGrid.NUM_ROWS) {

                    // analyze groups of 3 characters to determine whether an object should be
                    // added on a square; an extra 4th character, a space, is left between all
                    // groups of 3 normal character for readability (but not at the end of the line)
                    for (int i = 0; i / 4 < LevelGrid.NUM_COLS; i = i + 4) {

                        // class of object to create
                        Class objectClass = obtainPlaceableObjectClassFromString(line.substring(i, i + 2));

                        // orientation for the object, null if that concept makes no sense for it
                        Orientation orientation = obtainOrientationFromChar(line.charAt(i + 2));

                        // create the object of the class
                        PlaceableObject object = LevelGenerator.createPlaceableObject(objectClass, orientation);


                        // check if the object isn't null (if it is so, the square corresponding to
                        // the iteration space is left empty)
                        if (object != null) {

                            int row = lineIndex - 1;
                            int col = i / 4;

                            level.getGrid().installObjectAt(object, level
                                    .getGrid().getAt(row, col));

                        }
                    }
                }

                // set level total resolution time if in the total time line
                else if (lineIndex == LevelGenerator.LEVEL_TOTAL_TIME_LINE_INDEX) {

                    // the total time has the format "X s" in the file, where X is the number of
                    // seconds and s simply notates that second is the time unit used; total time
                    // is stored as milliseconds in the level object
                    level.setTotalTime(1000 * Integer.parseInt(line.substring(0, line.indexOf(" s"))));
                }

                // add robot types to inventory as cells for each of the lines corresponding to
                // the inventory section
                else if (lineIndex >= LevelGenerator.LEVEL_INVENTORY_START_LINE_INDEX && !line.isEmpty()) {
                    int spaceCharIndex = line.indexOf(" ");
                    int totalRobotAmount = Integer.parseInt(line.substring(0, spaceCharIndex));
                    Class robotClass = obtainRobotClassFromString(line.substring(spaceCharIndex + 1));

                    if (robotClass != null) {
                        level.getInventory().addCell(new LevelInventoryCell(robotClass, totalRobotAmount));
                    }
                }

                lineIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a placeable object class from the passed string code, or null if there are no matches
     *
     * @param string the string class code
     * @return the placeable object class
     */
    private static Class obtainPlaceableObjectClassFromString(String string) {
        switch (string) {

            case "--":
                return null;

            case "##":
                return Block.class;

            case "f1":
                return Trap_Fist.class;

            case "ft":
                return Trap_FlameThrower.class;

            case "lc":
                return Trap_LaserCannon.class;

            case "qc":
                return Trap_QuadrupleLaserCannon.class;

            case "sc":
                return Trap_ElectricCircle.class;

            default:
                return null;
        }
    }

    /**
     * Returns an orientation object corresponding to the passed character
     *
     * @param character character to determine orientation
     * @return obtained orientation
     */
    private static Orientation obtainOrientationFromChar(char character) {

        if (character == '.') {
            return null;
        }

        Orientation.EOrientationValue value;

        switch (character) {
            case 'U':
                value = Orientation.EOrientationValue.UP;
                break;

            case 'D':
                value = Orientation.EOrientationValue.DOWN;
                break;

            case 'R':
                value = Orientation.EOrientationValue.RIGHT;
                break;

            case 'L':
                value = Orientation.EOrientationValue.LEFT;
                break;

            default:
                value = Orientation.EOrientationValue.NEUTRAL;
                break;
        }

        return new Orientation(value);
    }

    /**
     * Returns the robot class corresponding to the passed string code
     *
     * @param string the string code to determine the robot class
     * @return the obtained robot class
     */
    private static Class obtainRobotClassFromString(String string) {

        Class robotClass = null;

        switch (string) {
            case "robot":
                robotClass = Robot.class;
                break;

            case "robot_steel":
                robotClass = Robot_Steel.class;
                break;

            case "robot_gold":
                robotClass = Robot_Gold.class;
                break;

            case "robot_diamond":
                robotClass = Robot_Diamond.class;
                break;

            case "robot_deactivator_radio":
                robotClass = Robot_Deactivator_Radio.class;
                break;

            case "robot_explosive":
                robotClass = Robot_Explosive.class;
                break;

            default:
                break;
        }

        return robotClass;
    }


    /**
     * Returns string lines of the file with the passed filename
     *
     * @param filename the name of text file.
     * @param context  the application activity context.
     * @return file contents
     * @throws IOException
     */
    private static ArrayList<String> obtainFileLines(Context context, String filename) throws IOException {

        String line;
        ArrayList<String> lines = new ArrayList<>();
        AssetManager manager = context.getAssets();
        InputStream stream = manager.open(filename);

        if (stream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            stream.close();
        }
        return lines;
    }


}
