package tcss.dungeonadventure;

import java.io.Serializable;
import java.util.Map;

public enum Item implements Serializable {
    /***
     * contains all possible items in the game: potions, pillars, etc.
     */
    HealingPotion,
    VisionPotion,
    PillarAbstraction,
    PillarEncapsulation,
    PillarInheritance,
    PillarPolymorphism,
    Entrance,
    Exit,
    Pit;
    private static final Map<Item, String> ASCII = Map.of(
            PillarAbstraction, "A",
            PillarEncapsulation, "E",
            PillarInheritance, "I",
            PillarPolymorphism, "P",
            Pit, "X",
            Entrance, "i",
            Exit, "O",
            HealingPotion, "H",
            VisionPotion, "V");


    public String asAscii() {
        return ASCII.get(this);
    }
}
