package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.KillEvent;
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

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(killEvent.killerPlayer);
		killEvent.xpCap += getCapIncrease(enchantLvl);
		int xp = (int) ((pitPlayer.getKills() / 10) * getXpIncrease(enchantLvl));
		killEvent.xpReward += xp;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		return new ALoreBuilder("&7Gain &b+" + getCapIncrease(enchantLvl) + " max XP &7on kill. Also",
				"&7gain &b+" + decimalFormat.format(getXpIncrease(enchantLvl)) + " XP &7on kill per 10", "&7killstreak").getLore();
	}

//	Has to be proportional
	public double getXpIncrease(int enchantLvl) {
		return enchantLvl * 1.0;
	}

	public int getCapIncrease(int enchantLvl) {
		return enchantLvl * 15;
	}
}
