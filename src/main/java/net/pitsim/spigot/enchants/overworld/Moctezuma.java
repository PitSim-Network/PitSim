package net.pitsim.spigot.enchants.overworld;

import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.enums.ApplyType;
import net.pitsim.spigot.events.KillEvent;
import net.pitsim.spigot.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Moctezuma extends PitEnchant {

	public Moctezuma() {
		super("Moctezuma", false, ApplyType.ALL,
				"moctezuma", "moct", "moc");
		levelStacks = true;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		int enchantLvl = killEvent.getKillerEnchantLevel(this);
		if(enchantLvl == 0) return;

		killEvent.goldReward += getGoldIncrease(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Earn &6+" + getGoldIncrease(enchantLvl) + "g &7on kill (assists &7excluded)"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that increases " +
				"the gold you get on kill";
	}

	public int getGoldIncrease(int enchantLvl) {
		return enchantLvl * 6;
	}
}
