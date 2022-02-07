package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticguilds.controllers.GuildManager;
import dev.kyro.arcticguilds.controllers.objects.Guild;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.HealEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Dispersion extends Killstreak {
	public static Dispersion INSTANCE;
	public static List<Player> rewardPlayers = new ArrayList<>();

	public Dispersion() {
		super("Dispersion", "Dispersion", 3, 0);
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Pre attackEvent) {
		if(!rewardPlayers.contains(attackEvent.defender)) return;
		Guild defenderGuild = GuildManager.getGuild(attackEvent.defender);
		if(defenderGuild != null) {
			AOutput.error(attackEvent.defender, "Dispersion does not work if you are in a guild");
			return;
		}

		List<PitEnchant> toRemove = new ArrayList<>();
		for(Map.Entry<PitEnchant, Integer> entry : attackEvent.getAttackerEnchantMap().entrySet()) {
			if(Math.random() > getChance() / 100.0) continue;
			toRemove.add(entry.getKey());
		}
		for(PitEnchant pitEnchant : toRemove) attackEvent.getAttackerEnchantMap().remove(pitEnchant);
	}

	@EventHandler
	public void onHeal(HealEvent healEvent) {
		if(!rewardPlayers.contains(healEvent.player)) return;
		if(healEvent.healType == HealEvent.HealType.HEALTH) healEvent.multipliers.add(1.25D);
	}

	@Override
	public void proc(Player player) {
		rewardPlayers.add(player);
		new BukkitRunnable() {
			@Override
			public void run() {
				rewardPlayers.remove(player);
			}
		}.runTaskLater(PitSim.INSTANCE, 20 * 4);
	}

	@Override
	public void reset(Player player) {
		rewardPlayers.remove(player);
	}

	@Override
	public ItemStack getDisplayItem(Player player) {

		AItemStackBuilder builder = new AItemStackBuilder(Material.WEB);
		builder.setName("&e" + name);
		builder.setLore(new ALoreBuilder("&7Every: &c" + killInterval + " kills", "",
				"&dDisperse " + getChance() + "% &7of the enchants",
				"&7on your opponent's attacks for",
				"&7for 4 seconds (Only works for players",
				"&7that are not in a guild)"));

		return builder.getItemStack();
	}

	public static int getChance() {
		return 50;
	}
}
