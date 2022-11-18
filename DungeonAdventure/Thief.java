package DungeonAdventure;

import java.util.Random;

public class Thief extends Hero {

    public Thief(String theName) {
        super("Scout",
                theName, 75, 6, .8, 25, 45, .4);
    }

    /**
     * DungeonAdventure.Thief's skill which lets them surprise attack an enemy, with 40% chance to attack twice, another 40% to attack
     * regularly, and 20% to miss.
     * @return 2 for critical success (2 attacks), 1 for normal attack, and 0 for miss.
     */
    @Override
    public int specialSkill() {
        Random random = new Random();
        double chance = random.nextDouble();
        if (chance > .6) {
            //Crit roll
            return 2;
        } else if (chance > .2) {
            //Normal roll
            return 1;
        } else {
            //Miss roll
            return 0;
        }
    }
}