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

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Increase your max health by &c" + Misc.getHearts(getExtraHealth(enchantLvl))
		).getLore();
	}

	public int getExtraHealth(int enchantLvl) {
		return enchantLvl * 4;
	}

	public int getExtraHealth(Map<PitEnchant, Integer> enchantMap) {
		if(!enchantMap.containsKey(this)) return 0;
		int enchantLvl = enchantMap.get(this);

		return getExtraHealth(enchantLvl);
	}
}
