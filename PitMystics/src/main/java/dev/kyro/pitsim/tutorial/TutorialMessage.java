package dev.kyro.pitsim.tutorial;

import java.util.ArrayList;
import java.util.List;

public enum TutorialMessage {
    DARK_BLUE("Dark Blue", "Blue"),
    DARK_GREEN("Dark Green", "Green");


    public String message;
    public String identifier;

    TutorialMessage(String message, String identifier) {
        this.message = message;
        this.identifier = identifier;

    }

    public static List<String> messageStrings = new ArrayList<>();

    public static List<String> getIdentifiers() {
        for(TutorialMessage value : TutorialMessage.values()) {
            messageStrings.add(value.identifier);
        }
        return messageStrings;
    }
}
