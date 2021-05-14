package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.controllers.PitPlayer;

import java.util.List;

public class PushComesToShove extends PitEnchant {

	public PushComesToShove() {
		super("Push comes to shove", false, ApplyType.BOWS,
				"pushcomestoshove", "push-comes-to-shove", "pcts");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {

		if(attackEvent.arrow == null) return;
		int enchantLvl = attackEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.attacker);
		HitCounter.incrementCounter(pitPlayer.player, this);

		if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, 3)) return;

		Vector velocity = attackEvent.arrow.getVelocity().normalize().multiply(getPunchLevel(enchantLvl) * 2.35);
		velocity.setY(0);

		attackEvent.defender.setVelocity(velocity);
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
