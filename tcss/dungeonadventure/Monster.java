package tcss.dungeonadventure;


import java.util.Random;

public class Monster extends AbstractDungeonCharacter {
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
     * @return int 0 if they fail to heal, otherwise return heal amount.
     */
    public int tryToHeal() {
        Random random = new Random();
        if (random.nextDouble() <= myHealChance) {
            int result = random.nextInt(myMinHeal + myMaxHeal) + 1;
            setMyHealth(getMyHealth() + result);
            return result;
        } else return 0;
    }
}
