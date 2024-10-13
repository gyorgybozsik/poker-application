package hu.bgy.pokerapp.enums;

public enum RoundRole {
   // DEALER,
    SMALL_BLIND,
    BIG_BLIND,
    SPEAKER_1,
    SPEAKER_2,
    SPEAKER_3,
    SPEAKER_4,
    SPEAKER_5,
    SPEAKER_6,
    SPEAKER_7,
    SPEAKER_8,
    ;

    public RoundRole nextRole() {
        RoundRole[] roundRoles = RoundRole.values();
        return roundRoles[(this.ordinal() + 1) % roundRoles.length];
    }
}
