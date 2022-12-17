package dungeon.adventure;

public record HealthChangeRecord(
        AbstractDungeonCharacter source,
        AbstractDungeonCharacter target,
        int amount, // amount of healing / dmg dealt / etc
        ActionResultType actionResultType) {
}
