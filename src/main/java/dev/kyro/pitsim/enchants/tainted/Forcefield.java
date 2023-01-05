package dev.kyro.pitsim.enchants.tainted;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class Forcefield extends PitEnchant {
	public static Forcefield INSTANCE;

	public Forcefield() {
		super("Forcefield", true, ApplyType.CHESTPLATES, "force", "field", "forcefield");
		tainted = true;
		INSTANCE = this;
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(!MapManager.inDarkzone(player)) continue;

					int enchantLvl = EnchantManager.getEnchantLevel(player, INSTANCE);
					if(enchantLvl == 0) continue;

					int radius = 2;
					double thetaRand = 360 * Math.random();
					double phiRand = 360 * Math.random();

					for(int i = 0; i < 48; i++) {
						double x2 = radius * Math.cos(phiRand) * Math.sin(thetaRand);
						double z2 = radius * Math.sin(phiRand) * Math.sin(thetaRand);
						double y2 = radius * Math.cos(thetaRand);

						int size = player.getWorld().getNearbyEntities(player.getLocation().add(x2, y2 + 1, z2), 0.7, 0.7, 0.7).size();

						float red = size == 0 ? 0 : 1;
						float blue = size == 0 ? 1 : 0;

						player.getWorld().spigot().playEffect(player.getLocation().add(x2, y2 + 1, z2), Effect.COLOURED_DUST, 0, 0, (float) red - 1, (float) 0, (float) blue, 1, 0, 64);

						thetaRand = 360 * Math.random();
						phiRand = 360 * Math.random();
					}

				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 1, 5);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply event) {
		int enchantLvl = event.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;
		if(!MapManager.inDarkzone(event.getDefender())) return;

		event.getDefender().setVelocity(event.getDefender().getVelocity().multiply(0));

		for(Entity entity : event.getDefender().getNearbyEntities(2, 2, 2)) {
			Vector dirVector = entity.getLocation().toVector().subtract(event.getDefender().getLocation().toVector()).normalize();
			Vector pullVector = dirVector.clone().normalize().setY(0.5).multiply(1.5).add(dirVector.clone().multiply(0.03));
			entity.setVelocity(pullVector);
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				event.getDefender().setVelocity(event.getDefender().getVelocity().multiply(0));
			}
		}.runTaskLater(PitSim.INSTANCE, 1);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {
		return new ALoreBuilder("&7Summon a powerful &eForcefield", "&7that reduces &fKnockback &7and", "&7repels enemies", "&d&o-" + reduction(enchantLvl) + "% Mana Regen").getLore();

	}

	public static int reduction(int enchantLvl) {
		return 80 - (20 * enchantLvl);
	}

}
