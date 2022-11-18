package DungeonAdventure;

import java.util.Random;

public class Priestess extends Hero {

    public Priestess(String theName) {
        super("Survivalist",
                theName, 75, 5, .8, 25, 45, .3);
    }

    /**
     * The priestess special is to heal. Currently coded to only heal themselves, but if in the future
     * multiple party members are added to the functionality then the code should be changed.
     * @return The amount of healing
     */
    @Override
    public int specialSkill() {
        Random random = new Random();
        int healing = random.nextInt(10, 50) + 1;
        if (getMyHealth() + healing > getMyMaxHealth()) {
            setMyHealth(getMyMaxHealth());
        } else {
            setMyHealth(getMyHealth() + healing);
        }
        return healing;
    }
}