package dev.kyro.pitsim.enchants.tainted.znotcodeduncommon;

import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.text.DecimalFormat;
import java.util.List;

public class Ethereal extends PitEnchant {
	public static Ethereal INSTANCE;

	public Ethereal() {
		super("Ethereal", false, ApplyType.CHESTPLATES,
				"ethereal");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onHeal(HealEvent healEvent) {
		Player player = healEvent.player;

		int enchantLvl = EnchantManager.getEnchantLevel(player, this);
		if(enchantLvl == 0) return;

		healEvent.multipliers.add(getHealingMultiplier(enchantLvl));
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		return new PitLoreBuilder(
				"&7Heal &c" + decimalFormat.format(getHealingMultiplier(enchantLvl)) + "x &7from all sources, " +
						"but lose &c+1 life on this item when you die"
		).getLore();
	}

	public static double getHealingMultiplier(int enchantLvl) {
		return enchantLvl * 0.4 + 0.8;
	}
}
