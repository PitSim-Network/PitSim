package dev.kyro.pitsim.enchants.tainted;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.PitPlayerAttemptAbilityEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;

public class MeteorSpell extends PitEnchant {

	public MeteorSpell() {
		super("Meteor", true, ApplyType.SCYTHES,
				"meteor");
		tainted = true;
	}

	@EventHandler
	public void onUse(PitPlayerAttemptAbilityEvent event) {
		int enchantLvl = event.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(event.getPlayer(), 10);
		if(cooldown.isOnCooldown()) return;

		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.useMana(getManaCost(enchantLvl))) {
			Sounds.NO.play(player);
			return;
		}

		cooldown.restart();

		Set<Material> mat = null;
		Block block = player.getTargetBlock(mat, 20);

		while(block.getType() == Material.AIR) {
			block = block.getLocation().subtract(0, 1, 0).getBlock();
		}

		for(Entity entity : player.getNearbyEntities(15, 15, 15)) {
			Vector direction = player.getLocation().getDirection();
			Vector towardsEntity = entity.getLocation().subtract(player.getLocation()).toVector().normalize();

			if(direction.distance(towardsEntity) < 0.3) {
				block = entity.getLocation().getBlock();
			}
		}

		Vector diff = block.getLocation().subtract(block.getLocation().add(5, 20, 0)).toVector();
		Location base = block.getLocation().add(5, 20, 0)/* the origin, where you are moving away from */;
		double add = diff.length(); //example amount
		diff.divide(new Vector(add, add, add));

		int time = 1;

		for(int i = 0; i < add; i++) {
			int finalI = i;
			new BukkitRunnable() {
				@Override
				public void run() {
					base.add(diff);
					base.getWorld().playEffect(base, Effect.EXPLOSION_LARGE, 1);
					base.getWorld().playEffect(base, Effect.LARGE_SMOKE, 1);
					base.getWorld().playEffect(base, Effect.PARTICLE_SMOKE, 1);
					Sounds.METEOR.play(base);
					if(finalI >= (add - 1)) {
						Sounds.EXPLOSIVE_3.play(base);

						for(Entity near : base.getWorld().getNearbyEntities(base, 5, 5, 5)) {
							if(near instanceof ArmorStand || near instanceof Villager) continue;
							if(!(near instanceof LivingEntity)) continue;
							if(near == player) continue;
							if(near instanceof Player) {
								((Player) near).damage(25, player);
								return;
							}
							EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(player, near, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 50);
							damageEvent.setDamage(50);
							Bukkit.getServer().getPluginManager().callEvent(damageEvent);
							return;
						}
					}
				}
			}.runTaskLater(PitSim.INSTANCE, time);
			time++;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		if(!(event.getEntity() instanceof Fireball)) return;
		event.blockList().clear();
	}

	@Override
	public List<String> getDescription(int enchantLvl) {
		return new ALoreBuilder("&7Summon a meteor, causing large", "&7damage to a single target", "&d&o-" + getManaCost(enchantLvl) + " Mana").getLore();
	}

	public static int getManaCost(int enchantLvl) {
		return 120 - (30 * enchantLvl);
	}
}
