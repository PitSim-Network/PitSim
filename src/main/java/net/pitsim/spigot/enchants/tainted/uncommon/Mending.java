package net.pitsim.spigot.enchants.tainted.uncommon;

import net.pitsim.spigot.controllers.EnchantManager;
import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.enums.ApplyType;
import net.pitsim.spigot.misc.PitLoreBuilder;
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
		if(!INSTANCE.isEnabled()) return 1;

		int enchantLvl = EnchantManager.getEnchantLevel(player, INSTANCE);
		if(enchantLvl == 0) return 1;

		return 1 + (getIncreasePercent(enchantLvl) / 100.0);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Your shield regenerates &9" + getIncreasePercent(enchantLvl) + "% &7faster when active"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"increases the speed at which your shield recovers while active";
	}

	public static int getIncreasePercent(int enchantLvl) {
		return enchantLvl * 12;
	}
}
