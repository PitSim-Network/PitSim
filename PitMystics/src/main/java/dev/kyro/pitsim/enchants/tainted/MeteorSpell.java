package dev.kyro.pitsim.enchants.tainted;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.PitPlayerAttemptAbilityEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
	public void onDamage(AttackEvent.Pre attackEvent) {
		if(attackEvent.fireball == null || attackEvent.attacker != attackEvent.defender) return;
		attackEvent.getAttackerEnchantMap().clear();
		attackEvent.getDefenderEnchantMap().clear();
		attackEvent.event.setCancelled(true);
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
		Block block = player.getTargetBlock(mat, 50);

		if(block.getType() == Material.AIR) {

		}

		Vector diff = block.getLocation().subtract(block.getLocation().add(5, 20, 0)).toVector();
		Location base = block.getLocation().add(5, 20, 0)/* the origin, where you are moving away from */;
		double add = diff.length(); //example amount
		diff.divide(new Vector(add, add, add));

		int time = 1;

		for (int i = 0; i < add; i++) {
			 new BukkitRunnable() {
				 @Override
				 public void run() {
					 base.add(diff);
					 base.getWorld().playEffect(base, Effect.EXPLOSION_LARGE, 1);
					 base.getWorld().playEffect(base, Effect.LARGE_SMOKE, 1);
					 base.getWorld().playEffect(base, Effect.BOW_FIRE, 1);
				 }
			 }.runTaskLater(PitSim.INSTANCE, time);
			time++;
		}


	}

	@EventHandler
	public void onAttack(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Fireball) {
			if(((Fireball) event.getDamager()).getShooter() == event.getEntity()) event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		if(!(event.getEntity() instanceof Fireball)) return;
		event.blockList().clear();
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Enjoy").getLore();
	}

	public static int getManaCost(int enchantLvl) {
		return 120 - (30 * enchantLvl);
	}
}
