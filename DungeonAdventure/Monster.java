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
//
//    class Ogre extends Monster {
//        public Ogre() {
//            super("Ogre", 200, 2,.6, 30,60
//                    ,.1,30,60);
//        }
//
//    }
//    class Gremlin extends Monster {
//
//        public Gremlin() {
//            super("Gremlin", 70, 5, .8, 15, 30,
//                    .4, 20, 40);
//        }
//
//    }
//    class Skeleton extends Monster {
//        public Skeleton() {
//            super("Skeleton", 100, 3, .8, 30, 50,
//                    .3, 30,50);
//        }
//
//    }
}
