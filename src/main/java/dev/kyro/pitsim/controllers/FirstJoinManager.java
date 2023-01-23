package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.KitItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class FirstJoinManager implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.prestige > 0 || player.getInventory().getContents().length > 0) return;

		player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
		player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
		player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));

		player.getInventory().setLeggings(KitManager.getItem(KitItem.SWEATY_GHEART));

		player.getInventory().setItem(0, KitManager.getItem(KitItem.EXE_SWEATY));
		player.getInventory().setItem(1, KitManager.getItem(KitItem.MLB_DRAIN));
	}
}
