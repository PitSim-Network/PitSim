package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Guts extends PitEnchant {

	public Guts() {
		super("Guts", false, ApplyType.SWORDS,
				"guts", "gut");
	}

	@EventHandler
	public void onAttack(KillEvent killEvent) {

		int enchantLvl = killEvent.getKillerEnchantLevel(this);
		if(enchantLvl == 0) return;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(killEvent.killer);
		pitPlayer.heal(getHealing(enchantLvl));
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Heal &c+" + Misc.getHearts(getHealing(enchantLvl)) + " &7on kill").getLore();
	}

	public double getHealing(int enchantLvl) {
		return enchantLvl * 0.5 + 0.5;
	}
}
