package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.megastreaks.Uberstreak;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import java.util.List;

public class Pullbow extends PitEnchant {

	public Pullbow() {
		super("Pullbow", true, ApplyType.BOWS,
				"pullbow", "pull");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(attackEvent.getAttacker() == attackEvent.getDefender()) return;

		Cooldown cooldown = getCooldown(attackEvent.getAttackerPlayer(), 160);
		if(cooldown.isOnCooldown()) return;
		else cooldown.restart();

		if(attackEvent.isDefenderPlayer()) {
			PitPlayer pitDefender = attackEvent.getDefenderPitPlayer();
			if(pitDefender.megastreak.isOnMega()) {
				if(pitDefender.megastreak instanceof Uberstreak || pitDefender.megastreak instanceof RNGesus) return;
			}
		}
		Vector dirVector = attackEvent.getAttacker().getLocation().toVector().subtract(attackEvent.getDefender().getLocation().toVector()).setY(0);
		Vector pullVector = dirVector.clone().normalize().setY(0.5).multiply(2.5).add(dirVector.clone().multiply(0.03));
		attackEvent.getDefender().setVelocity(pullVector.multiply(getMultiplier(enchantLvl)));

		Sounds.PULLBOW.play(attackEvent.getAttackerPlayer());
		PitPlayer pitAttacker = PitPlayer.getPitPlayer(attackEvent.getAttackerPlayer());
		if(pitAttacker.stats != null) pitAttacker.stats.pullbow++;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Hitting a player pulls them toward you (8s cooldown)"
		).getLore();
	}

	public static double getMultiplier(int enchantLvl) {
		return (enchantLvl * 0.2) + 1.15;
	}
}
