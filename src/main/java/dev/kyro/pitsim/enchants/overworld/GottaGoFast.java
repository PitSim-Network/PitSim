package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enchants.tainted.effects.Sonic;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GottaGoFast extends PitEnchant {
	public static Map<Player, Integer> speedMap = new HashMap<>();
	public static GottaGoFast INSTANCE;

	public GottaGoFast() {
		super("Gotta go fast", false, ApplyType.PANTS,
				"gotta-go-fast", "gottagofast", "gtgf", "gotta", "fast");
		INSTANCE = this;
		isUncommonEnchant = true;
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					Map<PitEnchant, Integer> enchantMap = EnchantManager.getEnchantsOnPlayer(player);
					int enchantLvl = enchantMap.getOrDefault(INSTANCE, 0);
					int oldEnchantLvl = speedMap.getOrDefault(player, 0);

					int sonicTier = EnchantManager.getEnchantLevel(player, Sonic.INSTANCE);
					if(enchantLvl == oldEnchantLvl && sonicTier > 0) continue;

					if(enchantLvl != oldEnchantLvl) {
						speedMap.put(player, enchantLvl);

						new BukkitRunnable() {
							@Override
							public void run() {
								player.setWalkSpeed(getWalkSpeed(enchantLvl));
							}
						}.runTask(PitSim.INSTANCE);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Move &e" + Misc.roundString(getWalkSpeedLore(enchantLvl)) + "&e% faster &7at all times"
		).getLore();
	}

	public static float getWalkSpeed(int enchantLvl) {
		return 0.2F + (0.2F * (getWalkSpeedLore(enchantLvl) / 100));
	}

	public static float getWalkSpeedLore(int enchantLvl) {
		return enchantLvl * 5 + 5;
	}
}
