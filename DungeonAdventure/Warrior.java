package DungeonAdventure;

import java.io.Serializable;
import java.util.Random;

public class Warrior extends Hero implements Serializable {

    public Warrior(String theName) {
        super("Bruiser",
                theName, 125, 4, .8, 35, 60, .2);
    }

    /**
     * DungeonAdventure.Warrior special skill is Crushing Blow, which does big dmg at a random chance to hit.
     * Currently hits at a 50% chance
     * @return The damage or 0 if missed.
     */
    @Override
    public int specialSkill() {
        Random random = new Random();
        if (random.nextDouble() <= .5) {
            //Successful roll
            int damage = random.nextInt(60, 150) + 1;
            return damage;
        } else {
            //Miss roll
            return 0;
        }
    }
}