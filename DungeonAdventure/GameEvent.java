package DungeonAdventure;

/***
 * notifications that the model provides in a message queue for the controller
 * or view to check if something has happened to the game state.
 */
public enum GameEvent {
    FoundItem,
    CombatStart, CombatEnd,
    AttackMissed, AttackHit,
    HealthChanged,
    PlayerDeath,
    MonsterDeath,
    Victory
}
