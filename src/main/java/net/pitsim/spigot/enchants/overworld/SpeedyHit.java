package net.pitsim.spigot.enchants.overworld;

import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.controllers.Cooldown;
import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.enums.ApplyType;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.PitLoreBuilder;
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
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(attackEvent.getAttackerPlayer(), (getCooldown(enchantLvl) * 20));
		if(cooldown.isOnCooldown()) return;
		else cooldown.restart();

		Misc.applyPotionEffect(attackEvent.getAttacker(), PotionEffectType.SPEED, getDuration(enchantLvl) * 20, getAmplifier(enchantLvl), true, false);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Gain &eSpeed " + AUtil.toRoman(getAmplifier(enchantLvl) + 1) + " &7for " +
				getDuration(enchantLvl) + "s &7on hit (" + getCooldown(enchantLvl) + "s cooldown)"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that gives you " +
				"speed when you hit your opponent";
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
