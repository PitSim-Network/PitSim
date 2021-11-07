package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class FightOrFlight extends Killstreak {

	public static FightOrFlight INSTANCE;

	public FightOrFlight() {
		super("Fight or Flight", "FightOrFlight", 7, 4);
		INSTANCE = this;
	}

	List<Player> rewardPlayers = new ArrayList<>();

	@EventHandler
	public void onHit(AttackEvent.Apply event) {
		if(rewardPlayers.contains(event.attacker)) {
			event.increasePercent += 20 / 100D;
			rewardPlayers.remove(event.attacker);
		}
	}


	@Override
	public void proc(Player player) {
		if(player.getHealth() < player.getMaxHealth() / 2) {
			Misc.applyPotionEffect(player, PotionEffectType.SPEED, 20 * 7, 2, true, false);
		} else {
			rewardPlayers.add(player);
		}
	}

	@Override
	public void reset(Player player) {
		rewardPlayers.remove(player);
	}

	@Override
	public ItemStack getDisplayItem(Player player) {

		AItemStackBuilder builder = new AItemStackBuilder(Material.FIREBALL);
		builder.setName("&e" + name);
		builder.setLore(new ALoreBuilder("&7Every: &c" + killInterval + " kills", "", "&7If below half &c\u2764&7:",
				"&7Gain &eSpeed III &7for 7 seconds.", "", "&7Otherwise:", "&7Deal &c+20% &7damage for 7 seconds."));

		return builder.getItemStack();
	}
}
