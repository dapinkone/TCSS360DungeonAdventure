package DungeonAdventure;

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
}
