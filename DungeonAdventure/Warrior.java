package DungeonAdventure;

import java.util.Random;

public class Warrior extends Hero {

    public Warrior(String theName) {
        super("Bruiser",
                theName, 125, 4, .8, 35, 60, .2);
    }

    /**
     * DungeonAdventure.Warrior special skill is Crushing Blow, which does big dmg at a random chance to hit.
     * Currently hits at a 50% chance
     * @param theTarget The enemy to get hit
     * @return The combat log.
     */
    @Override
    public String specialSkill(DungeonCharacter theTarget) {
        StringBuilder string = new StringBuilder(getMyName() + " attempts to crush " + theTarget.getMyName() + "...");
        Random random = new Random();
        if (random.nextDouble() <= .5) {
            //Successful roll
            int damage = random.nextInt(60, 150) + 1;
            string.append("\n" + theTarget.getMyName() + " is brutalized for " + damage + " damage!");
        } else {
            //Miss roll
            string.append("\nBut they miss!");
        }
        return String.valueOf(string);
    }
}
