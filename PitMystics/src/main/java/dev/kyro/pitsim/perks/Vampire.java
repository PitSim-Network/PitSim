package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Vampire extends PitPerk {

	public static Vampire INSTANCE;

	public Vampire() {
		super("Vampire", "vampire", new ItemStack(Material.FERMENTED_SPIDER_EYE), 10, false, "", INSTANCE, true);
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.attackerIsPlayer) return;
		if(!playerHasUpgrade(attackEvent.attacker)) return;
		PitPlayer pitAttacker = PitPlayer.getPitPlayer(attackEvent.attackerPlayer);

		double healing = 0.5;
		if(attackEvent.arrow != null && attackEvent.arrow.isCritical()) healing *= 3;
		pitAttacker.heal(healing);
	}

//	@EventHandler
//	public void onKill(KillEvent killEvent) {
//		if(!playerHasUpgrade(killEvent.killer)) return;
//
//		Misc.applyPotionEffect(killEvent.killer, PotionEffectType.REGENERATION, 160, 0, true, false);
//	}

	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&7Heal &c0.25\u2764 &7on hit.", "&7Tripled on arrow crit.").getLore();
	}
}
