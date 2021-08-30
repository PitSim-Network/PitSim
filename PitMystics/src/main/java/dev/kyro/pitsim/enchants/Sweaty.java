package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.killstreaks.NoMegastreak;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;

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
		int enchantLvl = killEvent.getKillerEnchantLevel(this);
		if(enchantLvl == 0) return;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(killEvent.killer);
		if(pitPlayer.megastreak.getClass() != NoMegastreak.class && pitPlayer.megastreak.isOnMega()) {
			killEvent.xpCap += getCapIncrese(enchantLvl);
			int xp = (int) Math.floor((pitPlayer.getKills() - pitPlayer.megastreak.getRequiredKills()) / getXpIncrease(enchantLvl));
			killEvent.xpReward += xp;
		}
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		if(enchantLvl != 3) {
			return new ALoreBuilder("&7Earn a stacking &b+1 XP &7every",
					" &7kill and &b+" + getCapIncrese(enchantLvl) + " max XP &7per kill", "&7(Must be on a megastreak)").getLore();
		} else {
			return new ALoreBuilder("&7Earn a stacking &b+1 XP &7every",
					"&7kill and &b+" + getCapIncrese(enchantLvl) + " max XP &7per kill", "&7(Must be on a megastreak)").getLore();
		}
	}

	public int getXpIncrease(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
			case 2:
			case 3:
				return 1;
			case 4:
			case 5:
			case 6:
				return 2;
		}
		return 0;
	}

	public int getCapIncrese(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 5;
			case 2:
				return 10;
			case 3:
				return 15;
			case 4:
				return 20;
			case 5:
				return 25;
			case 6:
				return 30;
		}
		return 0;
	}
}
