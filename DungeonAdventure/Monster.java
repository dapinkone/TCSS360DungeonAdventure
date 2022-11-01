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
                   final int theMaxDmg) {
        super(theName,theHealth,theAttackSpeed,theHitChance,theMinDmg,theMaxDmg);
    }

    @Override
    public String specialSkill(DungeonCharacter theTarget) {
        return null;
    }

    class Ogre extends Monster {

        public Ogre() {
            super("Ogre", 200, 2,.6, 30,60);
            myHealChance = .1;
            myMinHeal = 30;
            myMaxHeal = 60;
        }
    }
}
