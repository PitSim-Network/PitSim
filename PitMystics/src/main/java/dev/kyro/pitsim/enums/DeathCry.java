package dev.kyro.pitsim.enums;

public enum DeathCry {

    MARIO_DEATH("Super Mario"),
    GHAST_SCREAM("Ghast Scream");



    public String refName;

    DeathCry(String refName) {
        this.refName = refName;
    }
}
