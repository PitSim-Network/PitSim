package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Lifesteal extends PitEnchant {

	public Lifesteal() {
		super("Lifesteal", false, ApplyType.SWORDS,
				"ls", "lifesteal", "life");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canAttack(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		double damage = attackEvent.getFinalDamage();

		if(attackEvent.attacker.getHealth() > attackEvent.attacker.getMaxHealth() - damage * (getHealing(enchantLvl) / 100D)) {
			attackEvent.attacker.setHealth(attackEvent.attacker.getMaxHealth());
		} else {
			attackEvent.attacker.setHealth(attackEvent.attacker.getHealth() + damage * (getHealing(enchantLvl) / 100D));
		}

	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deal &c+" + getHealing(enchantLvl) + "% &7damage vs. players", "&7above 50% HP").getLore();
	}

	public double getHealing(int enchantLvl) {

		return (int) (Math.pow(enchantLvl, 1.1) * 4);
	}
}
