package DungeonAdventure;

public class MonsterFactory {

    public Monster Ogre() {
        return new Monster("Predator", 200, 2,.6,
                30,60,.1,30,60);
    }
    public Monster Gremlin() {
        return new Monster("Skitter", 70, 5, .8,
                15, 30, .4, 20, 40);
    }
    public Monster Skeleton() {
        return new Monster("Crawler", 100, 3, .8,
                30, 50, .3, 30,50);
    }
    public Monster Boss() {
        return new Monster("Awoken Horror", 330, 3,.6,
                40,60,.1,30,60);
    }
}
