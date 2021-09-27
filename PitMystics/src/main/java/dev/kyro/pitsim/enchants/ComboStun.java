package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class ComboStun extends PitEnchant {

	public ComboStun() {
		super("Combo: Stun", true, ApplyType.SWORDS,
				"combostun", "stun", "combo-stun", "cstun");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		int regLvl = attackEvent.getAttackerEnchantLevel(Regularity.INSTANCE);
		if(Regularity.isRegHit(attackEvent.defender) && Regularity.skipHit(regLvl)) return;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.attacker);
		HitCounter.incrementCounter(pitPlayer.player, this);
		if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, 5)) return;

		Cooldown cooldown = getCooldown(attackEvent.attacker, 8 * 20);
		if(cooldown.isOnCooldown()) return; else cooldown.reset();

		Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.SLOW, (int) getDuration(enchantLvl) * 20, 7, true, false);
		Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.JUMP, (int) getDuration(enchantLvl) * 20, 254, true, false);
		Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.SLOW_DIGGING, (int) getDuration(enchantLvl) * 20, 99, true, false);

		Misc.sendTitle(attackEvent.defender, "&cSTUNNED", (int) getDuration(enchantLvl) * 20);
		Misc.sendSubTitle(attackEvent.defender, "&eYou cannot move!", (int) getDuration(enchantLvl) * 20);

		attackEvent.attacker.playSound(attackEvent.attacker.getLocation(), Sound.ANVIL_LAND, 1, 1);
		attackEvent.defender.playSound(attackEvent.attacker.getLocation(), Sound.ANVIL_LAND, 1, 1);

	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7The &efifth &7strike on an enemy", "&7stuns them for " + getDuration(enchantLvl) + " &7seconds",
				"&7&o(Can only be stunned every 8s)").getLore();
	}

	public double getDuration(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 0.7;
			case 2:
				return 1.0;
			case 3:
				return 1.7;

		}
		return 0;
	}


}
