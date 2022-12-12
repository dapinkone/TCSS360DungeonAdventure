package DungeonAdventure;

import java.util.LinkedList;
public final class RecordQ extends LinkedList<HealthChangeRecord> {
    private static RecordQ instance = null;
    private RecordQ() {
        super();
    }
    public static RecordQ getInstance() {
        if(instance == null) {
            instance = new RecordQ();
        }
        return instance;
    }
}
