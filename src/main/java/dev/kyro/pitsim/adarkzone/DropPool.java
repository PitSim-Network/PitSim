package dev.kyro.pitsim.adarkzone;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class DropPool {
	public Map<ItemStack, Double> dropPool = new HashMap<>();

	public DropPool() {
	}

	public ItemStack getRandomDrop() {
		return Misc.weightedRandom(dropPool);
	}

	/*
	* Parameters: Map<Player, Double> damageMap - Map of players and their damage dealt to the boss
	* Returns: void
	* Description: Gives the top damage dealer 3 random drops from the drop pool, and the second place damage dealer 2
	* random drops from the drop pool, and the third place damage dealer 1 random drop from the drop pool.
	*/
	public void distributeRewards(Map<Player, Double> damageMap) {

		for (int j = 0; j < 3; j++) {
			Player topDamageDealer = null;
			double topDamage = 0;
			for(Player player : damageMap.keySet()) {
				double damage = damageMap.get(player);
				if(damage > topDamage) {
					topDamage = damage;
					topDamageDealer = player;
				}
			}

			if (topDamageDealer == null) return;

			for(int i = j; i < 3; i++) {
				ItemStack drop = getRandomDrop();
				AUtil.giveItemSafely(topDamageDealer, drop);

			}

			damageMap.remove(topDamageDealer);

		}
	}
	public DropPool addItem(ItemStack item, double chance) {
		dropPool.put(item, chance);
		return this;
	}
}
