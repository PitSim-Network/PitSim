package dev.kyro.pitsim.enchants.tainted.uncommon;

import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.List;

public class ShieldBuster extends PitEnchant {
	public static ShieldBuster INSTANCE;

	public ShieldBuster() {
		super("Shield Buster", false, ApplyType.SCYTHES,
				"shieldbuster", "buster");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	public static double getMultiplier(Player player) {
		int enchantLvl = EnchantManager.getEnchantLevel(player, INSTANCE);
		if(enchantLvl == 0) return 1;
		return getMultiplier(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		return new PitLoreBuilder(
				"&7Your attacks deal &c" + decimalFormat.format(getMultiplier(enchantLvl)) + "x &7damage to other player's shields"
		).getLore();
	}

	public static double getMultiplier(int enchantLvl) {
		return enchantLvl * 0.5 + 1.5;
	}
}
