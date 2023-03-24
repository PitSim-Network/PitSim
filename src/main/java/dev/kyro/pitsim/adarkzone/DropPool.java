package dev.kyro.pitsim.adarkzone;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DropPool {
	public Map<ItemStack, Double> dropPool = new HashMap<>();

	public DropPool() {
	}

	/**
	 * Returns a random item from the dropPool
	 *
	 * @return ItemStack from dropPool
	 */
	public ItemStack getRandomDrop() {
		if(dropPool.isEmpty()) return null;
		return Misc.weightedRandom(dropPool);
	}

	public void mobDistribution(Player killer, PitMob deadMob) {
		if(killer == null) return;
		if(dropPool.isEmpty()) return;
		ItemStack drop = getRandomDrop();
		AUtil.giveItemSafely(killer, drop);
	}

	/**
	 * Gives the top n damage dealers drops from the dropPool
	 *
	 * @param damageMap map of player UUIDs and the damage they did to a boss
	 */
	public void bossDistribution(Player killer, PitBoss deadBoss, Map<UUID, Double> damageMap) {
//		TODO: This whole method should have weighted chances to give drops configurable by the constructor of the drop pool prob

		double totalDamage = 0;
		for(Map.Entry<UUID, Double> entry : damageMap.entrySet()) {
			UUID uuid = entry.getKey();
			double damage = entry.getValue();
			totalDamage += damage;
		}

		for(Map.Entry<UUID, Double> entry : damageMap.entrySet()) {
			UUID uuid = entry.getKey();
			double damage = entry.getValue();
			if(damage / totalDamage >= 0.05) {
				Player player = Bukkit.getPlayer(uuid);
				if(player == null) continue;
				PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
				pitPlayer.unlockFastTravelDestination(deadBoss.getSubLevel());
			}
		}

		if(dropPool.isEmpty()) return;

//		if(topDamageDealer == null) return;
//		Player player = Bukkit.getPlayer(topDamageDealer);
//		if(player == null) return;
//
//		ItemStack drop = getRandomDrop();
//		AUtil.giveItemSafely(player, drop);
	}

	/**
	 * Adds item to the dropPool
	 *
	 * @param item   item to be added to dropPool
	 * @param weight weight of item being selected from dropPool
	 * @return DropPool istance
	 */
	public DropPool addItem(ItemStack item, double weight) {
		dropPool.put(item, weight);
		return this;
	}
}
