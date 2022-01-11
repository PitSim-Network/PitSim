package dev.kyro.pitsim.tutorial;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public enum TutorialMessage {
    DARK_BLUE("Dark Blue"),
    DARK_GREEN("Dark Green");


    public String message;

    TutorialMessage(String message) {
        this.message = message;

    }

    public static List<String> messageStrings = new ArrayList<>();

    public static List<String> getMessages() {
        for(TutorialMessage value : TutorialMessage.values()) {
            getMessages().add(value.message);

            Bukkit.broadcastMessage("Dark Blue");
        }
        return messageStrings;
    }
}
