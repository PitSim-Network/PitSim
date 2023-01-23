package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.megastreaks.Uberstreak;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import java.util.List;

public class PushComesToShove extends PitEnchant {

	public PushComesToShove() {
		super("Push comes to shove", false, ApplyType.BOWS,
				"pushcomestoshove", "push-comes-to-shove", "pcts");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		if(attackEvent.getArrow() == null) return;
		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;
		PitPlayer pitAttacker = attackEvent.getAttackerPitPlayer();

		HitCounter.incrementCounter(pitAttacker.player, this);
		if(!HitCounter.hasReachedThreshold(pitAttacker.player, this, 3)) return;

		Cooldown cooldown = getCooldown(attackEvent.getAttackerPlayer(), 200);
		if(cooldown.isOnCooldown()) return;
		else cooldown.restart();

		if(attackEvent.isDefenderPlayer()) {
			PitPlayer pitDefender = PitPlayer.getPitPlayer(attackEvent.getDefenderPlayer());
			if(pitDefender.megastreak instanceof Uberstreak && pitDefender.megastreak.isOnMega()) return;
		}

		Vector velocity = attackEvent.getArrow().getVelocity().normalize().multiply(getPunchMultiplier(enchantLvl) / 2.35);
		velocity.setY(0);

		attackEvent.getDefender().setVelocity(velocity);

		if(pitAttacker.stats != null) pitAttacker.stats.pcts++;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

		return new ALoreBuilder("&7Every 3rd shot on a player has",
				"&bPunch " + AUtil.toRoman(getPunchLevel(enchantLvl)) + " &7(5s cooldown)").getLore();
	}

	public int getPunchMultiplier(int enchantLvl) {

		return (int) Math.floor(Math.pow(enchantLvl, 0.67) * 22) - 10;
	}

	public int getPunchLevel(int enchantLvl) {

		return enchantLvl * 2 + 1;
	}
}
