package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AssuredStrike extends Killstreak {

	public static AssuredStrike INSTANCE;

	public AssuredStrike() {
		super("Assured Strike", "AssuredStrike", 5, 2);
		INSTANCE = this;
	}

	List<Player> rewardPlayers = new ArrayList<>();

	@EventHandler
	public void onHit(AttackEvent.Apply event) {
		if(rewardPlayers.contains(event.attacker)) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.attacker);
			pitPlayer.heal(1.5 + (event.getFinalDamageIncrease() * (50 / 100D)));
			rewardPlayers.remove(event.attacker);
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

		AItemStackBuilder builder = new AItemStackBuilder(Material.DIAMOND_SWORD);
		builder.setName("&e" + name);
		builder.setLore(new ALoreBuilder("&7Every: &c" + killInterval + " kills", "", "&7Next melee hit deals &c+35%", "&cdamage &7and grants &eSpeed I", "&7for 20 seconds."));

		return builder.getItemStack();
	}
}
