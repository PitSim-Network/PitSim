package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.enums.ApplyType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;

public abstract class PitUpgrade implements Listener {
    public Player player;


    public PitUpgrade(Player player) {
      this.player = player;
    }

    public abstract String getName();
    public abstract List<String> getDescription();
    public abstract Material getItemType();

    public void onEnable() {

    }

    public void onDisable() {

    }
}
