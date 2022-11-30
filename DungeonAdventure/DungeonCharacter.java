package DungeonAdventure;

import java.io.Serializable;
import java.util.Random;

public abstract class DungeonCharacter implements Serializable {
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
        //TODO: make setters, make sure setters take proper input.
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
     * @return The damage, or 0 if missed or -1 if dodged by a hero.
     */
    public int attack(final DungeonCharacter theTarget) {
        if (RANDOM.nextDouble() <= myHitChance) {

            if (RANDOM.nextDouble() <= theTarget.getMyDodgeChance()) {
                return -1; //DODGED
            } else {
                int damage = RANDOM.nextInt(myMinDmg, myMaxDmg + 1);
                theTarget.setMyHealth(theTarget.getMyHealth() - damage);
                return damage; //HIT
            }

        } else {
            return 0; //MISSED
        }
    }

    public boolean isDead() {
        return getMyHealth() <= 0;
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