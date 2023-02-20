package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Limiter extends Killstreak {
	public static Limiter INSTANCE;
	public List<LivingEntity> rewardPlayers = new ArrayList<>();

	public Limiter() {
		super("Limiter", "Limiter", 3, 0);
		INSTANCE = this;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(AttackEvent.Apply event) {
		if(rewardPlayers.contains(event.getDefender())) event.trueDamage = Math.min(event.trueDamage, 2);
	}

	@Override
	public void proc(Player player) {
		if(!rewardPlayers.contains(player)) rewardPlayers.add(player);

		new BukkitRunnable() {
			@Override
			public void run() {
				rewardPlayers.remove(player);
			}
		}.runTaskLater(PitSim.INSTANCE, 3 * 20L);
	}

	@Override
	public void reset(Player player) {
		rewardPlayers.remove(player);
	}

	@Override
	public ItemStack getDisplayItem(Player player) {
		AItemStackBuilder builder = new AItemStackBuilder(Material.ANVIL)
				.setName("&e" + displayName)
				.setLore(new ALoreBuilder(
						"&7Every: &c" + killInterval + " kills",
						"",
						"&7Limit the true damage you",
						"&7can take per hit to &9" + Misc.getHearts(2),
						"&7for 3 seconds"
				));

		return builder.getItemStack();
	}
}
