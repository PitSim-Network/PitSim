package net.pitsim.pitsim.enchants.tainted.uncommon;

import net.pitsim.pitsim.controllers.EnchantManager;
import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.misc.PitLoreBuilder;
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
		if(!INSTANCE.isEnabled()) return 1;

		int enchantLvl = EnchantManager.getEnchantLevel(player, INSTANCE);
		if(enchantLvl == 0) return 1;

		return 1 + (getIncrease(enchantLvl) / 100.0);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		return new PitLoreBuilder(
				"&7Your attacks deal &c+" + getIncrease(enchantLvl) + "% &7damage to other player's shields"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"significantly increases the damage you do to other players' shields";
	}

	public static int getIncrease(int enchantLvl) {
		if(enchantLvl == 1) return 30;
		return enchantLvl * 30 - 20;
	}
}
