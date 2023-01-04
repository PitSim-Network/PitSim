package dev.kyro.pitsim.adarkzone;

import dev.kyro.arcticapi.misc.AUtil;
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
	 * @return ItemStack from dropPool
	 */
	public ItemStack getRandomDrop() {
		return Misc.weightedRandom(dropPool);
	}

	/**
	 * Gives the top damage dealer 3 random drops from the drop pool, and the second place damage dealer 2
	 * random drops from the drop pool, and the third place damage dealer 1 random drop from the drop pool.
	 * @param damageMap map of player UUIDs and the damage they did to a boss
	 */
	public void distributeRewards(Map<UUID, Double> damageMap) {

		for (int j = 0; j < 3; j++)
		{
			UUID topDamageDealer = null;
			double topDamage = 0;
			for(UUID uuid : damageMap.keySet()) {
				double damage = damageMap.get(uuid);
				if(damage > topDamage) {
					topDamage = damage;
					topDamageDealer = uuid;
				}
			}

			if (topDamageDealer == null) return;
			Player player = Bukkit.getPlayer(topDamageDealer);

			if (player != null) {
				for(int i = j; i < 3; i++) {
					ItemStack drop = getRandomDrop();
					AUtil.giveItemSafely(player, drop);
				}
			}

			damageMap.remove(topDamageDealer);

		}
	}


	/**
	 * Adds item to the dropPool
	 * @param item item to be added to dropPool
	 * @param chance weight of item being selected from dropPool
	 * @return DropPool istance
	 */
	public DropPool addItem(ItemStack item, double chance) {
		dropPool.put(item, chance);
		return this;
	}
}
