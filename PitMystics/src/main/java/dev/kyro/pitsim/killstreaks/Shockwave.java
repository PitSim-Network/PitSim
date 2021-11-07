package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Effect;
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
		List<Entity> entityList = player.getNearbyEntities(5, 5,5);
		List<Player> nonList = new ArrayList<>();
		for(Entity entity : entityList) {
			if(entity instanceof Player && NonManager.getNon((Player) entity) != null) {
				nonList.add((Player) entity);
			}
		}
		for(Player non : nonList) {
			Map<PitEnchant, Integer> attackerEnchant = EnchantManager.getEnchantsOnPlayer(player);
			Map<PitEnchant, Integer> defenderEnchant = new HashMap<>();
			EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(player, non, EntityDamageEvent.DamageCause.CUSTOM, 0);
			AttackEvent attackEvent = new AttackEvent(ev, attackerEnchant, defenderEnchant, false);


			DamageManager.kill(attackEvent, player, non, false);
		}

		Sounds.SHOCKWAVE.play(player);
		player.playEffect(player.getLocation(), Effect.EXPLOSION_HUGE, 100);

	}

	@Override
	public void reset(Player player) {
	}

	@Override
	public ItemStack getDisplayItem(Player player) {

		AItemStackBuilder builder = new AItemStackBuilder(Material.MONSTER_EGG, 1, 60);
		builder.setName("&e" + name);
		builder.setLore(new ALoreBuilder("&7Every: &c" + killInterval + " kills", "", "&7Send out a shockwave, killing", "&7aall bots in a 5 block radius."));

		return builder.getItemStack();
	}
}
