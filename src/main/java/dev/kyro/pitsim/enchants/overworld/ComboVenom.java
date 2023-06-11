package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ComboVenom extends PitEnchant {
	public static ComboVenom INSTANCE;

	public static List<UUID> venomMap = new ArrayList<>();

	public ComboVenom() {
		super("Combo: Venom", true, ApplyType.NONE,
				"venom", "combo-venom");
		isUncommonEnchant = true;
		INSTANCE = this;

		statisticCategories = new ArrayList<>();
	}

	public static boolean isVenomed(LivingEntity entity) {
		if(entity == null) return false;
		return venomMap.contains(entity.getUniqueId());
	}

	public void venom(LivingEntity player) {
		venomMap.add(player.getUniqueId());
		Misc.applyPotionEffect(player, PotionEffectType.POISON, 20 * 12, 0, true, false);
		Sounds.VENOM.play(player);

		new BukkitRunnable() {
			@Override
			public void run() {
				venomMap.remove(player.getUniqueId());
			}
		}.runTaskLater(PitSim.INSTANCE, 20 * 24);
	}

	@EventHandler
	public void onPoison(EntityDamageEvent event) {
		if(event.getCause() == EntityDamageEvent.DamageCause.POISON) event.setCancelled(true);
	}

	@EventHandler
	public void onVenomAttacked(AttackEvent.Pre attackEvent) {
		if(attackEvent.getAttackerEnchantLevel(this) != 0) {
			attackEvent.getAttackerEnchantMap().clear();
			attackEvent.getDefenderEnchantMap().clear();
			attackEvent.getAttackerEnchantMap().put(this, 1);
		} else if(attackEvent.getDefenderEnchantLevel(this) != 0) {
			attackEvent.getAttackerEnchantMap().clear();
			attackEvent.getDefenderEnchantMap().clear();
			attackEvent.getDefenderEnchantMap().put(this, 1);
		} else if(isVenomed(attackEvent.getAttacker()) || isVenomed(attackEvent.getDefender())) {
			attackEvent.getAttackerEnchantMap().clear();
			attackEvent.getDefenderEnchantMap().clear();
		}
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0 || attackEvent.getArrow() != null) return;

		if(attackEvent.isAttackerPlayer()) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.getAttackerPlayer());
			HitCounter.incrementCounter(pitPlayer.player, this);
			if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, 3)) return;

			venom(attackEvent.getAttacker());
			venom(attackEvent.getDefender());
		}
	}

	@Override
	public String getSummary() {
		return null;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Every &ethird &7strike &apoisons &7enemies, temporarily applying " +
						"Somber for &512 seconds&7. Also &apoisons &7yourself!"
		).getLore();

	}
}
