package dev.kyro.pitsim.adarkzone.slayers.tainted.Loot;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LootRunnable {
	String profiler;

	public LootRunnable(String profiler) {
		this.profiler = profiler;
	}

	public void run(Player player, String itemName, String rare_type) {
		AOutput.send(player, rare_type + " &7you dropped a &c&l" + ChatColor.stripColor(itemName));

		Sounds.EVENT_START.play(player);

	}
}
