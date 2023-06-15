package net.pitsim.spigot.enchants.tainted.uncommon;

import net.pitsim.spigot.controllers.EnchantManager;
import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.enums.ApplyType;
import net.pitsim.spigot.misc.PitLoreBuilder;
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
		if(!INSTANCE.isEnabled()) return 0;

		int enchantLvl = EnchantManager.getEnchantLevel(player, INSTANCE);
		if(enchantLvl == 0) return 0;

		return getDecreaseTicks(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		double seconds = getDecreaseTicks(enchantLvl) / 20.0;
		return new PitLoreBuilder(
				"&7Decreases the time until your shield recharges after breaking by &9" +
						decimalFormat.format(seconds) + " &7second" + (seconds == 1 ? "" : "s")
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"decreases the amount of time it takes for your shield to repair after it breaks";
	}

	public static int getDecreaseTicks(int enchantLvl) {
		return enchantLvl * 10 + 10;
	}
}
