package dev.kyro.pitsim.boosters;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Booster;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PvPBooster extends Booster {
	public PvPBooster() {
		super("PvP Booster", "pvp", 14, ChatColor.RED);
	}

//	@EventHandler(priority = EventPriority.HIGH)
//	public void onKill(KillEvent killEvent) {
//		if(!isActive()) return;
//		killEvent.playerKillWorth *= 2;
//	}

	@Override
	public List<String> getDescription() {
		return null;
	}

	@Override
	public ItemStack getDisplayItem() {
		AItemStackBuilder builder = new AItemStackBuilder(Material.GOLD_SWORD);
		ALoreBuilder loreBuilder = new ALoreBuilder("&7Don't loose lives on &3Jewel", "&3Items&7.", "");
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
