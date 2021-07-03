package dev.kyro.pitsim.enums;

public enum KillEffect {

    EXE_DEATH("Executioner"),
    FIRE("Fire");


    public String refName;

    KillEffect(String refName) {
        this.refName = refName;
    }
}
