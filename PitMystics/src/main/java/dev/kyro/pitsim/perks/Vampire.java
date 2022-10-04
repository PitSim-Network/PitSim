package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Vampire extends PitPerk {

	public static Vampire INSTANCE;

	public Vampire() {
		super("Vampire", "vampire", new ItemStack(Material.FERMENTED_SPIDER_EYE), 10, false, "", INSTANCE, true);
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!playerHasUpgrade(attackEvent.getAttacker())) return;
		PitPlayer pitAttacker = attackEvent.getAttackerPitPlayer();

		int healing = 1;
		if(attackEvent.getArrow() != null && attackEvent.getArrow().isCritical()) healing = 3;
		pitAttacker.heal(healing);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!playerHasUpgrade(killEvent.getKiller())) return;

		Misc.applyPotionEffect(killEvent.getKiller(), PotionEffectType.REGENERATION, 160, 0, true, false);
	}

	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&7Heal &c0.5\u2764 &7on hit.", "&7Tripled on arrow crit.", "&cRegen I &7(8s) on kill.").getLore();
	}
}
