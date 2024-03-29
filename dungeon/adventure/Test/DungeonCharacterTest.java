package dungeon.adventure.Test;

import dungeon.adventure.ActionResultType;
import dungeon.adventure.AbstractDungeonCharacter;
import dungeon.adventure.RecordQ;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DungeonCharacterTest {

    @Test
    public void attackTestSuccess() {
        MockCharacter test1 = new MockCharacter("test1", 1.0);
        MockCharacter test2 = new MockCharacter("test2", 0);
        test1.attack(test2);
        final var result = RecordQ.getInstance().pop();
        assert result != null;
        // verify hit
        Assertions.assertEquals(result.actionResultType(), ActionResultType.Hit);
        Assertions.assertEquals(// damage done
                result.amount(),
                test2.getMyMaxHealth() - test2.getMyHealth());
        Assertions.assertEquals(result.target(), test2); // attacked
        Assertions.assertEquals(result.source(), test1); // attacker
    }

    @Test
    public void attackTestMiss() {
        MockCharacter test1 = new MockCharacter("test1", 1.0);
        MockCharacter test2 = new MockCharacter("test2", 0);
        test2.attack(test1);
        final var result = RecordQ.getInstance().poll();
        assert result != null;
        Assertions.assertEquals(result.actionResultType(), ActionResultType.Miss);
        Assertions.assertEquals(0, result.amount());
    }

    public static class MockCharacter extends AbstractDungeonCharacter {
        public MockCharacter(final String theName,final double theHitChance) {
            super(theName, 50, 1,
                    theHitChance, 10, 20);
        }
    }
}
