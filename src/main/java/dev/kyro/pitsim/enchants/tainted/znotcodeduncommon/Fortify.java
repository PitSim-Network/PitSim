package dev.kyro.pitsim.enchants.tainted.znotcodeduncommon;

import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.entity.Player;

import java.util.List;

public class Fortify extends PitEnchant {
	public static Fortify INSTANCE;

	public Fortify() {
		super("Fortify", false, ApplyType.CHESTPLATES,
				"fortify");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	public static int getShieldIncrease(Player player) {
		int enchantLvl = EnchantManager.getEnchantLevel(player, INSTANCE);
		if(enchantLvl == 0) return 0;

		return getShieldIncrease(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7I can't be asked to code this"
		).getLore();
	}

	public static int getShieldIncrease(int enchantLvl) {
		return enchantLvl * 30 + 10;
	}
}
