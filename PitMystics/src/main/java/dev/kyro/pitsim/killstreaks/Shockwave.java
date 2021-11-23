package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shockwave extends Killstreak {
	public static Shockwave INSTANCE;

	public Shockwave() {
		super("Shockwave", "Shockwave", 40, 24);
		INSTANCE = this;
	}

	@Override
	public void proc(Player player) {
		for(int i = 0; i < 5; i++) {
			Location exploLoc = player.getLocation().clone().add(0, 1, 0);
			exploLoc.add(Math.random() * 10 - 5, 0, Math.random() * 10 - 5);
			player.getWorld().playEffect(exploLoc, Effect.EXPLOSION_HUGE, 1);
		}

		List<Entity> entityList = player.getNearbyEntities(4, 4,4);
		List<Player> nonList = new ArrayList<>();
		for(Entity entity : entityList) {
			if(entity instanceof Player && NonManager.getNon((Player) entity) != null && entity.getLocation().distance(player.getLocation()) < 4) {
				nonList.add((Player) entity);
			}
		}
		int count = 0;
		for(Player non : nonList) {
			Map<PitEnchant, Integer> attackerEnchant = EnchantManager.getEnchantsOnPlayer(player);
			Map<PitEnchant, Integer> defenderEnchant = new HashMap<>();
			EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(player, non, EntityDamageEvent.DamageCause.CUSTOM, 0);
			AttackEvent attackEvent = new AttackEvent(ev, attackerEnchant, defenderEnchant, false);

			double distance = non.getLocation().distance(player.getLocation());
			if(distance < 2.5 && count < 15) {
				DamageManager.kill(attackEvent, player, non, false);
				count++;
				if(distance < 2) {
					DamageManager.kill(attackEvent, player, non, false);
					count++;
				}
			} else {
				non.setHealth(non.getHealth() / 2.0);
			}
		}

//		Sounds.SHOCKWAVE.play(player);
		player.playEffect(player.getLocation(), Effect.EXPLOSION_HUGE, 100);
	}

	@Override
	public void reset(Player player) {
	}

	@Override
	public ItemStack getDisplayItem(Player player) {

		AItemStackBuilder builder = new AItemStackBuilder(Material.MONSTER_EGG, 1, 60);
		builder.setName("&e" + name);
		builder.setLore(new ALoreBuilder("&7Every: &c" + killInterval + " kills", "", "&7Send out a shockwave, doing", "&7massive damage to nearby bots."));

		return builder.getItemStack();
	}
}
