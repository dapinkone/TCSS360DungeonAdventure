package tcss.dungeonadventure.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tcss.dungeonadventure.DungeonCharacter;
public class DungeonCharacterTest {

    @Test
    public void attackTestSuccess() {
        final MockCharacter test1 = new MockCharacter("test1", 1.0);
        final MockCharacter test2 = new MockCharacter("test2", 0);
        final int damage = test1.attack(test2);
        Assertions.assertEquals(damage, test2.getMyMaxHealth() - test2.getMyHealth());
    }

    @Test
    public void attackTestMiss() {
        final MockCharacter test1 = new MockCharacter("test1", 1.0);
        final MockCharacter test2 = new MockCharacter("test2", 0);
        final int damage = test2.attack(test1);
        Assertions.assertEquals(0, damage);
    }

    public class MockCharacter extends DungeonCharacter {
        public MockCharacter(final String theName, final double theHitChance) {
            super(theName, 50, 1,
                    theHitChance, 10, 20);
        }
    }
}
