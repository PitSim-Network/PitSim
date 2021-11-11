package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.HealEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;

public class Sufferance extends PitEnchant {

	public Sufferance() {
		super("Sufferance", false, ApplyType.PANTS,
				"sufferance", "suffer", "sufference");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int defenderLvl = attackEvent.getDefenderEnchantLevel(this);
		if(defenderLvl == 0) return;

		PitPlayer pitDefender = PitPlayer.getPitPlayer(attackEvent.defender);
		pitDefender.heal((attackEvent.trueDamage) * getReductionPercent(defenderLvl) / 100D,
				HealEvent.HealType.ABSORPTION, 4);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Convert &e" + getReductionPercent(enchantLvl) + "% &7of true damage",
				"&7taken into &6absorption").getLore();
	}

	public static int getReductionPercent(int enchantLvl) {
		return enchantLvl * 20;
	}
}
