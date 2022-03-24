package dev.kyro.pitsim.enchants.tainted;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enchants.ComboVenom;
import dev.kyro.pitsim.enums.ApplyType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class EmotionalDamage extends PitEnchant {
	public static EmotionalDamage INSTANCE;

	public EmotionalDamage() {
		super("Emotional Damage", true, ApplyType.CHESTPLATES,
				"aoe");
		isUncommonEnchant = true;
		tainted = true;
		INSTANCE = this;
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(!MapManager.inDarkzone(player)) continue;
					int level = EnchantManager.getEnchantLevel(player, INSTANCE);
					if(ComboVenom.isVenomed(player) || level == 0) continue;

					for(Entity nearbyEntity : player.getNearbyEntities(5, 5, 5)) {
						if(!(nearbyEntity instanceof LivingEntity) || nearbyEntity instanceof Player) continue;
						if(!shouldAdd(level)) continue;
						LivingEntity livingEntity = (LivingEntity) nearbyEntity;
						livingEntity.damage(5, player);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7EMOTIONAL DAMAGE!").getLore();
	}

	public static boolean shouldAdd(int level) {
		return level * 0.2 > Math.random();
	}
}
