package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.Player;
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

	public static boolean isVenomed(Player player) {
		return player.hasPotionEffect(PotionEffectType.POISON);
	}

	@EventHandler
	public void onPoison(EntityDamageEvent event) {
		if(event.getCause() == EntityDamageEvent.DamageCause.POISON) event.setCancelled(true);
	}

	@EventHandler
	public void onVenomAttacked(AttackEvent.Pre attackEvent) {
		if(isVenomed(attackEvent.attacker) || isVenomed(attackEvent.defender)) {
			attackEvent.getAttackerEnchantMap().clear();
			attackEvent.getDefenderEnchantMap().clear();
		}
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		if(attackEvent.attacker.hasPotionEffect(PotionEffectType.POISON) || attackEvent.defender.hasPotionEffect(PotionEffectType.POISON)) {
			Non non = NonManager.getNon(attackEvent.defender);
			if(non == null) attackEvent.multiplier.add(10/8.5D);
		}

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0 || attackEvent.arrow != null) return;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.attacker);
		HitCounter.incrementCounter(pitPlayer.player, this);
		if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, 3)) return;

		Misc.applyPotionEffect(attackEvent.attacker, PotionEffectType.POISON, 20 * 24, 0, true, false);
		Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.POISON, 20 * 12, 0, true, false);
		Sounds.VENOM.play(attackEvent.attacker);
		Sounds.VENOM.play(attackEvent.defender);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Every &ethird &7strike &apoisons", "&7enemies, temporarily applying", "&7Somber for &512 seconds.",
				"&7Also &apoisons &7yourself!").getLore();

	}
}
