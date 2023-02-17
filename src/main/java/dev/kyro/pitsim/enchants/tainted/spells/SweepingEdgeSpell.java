package dev.kyro.pitsim.enchants.tainted.spells;

import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.PitPlayerAttemptAbilityEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

public class SweepingEdgeSpell extends PitEnchant {
	public SweepingEdgeSpell() {
		super("Sweeping Edge", true, ApplyType.SCYTHES, "sweepingedge", "sweep", "sweeping");
		isTainted = true;
	}

	@EventHandler
	public void onUse(PitPlayerAttemptAbilityEvent event) {
		int enchantLvl = event.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(event.getPlayer(), 10);
		if(cooldown.isOnCooldown()) return;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
		if(!pitPlayer.useMana(getManaCost(enchantLvl))) {
			Sounds.NO.play(event.getPlayer());
			return;
		}

		Sounds.SWEEP.play(event.getPlayer().getLocation());
		cooldown.restart();

		Player player = event.getPlayer();

		int entities = 0;

		for(Entity entity : player.getNearbyEntities(7, 7, 7)) {
			if(entities == 5) break;
			if(!(entity instanceof LivingEntity)) continue;
			if(entity instanceof Player && Bukkit.getOnlinePlayers().contains(entity)) continue;
			EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(player, entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 2);
			Bukkit.getServer().getPluginManager().callEvent(damageEvent);
			if(!damageEvent.isCancelled()) ((LivingEntity) entity).damage(2);
			entities++;
		}

	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7This weapon hits all nearby enemies &d&o-" + getManaCost(enchantLvl) + " Mana"
		).getLore();
	}

	public static int getManaCost(int enchantLvl) {
		return 30 * (4 - enchantLvl);
	}
}
