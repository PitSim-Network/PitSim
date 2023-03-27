package dev.kyro.pitsim.adarkzone;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.aitems.StaticPitItem;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Supplier;

public class DropPool {
	public Map<ItemStack, Range> commonItems = new HashMap<>(); // certain amount of items are distributed each time (ex: 1-3 pearls)
	public Map<Supplier<ItemStack>, Double> rareItems = new HashMap<>(); // certain chance for an item to be added to the drop pool (10% chance for a pearl)

	public DropPool() {}

	public void mobDistribution(Player killer, PitMob deadMob) {
		if(killer == null) return;
		Map<UUID, Double> weightedMap = new HashMap<>();
		weightedMap.put(killer.getUniqueId(), 1.0);
		distributeRewards(weightedMap);
	}

	public void bossDistribution(Player killer, PitBoss deadBoss, Map<UUID, Double> damageMap) {
		double totalDamage = 0;
		for(Map.Entry<UUID, Double> entry : damageMap.entrySet()) totalDamage += entry.getValue();
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

		distributeRewards(damageMap);
	}

	public void distributeRewards(Map<UUID, Double> weightedMap) {
		List<ItemStack> rewards = new ArrayList<>();
		for(Map.Entry<ItemStack, Range> entry : commonItems.entrySet()) {
			Range range = entry.getValue();
			for(int i = 0; i < range.getRandom(); i++) rewards.add(entry.getKey());
		}
		for(Map.Entry<Supplier<ItemStack>, Double> entry : rareItems.entrySet()) {
			double chance = entry.getValue();
			if(Math.random() <= chance) rewards.add(entry.getKey().get());
		}
		for(ItemStack reward : rewards) {
			UUID choice = Misc.weightedRandom(weightedMap);
			Player player = Bukkit.getPlayer(choice);
			if(player == null) continue;
			AUtil.giveItemSafely(player, reward, true);
		}
	}

	public DropPool addCommonItem(ItemStack item, int low, int high) {
		commonItems.put(item, new Range(low, high));
		return this;
	}

	public DropPool addRareItem(StaticPitItem pitItem, double percent) {
		return addRareItem(pitItem::getItem, percent);
	}

	public DropPool addRareItem(Supplier<ItemStack> item, double percent) {
		rareItems.put(item, percent);
		return this;
	}

	private static class Range {
		private final int low;
		private final int high;

		public Range(int low, int high) {
			this.low = low;
			this.high = high;
		}

		public int getLow() {
			return low;
		}

		public int getHigh() {
			return high;
		}

		public int getRandom() {
			int amount = new Random().nextInt(high - low + 1) + low;
			return amount;
		}
	}
}
