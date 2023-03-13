package dev.kyro.pitsim.enchants.tainted.uncommon;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;

import java.util.List;
import java.util.Map;

public class Tanky extends PitEnchant {
	public static Tanky INSTANCE;

	public Tanky() {
		super("Tanky", false, ApplyType.CHESTPLATES,
				"tanky", "tank");
		isUncommonEnchant = true;
		isTainted = true;
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
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"increases your &cmax hp";
	}

	public static int getExtraHealth(int enchantLvl) {
		return enchantLvl * 4;
	}
}
