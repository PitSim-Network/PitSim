package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.BypassManager;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.killstreaks.Uberstreak;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class Pullbow extends PitEnchant {

	public Pullbow() {
		super("Pullbow", true, ApplyType.BOWS,
				"pullbow", "pull");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;
		Bukkit.broadcastMessage("test");

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		Bukkit.broadcastMessage("test2");

		Cooldown cooldown = getCooldown(attackEvent.attacker, 160);
		if(cooldown.isOnCooldown()) return; else cooldown.reset();

		Bukkit.broadcastMessage("test3");

//		if(BypassManager.bypassPullbow.contains(attackEvent.defender)) {
//			BypassManager.bypassPullbow.remove(attackEvent.defender);
//			BypassManager.bypassPullbow.add(attackEvent.defender);
//		}
//		else BypassManager.bypassPullbow.add(attackEvent.defender);

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.defender);
		if(pitPlayer.megastreak.getClass() == Uberstreak.class && pitPlayer.megastreak.isOnMega()) return;
		Bukkit.broadcastMessage("test4");
		Vector dirVector = attackEvent.attacker.getLocation().toVector().subtract(attackEvent.defender.getLocation().toVector()).setY(0);
		Vector pullVector = dirVector.clone().normalize().setY(0.5).multiply(2.5).add(dirVector.clone().multiply(0.03));
		Bukkit.broadcastMessage("test5");
		attackEvent.defender.setVelocity(pullVector.multiply(getMultiplier(enchantLvl)));
		Bukkit.broadcastMessage("test6");

//		new BukkitRunnable() {
//			@Override
//			public void run() {
//				BypassManager.bypassPullbow.remove(attackEvent.defender);
//			}
//		}.runTaskLater(PitSim.INSTANCE, 40L);

	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Hitting a player pulls them toward", "&7you (8s cooldown)").getLore();
	}

	public static double getMultiplier(int enchantLvl) {

		return (enchantLvl * 0.2) + 1.15;
	}
}
