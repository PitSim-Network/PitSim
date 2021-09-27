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

public class LastStand extends PitEnchant {

	public LastStand() {
		super("Last Stand", false, ApplyType.PANTS,
				"laststand", "last", "last-stand", "resistance");
	}

	@EventHandler
	public void onAttack(AttackEvent.Post attackEvent	) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(attackEvent.defender, 10 * 20);
		if(cooldown.isOnCooldown()) return; else cooldown.reset();

		if(attackEvent.defender.getHealth() - attackEvent.event.getFinalDamage() <= getProcHealth()) {
			Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.DAMAGE_RESISTANCE, getSeconds(enchantLvl)
					* 20, getAmplifier(enchantLvl) - 1, false, false);
		}
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Gain &9Resistance " + AUtil.toRoman(getAmplifier(enchantLvl)) + " &7("
		+ getSeconds(enchantLvl) + " &7seconds)", "&7when reaching &c3\u2764").getLore();
	}

	public int getProcHealth() {

		return 8;
	}

	public int getAmplifier(int enchantLvl) {

		return enchantLvl;
	}

	public int getSeconds(int enchantLvl) {
		return Misc.linearEnchant(enchantLvl, 0.5, 3);
	}
}
