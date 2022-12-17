package dungeon.adventure;

public record HealthChangeRecord(
        DungeonCharacter source,
        DungeonCharacter target,
        int amount, // amount of healing / dmg dealt / etc
        ActionResultType actionResultType) {
}
