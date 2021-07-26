package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.BypassManager;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
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
		if(!canApply(attackEvent)) return;

		if(attackEvent.arrow == null) return;
		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.attacker);
		HitCounter.incrementCounter(pitPlayer.player, this);

		Cooldown cooldown = getCooldown(attackEvent.attacker, 200);
		if(cooldown.isOnCooldown()) return; else cooldown.reset();

		if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, 3)) return;

		Vector velocity = attackEvent.arrow.getVelocity().normalize().multiply(getPunchMultiplier(enchantLvl) / 2.35);
		velocity.setY(0);

		if(BypassManager.bypassPCTS.contains(attackEvent.defender)) {
			BypassManager.bypassPCTS.remove(attackEvent.defender);
		}
		BypassManager.bypassPCTS.add(attackEvent.defender);

		attackEvent.defender.setVelocity(velocity);

		new BukkitRunnable() {
			@Override
			public void run() {
				BypassManager.bypassPCTS.remove(attackEvent.defender);
			}
		}.runTaskLater(PitSim.INSTANCE, 40L);
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
