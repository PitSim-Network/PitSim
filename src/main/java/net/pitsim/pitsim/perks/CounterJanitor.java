package net.pitsim.pitsim.perks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.pitsim.controllers.NonManager;
import net.pitsim.pitsim.controllers.objects.PitPerk;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.events.KillEvent;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import net.pitsim.pitsim.upgrades.UnlockCounterJanitor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class CounterJanitor extends PitPerk {
	public static CounterJanitor INSTANCE;

	public CounterJanitor() {
		super("Counter-Janitor", "counter-janitor");
		renownUpgradeClass = UnlockCounterJanitor.class;
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!hasPerk(killEvent.getKiller())) return;
		if(killEvent.isKillerPlayer() && NonManager.getNon(killEvent.getDead()) == null) {
			PitPlayer pitPlayer = killEvent.getKillerPitPlayer();
			double missingHealth = killEvent.getKiller().getMaxHealth() - killEvent.getKiller().getHealth();
			pitPlayer.heal(missingHealth);
		}
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.SPONGE)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, Player player) {
		loreBuilder.addLongLine(
				"&7Instantly heal to &cfull health &7on player kill"
		);
	}

	@Override
	public String getSummary() {
		return "&eCounter-Janitor is a perk unlocked in the &erenown shop&7 that &cheals you&7 for substantially " +
				"on player kill. This perk is incompatible with &cVampire";
	}
}
