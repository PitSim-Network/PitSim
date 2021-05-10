package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitremake.controllers.*;
import dev.kyro.pitremake.enums.ApplyType;
import org.bukkit.util.Vector;

import java.util.List;

public class PushComesToShove extends PitEnchant {

	public PushComesToShove() {
		super("Push comes to shove", false, ApplyType.BOWS,
				"pushcomestoshove", "push-comes-to-shove", "pcts");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		if(damageEvent.arrow == null) return damageEvent;
		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(damageEvent.attacker);
		HitCounter.incrementCounter(pitPlayer.player, this);

		if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, 3)) return damageEvent;

		Vector velocity = damageEvent.arrow.getVelocity().normalize().multiply(getPunchLevel(enchantLvl) * 2.35);
		velocity.setY(0);

		damageEvent.defender.setVelocity(velocity);

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Every 3rd shot on a player has",
				"&bPunch " + AUtil.toRoman(getPunchLevel(enchantLvl))).getLore();
	}

	public int getPunchMultiplier(int enchantLvl) {

		return (int) Math.floor(Math.pow(enchantLvl, 0.67) * 22) - 10;
	}

	public int getPunchLevel(int enchantLvl) {

		return enchantLvl * 2 + 1;
	}
}
