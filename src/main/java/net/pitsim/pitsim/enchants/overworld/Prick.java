package net.pitsim.pitsim.enchants.overworld;

import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.AttackEvent;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Prick extends PitEnchant {
	public Prick INSTANCE;

	public Prick() {
		super("Prick", false, ApplyType.PANTS,
				"prick", "thorns");
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;

		attackEvent.selfTrueDamage += getDamage(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Enemies hitting you receive &c" + Misc.getHearts(getDamage(enchantLvl)) + " &7true damage"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that does " +
				"true damage to anyone who attacks you";
	}

	public double getDamage(int enchantLvl) {
		return enchantLvl * 0.2 + 0.2;
	}
}
