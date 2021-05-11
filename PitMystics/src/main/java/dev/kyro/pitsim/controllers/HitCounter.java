package dev.kyro.pitsim.controllers;

import org.bukkit.entity.Player;

public class HitCounter {

	public static void incrementCounter(Player player, PitEnchant pitEnchant) {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.enchantHits.putIfAbsent(pitEnchant, 0);
		Integer currentCounter = pitPlayer.enchantHits.get(pitEnchant);

		pitPlayer.enchantHits.put(pitEnchant, currentCounter + 1);
	}

	public static boolean hasReachedThreshold(Player player, PitEnchant pitEnchant, int threshold) {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.enchantHits.putIfAbsent(pitEnchant, 0);
		Integer currentCounter = pitPlayer.enchantHits.get(pitEnchant);

		if(currentCounter < threshold) return false;

		pitPlayer.enchantHits.put(pitEnchant, 0);
		return true;
	}
}