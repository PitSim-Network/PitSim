package net.pitsim.spigot.enchants.overworld;

import net.pitsim.spigot.controllers.EnchantManager;
import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.enums.ApplyType;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.PitLoreBuilder;

import java.util.List;

public class GottaGoFast extends PitEnchant {
	public static GottaGoFast INSTANCE;

	public GottaGoFast() {
		super("Gotta go fast", false, ApplyType.PANTS,
				"gottagofast", "gotta-go-fast", "gtgf", "gotta", "fast");
		INSTANCE = this;
		isUncommonEnchant = true;
	}

	public static float getWalkSpeedIncrease(PitPlayer pitPlayer) {
		if(!INSTANCE.isEnabled()) return 0;

		int enchantLvl = EnchantManager.getEnchantLevel(pitPlayer.player, INSTANCE);
		if(enchantLvl == 0) return 0;

		return getWalkSpeedIncrease(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Move &e" + Misc.roundString(getWalkSpeedIncrease(enchantLvl)) + "&e% faster &7at all times"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that increases " +
				"your movement speed";
	}

	public static int getWalkSpeedIncrease(int enchantLvl) {
		return enchantLvl * 5 + 5;
	}
}
