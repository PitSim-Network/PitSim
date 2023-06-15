package net.pitsim.pitsim.enchants.tainted.uncommon;

import net.pitsim.pitsim.controllers.PlayerManager;
import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.KillEvent;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Greed extends PitEnchant {
	public static Greed INSTANCE;

	public Greed() {
		super("Greed", false, ApplyType.SCYTHES,
				"greed", "greedy");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!PlayerManager.isRealPlayer(killEvent.getKillerPlayer()) || !PlayerManager.isRealPlayer(killEvent.getDeadPlayer())) return;

		int enchantLvl = killEvent.getKillerEnchantLevel(this);
		if(enchantLvl == 0) return;

		killEvent.soulMultipliers.add(1 + (getIncreasePercent(enchantLvl) / 100.0));
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Gain &f+" + getIncreasePercent(enchantLvl) + "% &7souls &7from kills on other players"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"increases the amount of &fsouls &7that you get from killing other players";
	}

	public int getIncreasePercent(int enchantLvl) {
		return enchantLvl * 8 + 10;
	}
}