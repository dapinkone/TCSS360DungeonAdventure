package dungeon.adventure;

public enum ActionResultType {
    /***
     * Action : Source has hit target.
     */
    Hit,
    /*** Source has missed target. */
    Miss,
    /*** Source has critically hit target. */
    CriticalHit,
    /*** Source has healed itself. */
    Heal,
    /*** Source has dealt a crushing blow against target.  */
    CrushingBlow
}
