package dev.kyro.pitsim.enchants.tainted.uncommon;

import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.PitLoreBuilder;
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

	public static int getSoulChanceIncrease(int enchantLvl) {
		return enchantLvl * 16 + 16;
	}
}
