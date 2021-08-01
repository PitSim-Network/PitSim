package dev.kyro.pitsim.pitevents;

import dev.kyro.pitsim.controllers.PitEventManager;
import dev.kyro.pitsim.controllers.objects.PitEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class TestEvent2 extends PitEvent {
    public static TestEvent2 INSTANCE;

    public TestEvent2() {
        super("Test Event2", 5, true, ChatColor.GOLD);
        INSTANCE = this;
    }

    @Override
    public String getName() {return "Test Event2";}

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
