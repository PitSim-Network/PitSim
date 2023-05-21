package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.aitems.misc.GoldPickup;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GoldNanoFactory extends Killstreak {
	public static GoldNanoFactory INSTANCE;
	public static Map<Player, Integer> rewardPlayers = new HashMap<>();

	public GoldNanoFactory() {
		super("Gold Nano Factory", "GoldNanoFactory", 15, 14);
		INSTANCE = this;
	}

	public static void onGoldPickup(Player player) {
		if(rewardPlayers.containsKey(player)) {
			rewardPlayers.put(player, rewardPlayers.get(player) + 1);
		} else rewardPlayers.put(player, 1);
	}

	@EventHandler
	public void onKill(KillEvent event) {
		if(!event.isKillerRealPlayer()) return;

		if(rewardPlayers.containsKey(event.getKillerPlayer())) {
			event.goldMultipliers.add(1 + (getGoldIncrease() * rewardPlayers.get(event.getKillerPlayer())) / 100D);
			rewardPlayers.remove(event.getKillerPlayer());
		}
	}

	@Override
	public void proc(Player player) {
		for(int i = 0; i < 10; i++) {
			Location spawnLoc = player.getLocation();
			spawnLoc = spawnLoc.clone();
			spawnLoc.setX(spawnLoc.getX() + (Math.random() * 10 - 5));
			spawnLoc.setZ(spawnLoc.getZ() + (Math.random() * 10 - 5));
			spawnLoc.getWorld().dropItem(spawnLoc, ItemFactory.getItem(GoldPickup.class).getItem(GoldPickup.getPickupGold()));
		}
	}

	@Override
	public void reset(Player player) {
		rewardPlayers.remove(player);
	}

	@Override
	public ItemStack getDisplayStack(Player player) {

		AItemStackBuilder builder = new AItemStackBuilder(Material.GOLD_NUGGET);
		builder.setName("&e" + displayName);
		builder.setLore(new ALoreBuilder(
				"&7Every: &c" + killInterval + " kills",
				"", "&7Spawns &610 gold ingots&7. Picking them",
				"&7up grants &cRegen " + AUtil.toRoman(GoldPickup.getRegenAmplifier() + 1) + " &7(" +
						GoldPickup.getRegenSeconds() + "s), &6" + GoldPickup.getPickupGold() + "g&7,",
				"&7and &6+" + getGoldIncrease() + "% gold &7on your next kill."));

		return builder.getItemStack();
	}

	@Override
	public String getSummary() {
		return "&6Gold Nano Factory&7 is a killstreak that spawns &6gold&7 ingots that grant you &cregen&7, increases " +
				"&6gold&7 on next kill, and gives some &6gold when picked up every &c15 kills";
	}

	public static int getGoldIncrease() {
		return 25;
	}
}
