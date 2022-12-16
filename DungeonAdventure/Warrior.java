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
     */
    @Override
    public void specialSkill(DungeonCharacter theTarget) {
        Random random = new Random();
        int amount = 0;
        ActionResultType resultType = ActionResultType.Miss;
        if (random.nextDouble() <= .5) {
            //Successful roll
            amount = random.nextInt(60, 150) + 1;
            resultType = ActionResultType.CrushingBlow;
        }
        theTarget.takeDamage(amount);
        RecordQ.getInstance().add(
                new HealthChangeRecord(this, theTarget, amount, resultType));
    }
}
