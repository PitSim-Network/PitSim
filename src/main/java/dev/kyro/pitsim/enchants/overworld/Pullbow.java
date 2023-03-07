package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.megastreaks.RNGesus;
import dev.kyro.pitsim.megastreaks.Uberstreak;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class Pullbow extends PitEnchant {

	public Pullbow() {
		super("Pullbow", true, ApplyType.BOWS,
				"pullbow", "pull");
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onAttack(AttackEvent.Pre attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(attackEvent.getAttacker() == attackEvent.getDefender()) return;

		Cooldown cooldown = getCooldown(attackEvent.getAttackerPlayer(), 20 * 8);
		if(cooldown.isOnCooldown()) return;
		else cooldown.restart();

		if(attackEvent.isDefenderPlayer()) {
			PitPlayer pitDefender = attackEvent.getDefenderPitPlayer();
			if(pitDefender.megastreak.isOnMega()) {
				if(pitDefender.megastreak instanceof Uberstreak || pitDefender.megastreak instanceof RNGesus) return;
			}
		}

		Vector distanceVector = attackEvent.getAttacker().getLocation().subtract(attackEvent.getDefender().getLocation()).toVector().setY(0);
		double distance = Math.min(distanceVector.length(), getCapDistance(enchantLvl));
		Vector horizontalVelocity = distanceVector.clone().normalize().multiply(distance * 0.16);
		double yComponent = Math.min(distance * 0.02 + 0.23, 0.65);
		Vector finalVelocity = horizontalVelocity.clone().setY(yComponent);

		attackEvent.setCancelled(true);
		attackEvent.getArrow().remove();
		attackEvent.getDefender().damage(0);
		new BukkitRunnable() {
			@Override
			public void run() {
				attackEvent.getDefender().setVelocity(finalVelocity);
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);

		Sounds.PULLBOW.play(attackEvent.getAttackerPlayer());
		PitPlayer pitAttacker = PitPlayer.getPitPlayer(attackEvent.getAttackerPlayer());
		if(pitAttacker.stats != null) pitAttacker.stats.pullbow++;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Hitting a player pulls them to you (8s cooldown). Effect caps at &e" + getCapDistance(enchantLvl) +
						" block" + (getCapDistance(enchantLvl) == 1 ? "" : "s") + " &7of distance"
		).getLore();
	}

	public static int getCapDistance(int enchantLvl) {
		return enchantLvl * 5 + 10;
	}
}
