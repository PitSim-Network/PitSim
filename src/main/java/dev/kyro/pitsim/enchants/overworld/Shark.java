package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.enums.PitEntityType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Shark extends PitEnchant {

	public Shark() {
		super("Shark", false, ApplyType.MELEE,
				"shark");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		int nearby = 0;
		for(Entity nearbyEntity : attackEvent.getAttacker().getNearbyEntities(7, 7, 7)) {
			if(!Misc.isEntity(nearbyEntity, PitEntityType.NON, PitEntityType.REAL_PLAYER)) continue;
			LivingEntity livingEntity = (LivingEntity) nearbyEntity;
			if(livingEntity.getHealth() >= 10) continue;
			nearby++;
		}

		double increasePercent = getDamage(enchantLvl) * nearby;
		increasePercent = Math.min(increasePercent, getCap(enchantLvl));
		attackEvent.increasePercent += increasePercent;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Deal &c+" + getDamage(enchantLvl) + "% &7damage per other player below &c" + Misc.getHearts(getHealthThreshold()) +
				" &7within 7 " + "blocks (&c+" + getCap(enchantLvl) + "% &7max)"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that does more damage " +
				"when there are other players or bots nearby that have low health";
	}

	public double getHealthThreshold() {
		return 10;
	}

	public int getDamage(int enchantLvl) {
		return (int) (Math.pow(enchantLvl, 1.2) * 2);
	}

	public double getCap(int enchantLvl) {
		return enchantLvl * 10 + 5;
	}
}
