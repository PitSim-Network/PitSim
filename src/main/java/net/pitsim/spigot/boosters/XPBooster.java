package net.pitsim.spigot.boosters;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.spigot.controllers.LevelManager;
import net.pitsim.spigot.controllers.objects.Booster;
import net.pitsim.spigot.events.KillEvent;
import net.pitsim.spigot.misc.PitLoreBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

public class XPBooster extends Booster {
	public static XPBooster INSTANCE;

	public XPBooster() {
		super("XP Booster", "xp", 9, ChatColor.AQUA);
		INSTANCE = this;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onKill(KillEvent killEvent) {
		if(!isActive()) return;
		killEvent.xpMultipliers.add(1 + (getXPIncrease() / 100.0));
		killEvent.maxXPMultipliers.add(1 + (getMaxXPIncrease() / 100.0));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onKillMonitor(KillEvent killEvent) {
		if(!isActive() || activatorUUID == null || killEvent.getKiller() == null || killEvent.getKiller().getUniqueId().equals(activatorUUID)) return;
		queueShare(killEvent.getFinalXp());
	}

	@Override
	public void share(Player player, int amount) {
		LevelManager.addXP(player, (long) (amount * (getXPShare() / 100.0)));
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		return new AItemStackBuilder(Material.INK_SACK, 1, 12)
				.setLore(new PitLoreBuilder(
						"&7All players on the server gain &b+" + getXPIncrease() + "% XP &7and &b+" + getMaxXPIncrease() + "% max XP"
				).addLongLine(
						"&7If you activate this booster, gain &b" + decimalFormat.format(getXPShare()) +
						"% &7of the &bxp &7earned by everyone online"
				)).getItemStack();
	}

	public static int getXPIncrease() {
		return 50;
	}

	public static int getMaxXPIncrease() {
		return 35;
	}

	public static double getXPShare() {
		return 3;
	}
}
