package dev.kyro.pitsim.enchants.tainted.effects;

import dev.kyro.pitsim.adarkzone.DarkzoneManager;
import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.cosmetics.particles.WaterBubbleParticle;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.ManaRegenEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Defraction extends PitEnchant {
	public static Defraction INSTANCE;

	public Defraction() {
		super("Defraction", true, ApplyType.CHESTPLATES,
				"defraction", "defract", "defrac");
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onManaRegen(ManaRegenEvent event) {
		Player player = event.getPlayer();
		int enchantLvl = EnchantManager.getEnchantLevel(player, this);
		if(enchantLvl == 0) return;
		event.multipliers.add(Misc.getReductionMultiplier(getManaReduction(enchantLvl)));
	}

	@EventHandler
	public void onAttack(AttackEvent.Pre attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(Math.random() > getChance(enchantLvl) / 100.0) return;

		Location startLocation = attackEvent.getDefender().getLocation().add(0, 1, 0);
		LivingEntity reflectionTarget;

		List<LivingEntity> candidates = new ArrayList<>();
		for(Entity entity : attackEvent.getDefender().getNearbyEntities(15, 15, 15)) {
			if(!(entity instanceof LivingEntity) || entity == attackEvent.getAttacker()) continue;
			LivingEntity livingEntity = (LivingEntity) entity;
			PitMob pitMob = DarkzoneManager.getPitMob(livingEntity);
			if(pitMob == null) continue;
			double distance = livingEntity.getLocation().distance(attackEvent.getDefender().getLocation());
			if(distance > 15) continue;
			candidates.add(livingEntity);
		}
		if(candidates.isEmpty()) return;
		reflectionTarget = candidates.get(new Random().nextInt(candidates.size()));

		EntityDamageEvent event = new EntityDamageEvent(reflectionTarget, EntityDamageEvent.DamageCause.CUSTOM, attackEvent.getEvent().getDamage());
		Bukkit.getPluginManager().callEvent(event);
		if(!event.isCancelled()) reflectionTarget.damage(event.getDamage());
		attackEvent.setCancelled(true);

		Sounds.AEGIS.play(attackEvent.getDefender());

		Location endLocation = reflectionTarget.getLocation().add(0, 1, 0);
		double distance = startLocation.distance(endLocation);
		int steps = (int) Math.ceil(distance * 5);
		double stepSize = distance / steps;
		Vector stepVector = endLocation.toVector().subtract(startLocation.toVector()).normalize().multiply(stepSize);

		Location drawLocation = startLocation.clone();
		for(int i = 0; i <= steps; i++) {
			drawEffect(drawLocation);
			drawLocation.add(stepVector);
		}
	}

	private static void drawEffect(Location location) {
		for(Entity nearbyEntity : location.getWorld().getNearbyEntities(location, 25, 25, 25)) {
			if(!(nearbyEntity instanceof Player)) continue;
			EntityPlayer entityPlayer = ((CraftPlayer) nearbyEntity).getHandle();
			new WaterBubbleParticle().display(entityPlayer, location);
		}
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Deflect &9" + getChance(enchantLvl) + "% &7of attacks from mobs to other nearby mobs. " +
						"Regain mana &b" + getManaReduction(enchantLvl) + "% &7slower"
		).getLore();
	}

	public static int getChance(int enchantLvl) {
		return enchantLvl * 10;
	}

	public static int getManaReduction(int enchantLvl) {
		return 50;
	}
}
