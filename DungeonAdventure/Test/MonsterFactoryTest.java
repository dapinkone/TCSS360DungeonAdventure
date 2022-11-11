package DungeonAdventure.Test;
import DungeonAdventure.Monster;
import DungeonAdventure.MonsterFactory;
import org.junit.jupiter.api.*;

public class MonsterFactoryTest {


    @Test public void MonsterFactoryCreateTest() {
        MonsterFactory monsterFactory = new MonsterFactory();
    }
    @Test public void CreateMonsterTest() {
        MonsterFactory monsterFactory = new MonsterFactory();
        Monster test3 = monsterFactory.generateMonster("ogre");
        System.out.println(test3.getStats() + "\n");
        Monster test1 = monsterFactory.generateMonster("gremlin");
        System.out.println(test1.getStats() + "\n");
        Monster test2 = monsterFactory.generateMonster("skeleton");
        System.out.println(test2.getStats());
    }
}
