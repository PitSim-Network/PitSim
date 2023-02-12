package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class ComboVenom extends PitEnchant {

	public ComboVenom() {
		super("Combo: Venom", true, ApplyType.NONE,
				"venom", "combo-venom");
		isUncommonEnchant = true;
	}

	public static boolean isVenomed(LivingEntity entity) {
		return entity.hasPotionEffect(PotionEffectType.POISON);
	}

	@EventHandler
	public void onPoison(EntityDamageEvent event) {
		if(event.getCause() == EntityDamageEvent.DamageCause.POISON) event.setCancelled(true);
	}

//	@EventHandler
//	public void onVenomAttacked(AttackEvent.Pre attackEvent) {
//		if(isVenomed(attackEvent.attacker) || isVenomed(attackEvent.defender)) {
//			attackEvent.getAttackerEnchantMap().clear();
//			attackEvent.getDefenderEnchantMap().clear();
//		}
//	}

//	@EventHandler
//	public void onAttack(AttackEvent.Apply attackEvent) {
//		if(!canApply(attackEvent)) return;
//
//		if(attackEvent.attacker.hasPotionEffect(PotionEffectType.POISON) || attackEvent.defender.hasPotionEffect(PotionEffectType.POISON)) {
//			Non non = NonManager.getNon(attackEvent.defender);
//			if(non == null) attackEvent.multipliers.add(10 / 8.5D);
//		}
//
//		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
//		if(enchantLvl == 0 || attackEvent.arrow != null) return;
//
//		if(attackEvent.attackerIsPlayer) {
//			PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.attackerPlayer);
//			HitCounter.incrementCounter(pitPlayer.player, this);
//			if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, 3)) return;
//
//			Misc.applyPotionEffect(attackEvent.attacker, PotionEffectType.POISON, 20 * 24, 0, true, false);
//			Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.POISON, 20 * 12, 0, true, false);
//			Sounds.VENOM.play(attackEvent.attacker);
//			Sounds.VENOM.play(attackEvent.defender);
//		}
//	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Every &ethird &7strike &apoisons &7enemies, temporarily applying " +
						"Somber for &512 seconds&7. Also &apoisons &7yourself!"
		).getLore();

	}
}
