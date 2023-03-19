package dev.kyro.pitsim.enchants.tainted.uncommon;

import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.wrappers.PlayerItemLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	public void onKill(KillEvent killEvent) {
		for(Map.Entry<PlayerItemLocation, KillEvent.ItemInfo> entry : new ArrayList<>(killEvent.getVulnerableItems().entrySet())) {
			KillEvent.ItemInfo itemInfo = entry.getValue();
			ItemStack itemStack = itemInfo.itemStack;
			if(!itemInfo.pitItem.isMystic) continue;

			int enchantLvl = EnchantManager.getEnchantLevel(itemStack, this);
			if(enchantLvl == 0) continue;

			entry.getValue().livesToLose++;
		}
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

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"heals you for more from all sources, but loses additional lives when you die";
	}

	public static double getHealingMultiplier(int enchantLvl) {
		return enchantLvl * 0.4 + 0.8;
	}
}
