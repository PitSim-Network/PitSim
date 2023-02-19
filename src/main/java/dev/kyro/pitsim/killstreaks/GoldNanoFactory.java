package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.LevelManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class GoldNanoFactory extends Killstreak {

	public static GoldNanoFactory INSTANCE;

	public GoldNanoFactory() {
		super("Gold Nano Factory", "GoldNanoFactory", 15, 14);
		INSTANCE = this;
	}

	public static Map<Player, Integer> rewardPlayers = new HashMap<>();

	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		if(NonManager.getNon(event.getPlayer()) != null) return;
		if(event.getItem().getItemStack().getType() == Material.GOLD_INGOT) {
			event.setCancelled(true);
			event.getItem().remove();

			Sounds.SUCCESS.play(event.getPlayer());
			LevelManager.addGold(event.getPlayer(), 123);

			Misc.applyPotionEffect(event.getPlayer(), PotionEffectType.REGENERATION, 20 * getRegenSeconds(), 3, true, false);

			if(Killstreak.hasKillstreak(event.getPlayer(), this)) {
				if(rewardPlayers.containsKey(event.getPlayer())) {
					rewardPlayers.put(event.getPlayer(), rewardPlayers.get(event.getPlayer()) + 1);
				} else rewardPlayers.put(event.getPlayer(), 1);
				AOutput.send(event.getPlayer(), "&6&lGOLD PICKUP!&7 Gain &6+123g&7. &6+25% gold &7on your next kill.");
			} else {
				AOutput.send(event.getPlayer(), "&6&lGOLD PICKUP!&7 Gain &6+123g&7");
			}
		}
	}

	@EventHandler
	public void onKill(KillEvent event) {
		if(!event.isKillerPlayer()) return;

		if(rewardPlayers.containsKey(event.getKillerPlayer())) {
			event.goldMultipliers.add(1 + (25 * rewardPlayers.get(event.getKillerPlayer())) / 100D);
			rewardPlayers.remove(event.getKillerPlayer());
		}
	}

	@Override
	public void proc(Player player) {

		for(int i = 0; i < 10; i++) {
			ItemStack ingot = new ItemStack(Material.GOLD_INGOT);
			ItemMeta ingotMeta = ingot.getItemMeta();
			ingotMeta.setDisplayName(i + "");
			ingot.setItemMeta(ingotMeta);
			Location spawnLoc = player.getLocation();
			spawnLoc = spawnLoc.clone();
			spawnLoc.setX(spawnLoc.getX() + (Math.random() * 10 - 5));
			spawnLoc.setZ(spawnLoc.getZ() + (Math.random() * 10 - 5));
			spawnLoc.getWorld().dropItem(spawnLoc, ingot);
		}
	}

	@Override
	public void reset(Player player) {
		rewardPlayers.remove(player);
	}

	@Override
	public ItemStack getDisplayItem(Player player) {

		AItemStackBuilder builder = new AItemStackBuilder(Material.GOLD_NUGGET);
		builder.setName("&e" + displayName);
		builder.setLore(new ALoreBuilder(
				"&7Every: &c" + killInterval + " kills",
				"", "&7Spawns &610 gold ingots. &7Picking them",
				"&7up grants &cRegen IV &7(" + getRegenSeconds() + "s), &6123g&7,",
				"&7and &6+25% gold &7on your next kill."));

		return builder.getItemStack();
	}

	public int getRegenSeconds() {
		return 2;
	}
}
