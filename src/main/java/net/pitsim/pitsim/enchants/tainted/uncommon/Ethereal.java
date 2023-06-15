package net.pitsim.pitsim.enchants.tainted.uncommon;

import net.pitsim.pitsim.controllers.EnchantManager;
import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.HealEvent;
import net.pitsim.pitsim.events.KillEvent;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import net.pitsim.pitsim.misc.PlayerItemLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Ethereal extends PitEnchant {
	public static Ethereal INSTANCE;

	public Ethereal() {
		super("Ethereal", false, ApplyType.CHESTPLATES,
				"ethereal", "eth");
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

		healEvent.multipliers.add(1 + (getHealingPercent(enchantLvl) / 100.0));
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Heal &c+" + getHealingPercent(enchantLvl) + "% &7from all sources, " +
						"but lose &c+1 life &7on this item when you die"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"heals you for more from all sources, but loses additional lives when you die";
	}

	public static int getHealingPercent(int enchantLvl) {
		if(enchantLvl == 1) return 15;
		return enchantLvl * 9 + 3;
	}
}
