package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Electrolytes extends PitEnchant {

	public Electrolytes() {
		super("Electrolytes", false, ApplyType.PANTS,
				"electrolytes", "electrolyte", "electro", "elec");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer() || !killEvent.getKiller().hasPotionEffect(PotionEffectType.SPEED)) return;

		int enchantLvl = killEvent.getKillerEnchantLevel(this);
		if(enchantLvl == 0) return;

		for(PotionEffect potionEffect : killEvent.getKiller().getActivePotionEffects()) {
			if(!potionEffect.getType().equals(PotionEffectType.SPEED)) continue;

			double multiplier = potionEffect.getAmplifier() > 0 ? 0.5 : 1;
			int ticks = (int) Math.min(potionEffect.getDuration() + (getSeconds(enchantLvl) * 20) * multiplier, getMaxSeconds() * 20);
			if(potionEffect.getAmplifier() > 0) ticks *= 0.5;
			Misc.applyPotionEffect(killEvent.getKiller(), PotionEffectType.SPEED, ticks,
					potionEffect.getAmplifier(), false, false);
			break;
		}
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7If you have &eSpeed &7on kill, add &e" + getSeconds(enchantLvl) +
				" &7seconds to its duration. (Halved for Speed II+, Max " + getMaxSeconds() + "s)"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that can extend " +
				"the duration that you have &eSpeed &7for. This enchant " +
				"pairs well with sources of &eSpeed IV";
	}

	public int getSeconds(int enchantLvl) {
		return enchantLvl * 2 - 1;
	}

	public int getMaxSeconds() {
		return 8;
	}
}
