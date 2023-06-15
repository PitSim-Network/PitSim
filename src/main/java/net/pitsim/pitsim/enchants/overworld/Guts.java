package net.pitsim.pitsim.enchants.overworld;

import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.KillEvent;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Guts extends PitEnchant {

	public Guts() {
		super("Guts", false, ApplyType.MELEE,
				"guts", "gut");
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer()) return;

		int enchantLvl = killEvent.getKillerEnchantLevel(this);
		if(enchantLvl == 0) return;

		PitPlayer pitPlayer = killEvent.getKillerPitPlayer();
		pitPlayer.heal(getHealing(enchantLvl));
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Heal &c+" + Misc.getHearts(getHealing(enchantLvl)) + " &7on kill"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that heals you on kill";
	}

	public double getHealing(int enchantLvl) {
		return enchantLvl * 0.5 + 0.5;
	}
}
