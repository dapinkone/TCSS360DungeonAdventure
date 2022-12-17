package dungeon.adventure;

import java.io.Serializable;
import java.util.Random;

public class Thief extends Hero implements Serializable {

    public Thief(String theName) {
        super("Scout",
                theName, 75, 6, .8, 25, 45, .4);
    }

    /**
     * dungeon.adventure.Thief's skill which lets them surprise attack an enemy, with 40% chance to critically attack,
     * another 40% to attack regularly, and 20% to miss.
     */
    @Override
    public void specialSkill(AbstractDungeonCharacter theTarget) {
        Random random = new Random();
        double chance = random.nextDouble();
        var type = ActionResultType.Miss;
        var amount = 0;
        if (chance > .6) {
            //Crit roll
            type = ActionResultType.CriticalHit;
            amount = 2 * random.nextInt(myMinDmg + 10, myMaxDmg + 1);
        } else if (chance > .2) {
            //Normal roll
            type = ActionResultType.Hit;
            amount = random.nextInt(myMinDmg, myMaxDmg + 1);
        }
        theTarget.takeDamage(amount);
        RecordQ.getInstance().add(
                new HealthChangeRecord(
                        this,
                        theTarget,
                        amount,
                        type
                )
        );
    }
}