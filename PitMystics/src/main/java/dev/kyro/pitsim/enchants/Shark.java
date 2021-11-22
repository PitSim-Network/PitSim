package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Shark extends PitEnchant {

	public Shark() {
		super("Shark", false, ApplyType.SWORDS,
				"shark");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		List<Entity> entityList = attackEvent.attacker.getNearbyEntities(7, 7, 7);
		int nearby = 0;

		for(Entity entity : entityList) {
			if(entity instanceof Player && ((Player) entity).getHealth() < 10) nearby++;
		}

		double increasePercent = (getDamage(enchantLvl) / 100D) * nearby;
		increasePercent = Math.min(increasePercent, (getCap(enchantLvl) / 100D));
		attackEvent.increasePercent += increasePercent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deal &c+" + getDamage(enchantLvl) + "% &7damage per other",
				"&7player below &c5\u2764 &7within 7", "&7blocks (&c+" + getCap(enchantLvl) + "% &7max)").getLore();
	}

	public int getDamage(int enchantLvl) {
		return (int) (Math.pow(enchantLvl, 1.2)  * 2);
	}

	public double getCap(int enchantLvl) {
		return enchantLvl * 10;
	}
}
