package dev.kyro.pitsim.pitevents;

import dev.kyro.pitsim.controllers.PitEventManager;
import dev.kyro.pitsim.controllers.objects.PitEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class TestEvent extends PitEvent {
    public static TestEvent INSTANCE;

    public TestEvent() {
        super("Test Event", 5, true, ChatColor.GREEN);
        INSTANCE = this;
    }

    @Override
    public String getName() {return "Test Event";}

    @Override
    public void prepare() {
        Bukkit.broadcastMessage("Preparing for: " + name);
    }

    @Override
    public void start() {
        Bukkit.broadcastMessage("Starting: " + name + ". Ends in " + minutes + " minutes.");
    }

    @Override
    public void end() {
        Bukkit.broadcastMessage(name + " has ended.");
    }
}
