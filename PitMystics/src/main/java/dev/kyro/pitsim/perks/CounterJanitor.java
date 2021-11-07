package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
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
		super("Counter-Janitor", "counter-janitor", new ItemStack(Material.SPONGE), 20, true, "COUNTER_JANITOR", INSTANCE);
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent)  {
		if(!playerHasUpgrade(killEvent.killer)) return;
		if(NonManager.getNon(killEvent.dead) == null) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(killEvent.killer);
			double missingHealth = killEvent.killer.getMaxHealth() - killEvent.killer.getHealth();
			pitPlayer.heal(missingHealth / 2);
		}
	}

	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&7Instantly heal half your", "&chealth &7on player kill.").getLore();
	}
}
