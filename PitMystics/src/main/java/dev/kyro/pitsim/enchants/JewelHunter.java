package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;

import java.util.List;

public class JewelHunter extends PitEnchant {

	public JewelHunter() {
		super("Jewel Hunter", false, ApplyType.SWORDS,
				"jewelhunter", "jewel-hunter", "hunter");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		int jewels = 0;

		if(!Misc.isAirOrNull(attackEvent.defender.getItemInHand()) && EnchantManager.isJewelComplete(attackEvent.defender.getItemInHand())) jewels++;
		if(!Misc.isAirOrNull(attackEvent.defender.getInventory().getLeggings()) &&
				EnchantManager.isJewelComplete(attackEvent.defender.getInventory().getLeggings())) jewels++;

		attackEvent.increasePercent += jewels * (getDamage(enchantLvl) / 100D);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deal &c+" + getDamage(enchantLvl) + "% &7melee damage", "&7per jewel item your victim is using").getLore();
	}

	public int getDamage(int enchantLvl) {

		return enchantLvl * 5 + 5;
	}
}
