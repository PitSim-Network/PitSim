package dev.kyro.pitsim.enchants.tainted.uncommon;

import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.entity.Player;

import java.util.List;

public class Resilient extends PitEnchant {
	public static Resilient INSTANCE;

	public Resilient() {
		super("Resilient", false, ApplyType.CHESTPLATES,
				"resilient", "resilent", "resileint");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	public static int getRegenIncrease(Player player) {
		int enchantLvl = EnchantManager.getEnchantLevel(player, INSTANCE);
		if(enchantLvl == 0) return 0;

		return getRegenIncrease(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Passively regenerate health &c+" + getRegenIncrease(enchantLvl) + "% &7faster. When worn, regain mana &b" +
						getManaReduction(enchantLvl) + "% &7slower"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"causes you to regenerate &bmana &7faster when worn";
	}

	public static int getRegenIncrease(int enchantLvl) {
		return enchantLvl * 25;
	}

	public static int getManaReduction(int enchantLvl) {
		return 50;
	}
}
