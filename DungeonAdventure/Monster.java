package DungeonAdventure;

import java.util.Random;

public class Monster extends DungeonCharacter {
    private final double myHealChance;
    private final int myMinHeal;
    private final int myMaxHeal;

    public Monster(final String theName,
                   final int theHealth,
                   final int theAttackSpeed,
                   final double theHitChance,
                   final int theMinDmg,
                   final int theMaxDmg,
                   final double theHealChance,
                   final int theMinHeal,
                   final int theMaxHeal) {
        super(theName, theHealth, theAttackSpeed, theHitChance, theMinDmg, theMaxDmg);

        myHealChance = theHealChance;
        myMinHeal = theMinHeal;
        myMaxHeal = theMaxHeal;
    }

    /**
     * Calls method if monster for the monster to roll to heal.
     *
     */
    public void tryToHeal() {
        Random random = new Random();
        if (random.nextDouble() <= myHealChance) {
            int result = random.nextInt(myMinHeal + myMaxHeal) + 1;
            heal(result);
            RecordQ.getInstance().add(
                    new HealthChangeRecord(
                            this,
                            this,
                            result,
                            ActionResultType.Heal));
        }
    }

    @Override
    public void takeDamage(int theAmount) {
        setMyHealth(getMyHealth() - theAmount);
        if (!isDead()) tryToHeal();
    }


}
