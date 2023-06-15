package net.pitsim.pitsim.enchants.overworld;

import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.KillEvent;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.text.DecimalFormat;
import java.util.List;

public class Sweaty extends PitEnchant {

	public Sweaty() {
		super("Sweaty", false, ApplyType.ALL,
				"sweaty", "sweaty", "sw");
		isUncommonEnchant = true;
		levelStacks = true;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer()) return;
		int enchantLvl = killEvent.getKillerEnchantLevel(this);
		if(enchantLvl == 0) return;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(killEvent.getKillerPlayer());
		killEvent.xpCap += getCapIncrease(enchantLvl);
		int xp = (int) ((pitPlayer.getKills() / 10) * getXpIncrease(enchantLvl));
		killEvent.xpReward += xp;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		return new PitLoreBuilder(
				"&7Gain &b+" + getCapIncrease(enchantLvl) + " max XP &7on kill. Also gain &b+" +
				decimalFormat.format(getXpIncrease(enchantLvl)) + " XP &7on kill per 10 killstreak"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that increases " +
				"the &bXP &7and &bmax XP &7you get on kill";
	}

//	Has to be proportional
	public double getXpIncrease(int enchantLvl) {
		return enchantLvl * 1.0;
	}

	public int getCapIncrease(int enchantLvl) {
		return enchantLvl * 15;
	}
}
