package dev.kyro.pitsim.boosters;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.LevelManager;
import dev.kyro.pitsim.controllers.objects.Booster;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

public class GoldBooster extends Booster {
	public static GoldBooster INSTANCE;

	public GoldBooster() {
		super("Gold Booster", "gold", 11, ChatColor.GOLD);
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!isActive()) return;
		killEvent.goldMultipliers.add(1 + (getGoldIncrease() / 100.0));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onKillMonitor(KillEvent killEvent) {
		if(!isActive() || activatorUUID == null || killEvent.getKiller().getUniqueId().equals(activatorUUID)) return;
		queueShare(killEvent.getFinalGold());
	}

	@Override
	public void share(Player player, int amount) {
		LevelManager.addGold(player, (int) (amount * (getGoldShare() / 100.0)));
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		return new AItemStackBuilder(Material.INK_SACK, 1, 14)
				.setLore(new ALoreBuilder(
						"&7All players on the server gain",
						"&6+" + getGoldIncrease() +"% gold",
						"",
						"&7If you activate this booster,",
						"&7gain &6" + decimalFormat.format(getGoldShare()) + "% &7of the &6gold &7earned",
						"&7by everyone online"
				)).getItemStack();
	}

	public static int getGoldIncrease() {
		return 40;
	}

	public static double getGoldShare() {
		return 3;
	}
}
