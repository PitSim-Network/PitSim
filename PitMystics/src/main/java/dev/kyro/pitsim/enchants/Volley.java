package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.SpawnManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.VolleyShootEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Volley extends PitEnchant {

	public Volley() {
		super("Volley", true, ApplyType.BOWS,
				"volley");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;
	}

	@EventHandler(ignoreCancelled = true)
	public void onBowShoot(EntityShootBowEvent event) {

		if(event instanceof VolleyShootEvent) return;

		if(!(event.getEntity() instanceof Player) || !(event.getProjectile() instanceof Arrow)) return;
		Player player = ((Player) event.getEntity()).getPlayer();
		Arrow arrow = (Arrow) event.getProjectile();

		int enchantLvl = EnchantManager.getEnchantLevel(player, this);
		if(enchantLvl == 0) return;

		new BukkitRunnable() {
			int count = 0;
			final double arrowVelo = arrow.getVelocity().length();
			@Override
			public void run() {

				if(++count == getArrows(enchantLvl)) {

					cancel();
					return;
				}

				if(SpawnManager.isInSpawn(player.getLocation())) return;

				Arrow volleyArrow = player.launchProjectile(Arrow.class);
				volleyArrow.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(arrowVelo));

				VolleyShootEvent volleyShootEvent = new VolleyShootEvent(event.getEntity(), event.getBow(), volleyArrow, event.getForce());
				PitSim.INSTANCE.getServer().getPluginManager().callEvent(volleyShootEvent);
				if(volleyShootEvent.isCancelled()) volleyArrow.remove();

				PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
				if(pitPlayer.stats != null) pitPlayer.stats.volley++;

				new BukkitRunnable() {
					@Override
					public void run() {
						Sounds.VOLLEY.play(player);
					}
				}.runTaskLater(PitSim.INSTANCE, 1L);
			}
		}.runTaskTimer(PitSim.INSTANCE, 2L, 2L);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Shoot &f" + getArrows(enchantLvl) + " arrows &7at once").getLore();
	}

	public int getArrows(int enchantLvl) {

		return enchantLvl + 1;
	}
}
