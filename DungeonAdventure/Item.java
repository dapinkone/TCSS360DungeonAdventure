package DungeonAdventure;

import java.io.Serializable;
import java.util.List;

public enum Item implements Serializable {
    /***
     * contains all possible items in the game: potions, pillars, etc
     */
    Entrance,
    Exit,
    HealingPotion,
    VisionPotion,
    PillarAbstraction,
    PillarEncapsulation,
    PillarInheritance,
    PillarPolymorphism,
    Pit;

    public boolean canBePickedUp() {
        return !List.of(Entrance, Exit, Pit).contains(this);
    }
}
