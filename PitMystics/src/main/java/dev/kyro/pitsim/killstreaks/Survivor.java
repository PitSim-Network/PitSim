package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Survivor extends Killstreak {

	public static Survivor INSTANCE;

	public Survivor() {
		super("Survivor", "Survivor", 15, 0);
		INSTANCE = this;
	}

	List<Player> rewardPlayers = new ArrayList<>();

	@EventHandler
	public void onHeal(HealEvent healEvent) {
		if(!rewardPlayers.contains(healEvent.player)) return;
		if(healEvent.healType == HealEvent.HealType.HEALTH) healEvent.multipliers.add(1.25D);
	}

	@Override
	public void proc(Player player) {
		rewardPlayers.add(player);
		Sounds.SURVIVOR_HEAL.play(player);

		new BukkitRunnable() {
			@Override
			public void run() {
				rewardPlayers.remove(player);
			}
		}.runTaskLater(PitSim.INSTANCE, 15 * 20L);
	}

	@Override
	public void reset(Player player) {
		rewardPlayers.remove(player);
	}

	@Override
	public ItemStack getDisplayItem(Player player) {

		AItemStackBuilder builder = new AItemStackBuilder(Material.GOLDEN_APPLE);
		builder.setName("&e" + name);
		builder.setLore(new ALoreBuilder("&7Every: &c" + killInterval + " kills", "", "&7Heal &e25% &7more &c\u2764", "&7for 15 seconds."));

		return builder.getItemStack();
	}
}
