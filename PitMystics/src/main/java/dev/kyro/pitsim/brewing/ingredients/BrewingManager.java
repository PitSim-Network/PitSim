package dev.kyro.pitsim.brewing.ingredients;

import dev.kyro.pitsim.brewing.objects.BrewingAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class BrewingManager implements Listener {

    public static List<BrewingAnimation> brewingAnimations = new ArrayList<>();
    public static List<ArmorStand> brewingStands = new ArrayList<>();

    public static void onStart() {
        brewingAnimations.add(new BrewingAnimation(new Location(Bukkit.getWorld("darkzone"), 10, 71, -80)));

    }


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(event.getClickedBlock().getType() != Material.CAULDRON) return;
        if(event.getPlayer().getWorld() != Bukkit.getWorld("darkzone")) return;

        event.setCancelled(true);
        for (BrewingAnimation brewingAnimation : brewingAnimations) {
            if(brewingAnimation.location.equals(event.getClickedBlock().getLocation())) brewingAnimation.addPlayer(event.getPlayer());
        }
    }
}
