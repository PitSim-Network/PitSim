package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;

public class TrueShot extends PitEnchant {

	public TrueShot() {
		super("True Shot", true, ApplyType.BOWS,
				"trueshot", "true");
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		double damage = attackEvent.getFinalDamageIncrease();
		attackEvent.trueDamage += damage * (getPercent(enchantLvl) / 100.0);
		attackEvent.multipliers.add(Misc.getReductionMultiplier(getPercent(enchantLvl)));
	}

	@Override
	public List<String> getDescription(int enchantLvl) {
		if(enchantLvl == 1) {
			return new ALoreBuilder("&7Deal &c" + getPercent(enchantLvl) + "% &7of arrow damage as",
					"&7true damage (ignores armor)").getLore();
		}
		return new ALoreBuilder("&7Deal &c" + getPercent(enchantLvl) + "% + " + Misc.getHearts(getBase(enchantLvl)) + " &7of arrow",
				"&7damage as true damage (ignores", "&7armor)").getLore();
	}

	public int getPercent(int enchantLvl) {
		return Math.min(enchantLvl * 10, 100) + 15;
	}

	public double getBase(int enchantLvl) {
		return enchantLvl * 0.5 - 0.5;
	}
}