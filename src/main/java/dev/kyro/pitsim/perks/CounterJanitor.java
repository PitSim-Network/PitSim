package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CounterJanitor extends PitPerk {
	public Map<Player, List<Player>> hitPlayers = new HashMap<>();

	public static CounterJanitor INSTANCE;

	public CounterJanitor() {
		super("Counter-Janitor", "counter-janitor", new ItemStack(Material.SPONGE), 19, true, "COUNTER_JANITOR", INSTANCE, true);
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!playerHasUpgrade(killEvent.getKiller())) return;
		if(MapManager.inDarkzone(killEvent.getKiller())) return;
		if(killEvent.isKillerPlayer() && NonManager.getNon(killEvent.getDead()) == null) {
			PitPlayer pitPlayer = killEvent.getKillerPitPlayer();
			double missingHealth = killEvent.getKiller().getMaxHealth() - killEvent.getKiller().getHealth();
			pitPlayer.heal(missingHealth / 2);
		}
	}

	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&7Instantly heal half your", "&chealth &7on player kill.").getLore();
	}
}
