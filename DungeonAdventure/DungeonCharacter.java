package DungeonAdventure;

import java.util.Random;

public abstract class DungeonCharacter {
    private static final Random RANDOM = new Random();
    private String myName;
    private int myHealth;
    private final int myMaxHealth;
    private int myAttackSpeed;
    private double myHitChance;
    private double myDodgeChance = 0;
    private int myMinDmg;
    private int myMaxDmg;

    public DungeonCharacter(final String theName,
                            final int theHealth,
                            final int theAttackSpeed,
                            final double theHitChance,
                            final int theMinDmg,
                            final int theMaxDmg) {
        myName = theName;
        myHealth = theHealth;
        myMaxHealth = theHealth;
        myAttackSpeed = theAttackSpeed;
        myHitChance = theHitChance;
        myMinDmg = theMinDmg;
        myMaxDmg = theMaxDmg;
    }



    /**
     * Attack method, rolls a chance to hit, and another chance for that hit to be blocked.
     * Then rolls the damage within the character's damage range.
     * @param theTarget the target to attack
     * @return String of the combat log
     */
    String attack(final DungeonCharacter theTarget) {
        StringBuilder result = new StringBuilder(getMyName() + " attacks " + theTarget.getMyName() + "...\n");

        if (RANDOM.nextDouble() <= myHitChance) {

            if (RANDOM.nextDouble() <= theTarget.getMyDodgeChance()) {
                result.append("But " + theTarget.getMyName() + " blocks it!\n");
            } else {
                int damage = RANDOM.nextInt(myMinDmg, myMaxDmg + 1);
                theTarget.setMyHealth(theTarget.getMyHealth() - damage);
                result.append(theTarget.getMyName() + " takes " + damage + " damage.\n");
            }

        } else {
            result.append("But the attack misses!\n");
        }

        return String.valueOf(result);
    }


    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public int getMyHealth() {
        return myHealth;
    }

    public void setMyHealth(int myHealth) {
        this.myHealth = myHealth;
    }

    public int getMyMaxHealth() {
        return myMaxHealth;
    }

    public int getMyAttackSpeed() {
        return myAttackSpeed;
    }

    public double getMyDodgeChance() {
        return myDodgeChance;
    }

    public void setMyDodgeChance(double myBlockChance) {
        this.myDodgeChance = myBlockChance;
    }

    public String getStats() {
        StringBuilder string = new StringBuilder();
        string.append("Name: " + myName);
        string.append("\nHealth: " + myHealth + "/" + myMaxHealth);
        string.append("\nSpeed: " + myAttackSpeed);
        string.append("\nHit Chance: " + myHitChance * 100 + "%");
        string.append("\nDamage: " + myMinDmg + "-" +myMaxDmg);
        if (myDodgeChance > 0) string.append("\nBlock Chance: " + myDodgeChance * 100 + "%");
        return String.valueOf(string);
    }
}
