package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Formatter;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Parasite extends PitEnchant {

	public Parasite() {
		super("Parasite", false, ApplyType.BOWS,
				"parasite", "para");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(attackEvent.getAttackerPlayer(), getCooldownTicks(enchantLvl));
		if(cooldown.isOnCooldown()) return;
		else cooldown.restart();

		PitPlayer pitAttacker = attackEvent.getAttackerPitPlayer();
		pitAttacker.heal(getHealing(enchantLvl));
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		double seconds = getCooldownTicks(enchantLvl) / 20.0;
		return new PitLoreBuilder(
				"&7Heal &c" + Misc.getHearts(getHealing(enchantLvl)) + " &7on arrow hit (" +
						Formatter.decimalCommaFormat.format(seconds) + "s cooldown)"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that heals you " +
				"when you land bow shots";
	}

	public double getHealing(int enchantLvl) {
		if(enchantLvl == 1) return 1;
		return enchantLvl - 0.5;
	}

	public int getCooldownTicks(int enchantLvL) {
		return 30;
	}
}
