package pis03_2016.savealltherobots.model;

/**
 * Robot that can be placed in a square
 */
public class Robot extends PlaceableObject {

    /**
     * amount of trap impacts the robot can survive without exploding
     */
    protected int resistance;

    /**
     * Constructor
     */
    public Robot() {
        resistance = 0;
    }

    /**
     * Returns the resistance value
     *
     * @return the resistance value
     */
    public int getResistance() {
        return resistance;
    }

    /**
     * Returns whether the robot can resist attacks of as many traps as indicated
     *
     * @param trapAmount number of traps that attack the robot
     * @return If a trap can resist the attack
     */
    public boolean canResistTrapAmount(int trapAmount) {
        return trapAmount <= getResistance();
    }

    /**
     * Obtains and returns the display effect data unit for the robot explosion
     *
     * @param square square of the robot
     */
    public VisualEffectDisplayData obtainExplosionDataUnit(LevelSquare square) {
        return new VisualEffectDisplayData(square, VisualEffectDisplayData.EVisualEffectType
                .ROBOT_EXPLOSION, 0, false, false);
    }

}
