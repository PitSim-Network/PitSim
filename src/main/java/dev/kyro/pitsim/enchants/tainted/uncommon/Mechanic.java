package dev.kyro.pitsim.enchants.tainted.uncommon;

import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.List;

public class Mechanic extends PitEnchant {
	public static Mechanic INSTANCE;

	public Mechanic() {
		super("Mechanic", false, ApplyType.CHESTPLATES,
				"mechanic");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	public static int getDecreaseTicks(Player player) {
		int enchantLvl = EnchantManager.getEnchantLevel(player, INSTANCE);
		if(enchantLvl == 0) return 0;

		return getDecreaseTicks(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		return new PitLoreBuilder(
				"&7Decreases the time until your shield recharges after breaking by &9" +
						decimalFormat.format(getDecreaseTicks(enchantLvl)) +
						" &7second" + (getDecreaseTicks(enchantLvl) == 1 ? "" : "s")
		).getLore();
	}

	public static int getDecreaseTicks(int enchantLvl) {
		return enchantLvl * 4 + 8;
	}
}
