package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Dispersion extends Killstreak {
	public static Dispersion INSTANCE;
	public static List<LivingEntity> rewardPlayers = new ArrayList<>();

	public Dispersion() {
		super("Dispersion", "Dispersion", 3, 0);
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Pre attackEvent) {
		if(!attackEvent.defenderIsPlayer) return;

		if(!rewardPlayers.contains(attackEvent.defender)) return;

		List<PitEnchant> toRemove = new ArrayList<>();
		for(Map.Entry<PitEnchant, Integer> entry : attackEvent.getAttackerEnchantMap().entrySet()) {
			if(Math.random() > getChance() / 100.0) continue;
			toRemove.add(entry.getKey());
		}
		for(PitEnchant pitEnchant : toRemove) attackEvent.getAttackerEnchantMap().remove(pitEnchant);
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

	public static int getChance() {
		return 50;
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
}
