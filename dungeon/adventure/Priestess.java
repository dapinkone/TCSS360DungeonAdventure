package dungeon.adventure;

import java.io.Serializable;
import java.util.Random;

public class Priestess extends Hero implements Serializable {

    public Priestess(String theName) {
        super("Survivalist",
                theName, 75, 5, .8, 25, 45, .3);
    }

    /**
     * The priestess special is to heal. Currently coded to only heal themselves, but if in the future
     * multiple party members are added to the functionality then the code should be changed.
     */
    @Override
    public void specialSkill(DungeonCharacter theTarget) { // kinda breaks contract?
        Random random = new Random();
        int healing = random.nextInt(10, 50) + 1;
        heal(healing);
        RecordQ.getInstance().add(
                new HealthChangeRecord(
                        this,
                        this,
                        healing,
                        ActionResultType.Heal));
    }
}