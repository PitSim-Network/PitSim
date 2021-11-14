package dev.kyro.pitsim.boosters;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Booster;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GoldBooster extends Booster {
	public GoldBooster() {
		super("Gold Booster", "gold", 12, ChatColor.GOLD    );
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!isActive()) return;
		killEvent.goldMultipliers.add(1.5);
	}

	@Override
	public List<String> getDescription() {
		return null;
	}

	@Override
	public ItemStack getDisplayItem() {
		AItemStackBuilder builder = new AItemStackBuilder(Material.INK_SACK, 1, 14);
		ALoreBuilder loreBuilder = new ALoreBuilder("&7All players on the server gain",
				"&61.5x gold&7. Earn &610% gold from", "&7all players as they streak", "");
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
