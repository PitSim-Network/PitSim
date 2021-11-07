package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Explicious extends Killstreak {

	public static Explicious INSTANCE;

	public Explicious() {
		super("Explicious", "Explicious", 3, 0);
		INSTANCE = this;
	}

	List<Player> rewardPlayers = new ArrayList<>();


	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(rewardPlayers.contains(killEvent.killer)) {
			killEvent.xpReward += 20;
			killEvent.xpCap += 20;
			rewardPlayers.remove(killEvent.killer);
		}
	}


	@Override
	public void proc(Player player) {
		rewardPlayers.add(player);
	}

	@Override
	public void reset(Player player) {
		rewardPlayers.remove(player);
	}

	@Override
	public ItemStack getDisplayItem(Player player) {

		AItemStackBuilder builder = new AItemStackBuilder(Material.INK_SACK, 1, 12);
		builder.setName("&e" + name);
		builder.setLore(new ALoreBuilder("&7Every: &c" + killInterval + " kills", "", "&7Gain &b+20 XP&7 and &b+20 max XP", "&7on your next kill."));

		return builder.getItemStack();
	}
}
