package DungeonAdventure.Test;

import DungeonAdventure.DungeonCharacter;
import org.junit.jupiter.api.*;

public class DungeonCharacterTest {

    public class MockCharacter extends DungeonCharacter {
        public MockCharacter(String theName, double theHitChance) {
            super(theName,50, 1,
                    theHitChance, 10, 20);
        }
    }

    @Test
    public void attackTestSuccess() {
        MockCharacter test1 = new MockCharacter("test1", 1.0);
        MockCharacter test2 = new MockCharacter("test2", 0);
        int damage = test1.attack(test2);
        Assertions.assertEquals(damage, test2.getMyMaxHealth() - test2.getMyHealth());
    }

    @Test
    public void attackTestMiss() {
        MockCharacter test1 = new MockCharacter("test1", 1.0);
        MockCharacter test2 = new MockCharacter("test2", 0);
        int damage = test2.attack(test1);
        Assertions.assertEquals(0, damage);
    }


}
