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

	/*
	 * Returns: ItemStack
	 * Description: Returns a random item from the drop pool
	 */
	public ItemStack getRandomDrop() {
		return Misc.weightedRandom(dropPool);
	}

	/*
	* Parameters: Map<UUID, Double> damageMap - Map of UUIDs and their damage dealt to the boss
	* Returns: void
	* Description: Gives the top damage dealer 3 random drops from the drop pool, and the second place damage dealer 2
	* random drops from the drop pool, and the third place damage dealer 1 random drop from the drop pool.
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


	/*
	* Parameters: ItemStack itemStack - Item to add to the drop pool
	* double weight - Weight of the item
	* Returns: void
	* Description: Adds an item to the drop pool with a weight
	*/
	public DropPool addItem(ItemStack item, double chance) {
		dropPool.put(item, chance);
		return this;
	}
}
