package net.pitsim.spigot.enchants.overworld;

import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.enums.ApplyType;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.PitLoreBuilder;

import java.util.List;
import java.util.Map;

public class Hearts extends PitEnchant {
	public static Hearts INSTANCE;

	public Hearts() {
		super("Hearts", false, ApplyType.PANTS,
				"hearts", "heart", "health");
		INSTANCE = this;
	}

	public static int getExtraHealth(Map<PitEnchant, Integer> enchantMap) {
		if(!INSTANCE.isEnabled()) return 0;

		if(!enchantMap.containsKey(INSTANCE)) return 0;
		int enchantLvl = enchantMap.get(INSTANCE);

		return getExtraHealth(enchantLvl);
	}
	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Increase your max health by &c" + Misc.getHearts(getExtraHealth(enchantLvl))
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that increases your &cmax hp";
	}

	public static int getExtraHealth(int enchantLvl) {
		return enchantLvl * 2;
	}
}
