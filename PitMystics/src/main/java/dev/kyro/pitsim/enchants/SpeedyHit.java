package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class SpeedyHit extends PitEnchant {

	public SpeedyHit() {
		super("Speedy Hit", true, ApplyType.MELEE,
				"speedyhit", "speedy", "speed", "sh", "speedy-hit");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.attackerIsPlayer) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(attackEvent.attackerPlayer, (getCooldown(enchantLvl) * 20));
		if(cooldown.isOnCooldown()) return;
		else cooldown.restart();

		Misc.applyPotionEffect(attackEvent.attacker, PotionEffectType.SPEED, getDuration(enchantLvl) * 20, getAmplifier(enchantLvl), true, false);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Gain Speed " + AUtil.toRoman(getAmplifier(enchantLvl) + 1) + " for &e" + getDuration(enchantLvl) + "s &7on hit (" +
				getCooldown(enchantLvl) + "s", "&7cooldown)").getLore();
	}

	public int getAmplifier(int enchantLvl) {

		return Misc.linearEnchant(enchantLvl, 0.5, 0);
	}

	public int getDuration(int enchantLvl) {

		return enchantLvl * 2 + 3;
	}

	public int getCooldown(int enchantLvl) {

		return Math.max(4 - enchantLvl, 1);
	}
}
