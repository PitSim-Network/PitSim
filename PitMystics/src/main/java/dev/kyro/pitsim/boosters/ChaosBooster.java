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

public class ChaosBooster extends Booster {
	public ChaosBooster() {
		super("Chaos Booster", "chaos", 16, ChatColor.GREEN);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!isActive()) return;
	}

	@Override
	public List<String> getDescription() {
		return null;
	}

	@Override
	public ItemStack getDisplayItem() {
		AItemStackBuilder builder = new AItemStackBuilder(Material.TNT);
		ALoreBuilder loreBuilder = new ALoreBuilder("&7There are 2x bots in middle.",  "");
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
