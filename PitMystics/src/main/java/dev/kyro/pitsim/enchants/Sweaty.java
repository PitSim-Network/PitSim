package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.megastreaks.NoMegastreak;
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
			killEvent.xpCap += getCapIncrease(enchantLvl);
			int megaKills = (int) (pitPlayer.getKills() - pitPlayer.megastreak.getRequiredKills());
			int xp = megaKills * getXpIncrease(enchantLvl) / 2;
			killEvent.xpReward += xp;
		}
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		if(enchantLvl == 3) return new ALoreBuilder("&7Gain &b+" + getCapIncrease(enchantLvl) + " max xp&7. When combined",
				"&7with another &bSweaty III &7item, earn", "&7a stacking &b+" + getXpIncrease(6) + " xp &7every other kill").getLore();
		return new ALoreBuilder("&7Gain &b+" + getCapIncrease(enchantLvl) + " max xp").getLore();

//		if(enchantLvl != 3) {
//			return new ALoreBuilder("&7Earn a stacking &b+1 XP &7every",
//					" &7kill and &b+" + getCapIncrease(enchantLvl) + " max XP &7per kill", "&7(Must be on a megastreak)").getLore();
//		} else {
//			return new ALoreBuilder("&7Earn a stacking &b+1 XP &7every",
//					"&7kill and &b+" + getCapIncrease(enchantLvl) + " max XP &7per kill", "&7(Must be on a megastreak)").getLore();
//		}
	}

	public int getXpIncrease(int enchantLvl) {
		return enchantLvl == 6 ? 1 : 0;
	}

	public int getCapIncrease(int enchantLvl) {
		return enchantLvl * 15;

//		switch(enchantLvl) {
//			case 1:
//				return 10;
//			case 2:
//				return 20;
//			case 3:
//				return 35;
//			case 4:
//				return 45;
//			case 5:
//				return 55;
//			case 6:
//				return 70;
//		}
//		return 0;
	}
}
