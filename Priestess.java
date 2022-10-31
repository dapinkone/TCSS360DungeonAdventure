import java.util.Random;

public class Priestess extends Hero {

    public Priestess(String theName) {
        super("Survivalist",
                theName, 75, 5, .7, 25, 45, .3);
    }

    /**
     * The priestess special is to heal. Currently coded to only heal themselves, but if in the future
     * multiple party members are added to the functionality then the code should be changed.
     * @param theTarget Does not matter, so null.
     * @return String of the combat log.
     */
    @Override
    public String specialSkill(DungeonCharacter theTarget) {
        StringBuilder string = new StringBuilder(getMyName() + " treats their wounds...");
        Random random = new Random();
        int healing = random.nextInt(10, 50) + 1;
        if (getMyHealth() + healing > getMyMaxHealth()) {
            setMyHealth(getMyMaxHealth());
        } else {
            setMyHealth(getMyHealth() + healing);
        }
        string.append("\nThey recover " + healing + " health.");
        return String.valueOf(string);
    }
}