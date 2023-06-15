package net.pitsim.spigot.enchants.tainted.uncommon;

import net.pitsim.spigot.controllers.EnchantManager;
import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.enums.ApplyType;
import net.pitsim.spigot.misc.PitLoreBuilder;
import org.bukkit.entity.Player;

import java.util.List;

public class Reaper extends PitEnchant {
	public static Reaper INSTANCE;

	public Reaper() {
		super("Reaper", false, ApplyType.SCYTHES,
				"reaper");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	public static int getSoulChanceIncrease(Player killer) {
		if(!INSTANCE.isEnabled()) return 0;

		int enchantLvl = EnchantManager.getEnchantLevel(killer, INSTANCE);
		if(enchantLvl == 0) return 0;

		return getSoulChanceIncrease(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7On kill, &5+" + getSoulChanceIncrease(enchantLvl) + "% &7chance of harvesting souls from mobs you kill"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"increases the chance of harvesting &fsouls &7from mobs that you kill";
	}

	public static int getSoulChanceIncrease(int enchantLvl) {
		return enchantLvl * 8;
	}
}
