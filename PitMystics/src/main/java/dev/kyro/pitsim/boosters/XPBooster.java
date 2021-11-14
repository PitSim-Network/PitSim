package dev.kyro.pitsim.boosters;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Booster;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class XPBooster extends Booster {
	public XPBooster() {
		super("XP Booster", "xp", 10, ChatColor.AQUA);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onKill(KillEvent killEvent) {
		if(!isActive()) return;
		killEvent.xpMultipliers.add(1.5);
		killEvent.xpCap *= 1.3;
	}

	@Override
	public List<String> getDescription() {
		return null;
	}

	@Override
	public ItemStack getDisplayItem() {
		AItemStackBuilder builder = new AItemStackBuilder(Material.INK_SACK, 1, 12);
		ALoreBuilder loreBuilder = new ALoreBuilder("&7All players on the server gain",
				"&b1.5x XP&7 and &b1.3x max XP&7. Earn &b10% XP", "&7from all players as they streak", "");
		if(minutes > 0) {
			builder.setName("&a" + name);
			loreBuilder.addLore("&7Status: &aActive!", "&7Expires in: &e" + minutes + " minutes", "");
		} else {
			builder.setName("&c" + name);
			loreBuilder.addLore("&7Status: &cInactive!", "&7Use a booster to activate", "");
		}
		builder.setLore(loreBuilder);
		return builder.getItemStack();
	}
}
