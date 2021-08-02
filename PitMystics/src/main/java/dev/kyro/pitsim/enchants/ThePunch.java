package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import java.util.List;

public class ThePunch extends PitEnchant {

	public ThePunch() {
		super("The Punch", true, ApplyType.NONE,
				"thepunch", "the-punch", "punch", "yeet");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(attackEvent.attacker, getCooldown(enchantLvl));
		if(cooldown.isOnCooldown()) return; else cooldown.reset();

//		BypassManager.bypassExplosive.add(attackEvent.defender);
//		new BukkitRunnable() {
//			@Override
//			public void run() {
//				BypassManager.bypassExplosive.remove(attackEvent.defender);
//			}
//		}.runTaskLater(PitSim.INSTANCE, 40L);

		Vector newVelo = attackEvent.defender.getVelocity().setY(2);
		attackEvent.defender.setVelocity(new Vector(0, 1, 0));
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Hitting a player launches them up", "&7in the air (20s cooldown)").getLore();
	}

	public static int getCooldown(int enchantLvl) {

		return 400;
	}
}
