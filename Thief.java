import java.util.Random;

public class Thief extends Hero {

    public Thief(String theName) {
        super("Pot-shotter",
                theName, 75, 6, .8, 20, 40, .4);
    }

    /**
     * Thief's skill which lets them surprise attack an enemy, with 40% chance to attack twice, another 40% to attack
     * regularly, and 20% to miss.
     * @param theTarget Target being attacked
     * @return The combat log
     */
    @Override
    public String specialSkill(DungeonCharacter theTarget) {
        StringBuilder string = new StringBuilder(getMyName() + " aims at " + theTarget.getMyName() + "...");
        Random random = new Random();
        double chance = random.nextDouble();
        if (chance > .6) {
            //Crit roll

        } else if (chance > .2) {
            //Normal roll

        } else {
            //Miss roll
            string.append("\nBut they miss!");
        }
        return String.valueOf(string);
    }
}