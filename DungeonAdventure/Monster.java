package DungeonAdventure;

import java.util.Random;

public class Monster extends DungeonCharacter {
    private double myHealChance;
    private int myMinHeal;
    private int myMaxHeal;

    public Monster(final String theName,
                   final int theHealth,
                   final int theAttackSpeed,
                   final double theHitChance,
                   final int theMinDmg,
                   final int theMaxDmg,
                   final double theHealChance,
                   final int theMinHeal,
                   final int theMaxHeal) {
        super(theName,theHealth,theAttackSpeed,theHitChance,theMinDmg,theMaxDmg);

        myHealChance = theHealChance;
        myMinHeal = theMinHeal;
        myMaxHeal = theMaxHeal;
    }

    /**
     * Calls method if monster for the monster to roll to heal.
     * @return int 0 if they fail to heal, otherwise return heal amount.
     */
    public int tryToHeal() {
        Random random = new Random();
        if (random.nextDouble() <= myHealChance) {
            int result = random.nextInt(myMinHeal + myMaxHeal) + 1;
            heal(result);
            return result;
        } else return 0;
    }

    @Override
    public void takeDamage(int amount) {
        setMyHealth(getMyHealth() - amount);
        if(!isDead()) tryToHeal();
    }


}
