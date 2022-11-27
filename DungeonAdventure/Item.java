package DungeonAdventure;

import java.io.Serializable;

public enum Item implements Serializable {
    /***
     * contains all possible items in the game: potions, pillars, etc
     */
    HealingPotion,
    VisionPotion,
    PillarAbstraction,
    PillarEncapsulation,
    PillarInheritance,
    PillarPolymorphism,
    Entrance,
    Exit,
    Pit
}
