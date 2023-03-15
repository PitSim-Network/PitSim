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
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class CounterStrike extends Killstreak {
	public static CounterStrike INSTANCE;
	public static List<LivingEntity> rewardPlayers = new ArrayList<>();

	public CounterStrike() {
		super("Counter-Strike", "CounterStrike", 7, 20);
		INSTANCE = this;
	}

	@EventHandler
	public void onHit(AttackEvent.Apply event) {
		if(rewardPlayers.contains(event.getAttacker())) event.increasePercent += 10;
		if(rewardPlayers.contains(event.getDefender())) event.multipliers.add(Misc.getReductionMultiplier(20));
	}

	@Override
	public void proc(Player player) {
		if(!rewardPlayers.contains(player)) rewardPlayers.add(player);

		new BukkitRunnable() {
			@Override
			public void run() {
				rewardPlayers.remove(player);
			}
		}.runTaskLater(PitSim.INSTANCE, 20 * 8L);
	}

	@Override
	public void reset(Player player) {
		rewardPlayers.remove(player);
	}

	@Override
	public ItemStack getDisplayItem(Player player) {
		AItemStackBuilder builder = new AItemStackBuilder(Material.IRON_BARDING)
				.setName("&e" + displayName)
				.setLore(new ALoreBuilder(
						"&7Every: &c" + killInterval + " kills",
						"",
						"&7Deal &c+10% &7damage and receive",
						"&9-20% &7damage per hit for 8s."
				));

		return builder.getItemStack();
	}

	@Override
	public String getSummary() {
		return "&eCounter-Strike&7 is a killstreak that increases your &cDamage&7 and &9Damage Reduction&7 temporarily every &c7 kills";
	}
}
