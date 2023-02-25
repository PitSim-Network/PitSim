package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.entity.Player;

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

	public static float getWalkSpeedIncrease(Player player) {
		if(!INSTANCE.isEnabled()) return 0;

		int enchantLvl = EnchantManager.getEnchantLevel(player, INSTANCE);
		if(enchantLvl == 0) return 0;

		return getWalkSpeedIncrease(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Move &e" + Misc.roundString(getWalkSpeedIncrease(enchantLvl)) + "&e% faster &7at all times"
		).getLore();
	}

	public static int getWalkSpeedIncrease(int enchantLvl) {
		return enchantLvl * 5 + 5;
	}
}
