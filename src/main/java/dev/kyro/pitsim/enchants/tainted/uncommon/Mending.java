package dev.kyro.pitsim.enchants.tainted.uncommon;

import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.entity.Player;

import java.util.List;

public class Mending extends PitEnchant {
	public static Mending INSTANCE;

	public Mending() {
		super("Mending", false, ApplyType.CHESTPLATES,
				"mending", "mend");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	public static double getIncreaseMultiplier(Player player) {
		int enchantLvl = EnchantManager.getEnchantLevel(player, INSTANCE);
		if(enchantLvl == 0) return 1;

		return 1 + (getIncreasePercent(enchantLvl) / 100.0);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Your shield regenerates &9" + getIncreasePercent(enchantLvl) + "% faster"
		).getLore();
	}

	public static int getIncreasePercent(int enchantLvl) {
		return enchantLvl * 10;
	}
}
