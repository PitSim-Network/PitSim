package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.event.EventHandler;

import java.util.List;

public class KingBuster extends PitEnchant {

	public KingBuster() {
		super("King Buster", false, ApplyType.SWORDS,
				"kb", "kingbuster", "kbuster", "king-buster");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {

		int enchantLvl = attackEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(attackEvent.defender.getHealth() / attackEvent.defender.getMaxHealth() < 0.5) return;
		attackEvent.increasePercent += getDamage(enchantLvl) / 100D;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deal &c+" + getDamage(enchantLvl) + "% &7damage vs. players", "&7above 50% HP").getLore();
	}

	public int getDamage(int enchantLvl) {

		return (int) (Math.floor(Math.pow(enchantLvl, 1.5) * 3.2) + 4);
	}
}