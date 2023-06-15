package net.pitsim.pitsim.enchants.tainted.uncommon;

import net.pitsim.pitsim.controllers.PlayerManager;
import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.KillEvent;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Hoarder extends PitEnchant {
	public static Hoarder INSTANCE;

	public Hoarder() {
		super("Hoarder", false, ApplyType.CHESTPLATES,
				"hoarder", "hoard");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!PlayerManager.isRealPlayer(killEvent.getDeadPlayer())) return;

		int enchantLvl = killEvent.getKillerEnchantLevel(this);
		if(enchantLvl == 0) return;

		killEvent.soulMultipliers.add(Misc.getReductionMultiplier(getDecreasePercent(enchantLvl)));
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Lose &f-" + getDecreasePercent(enchantLvl) + "% &7souls on death"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"decreases the amount of &fsouls &7that you lose on death";
	}

	public int getDecreasePercent(int enchantLvl) {
		return enchantLvl * 10;
	}
}
