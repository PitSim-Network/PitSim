package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class Celebrity extends RenownUpgrade {
	public Celebrity() {
		super("Celebrity", "CELEBRITY", 300, 40, 40, false, 0);
	}

	@Override
	public ItemStack getDisplayItem(Player player) {
		ItemStack item = new ItemStack(Material.RAW_FISH, 1, (short) 3);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(UpgradeManager.itemNameString(this, player));
		List<String> lore = new ALoreBuilder("&7Literally earn &62x gold &7from", "&7kills").getLore();
		meta.setLore(UpgradeManager.loreBuilder(this, player, lore, false));
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(10, 50);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer()) return;
		if(!UpgradeManager.hasUpgrade(killEvent.getKillerPlayer(), this)) return;

		killEvent.goldMultipliers.add(2.0);
	}

	@Override
	public String getSummary() {
		return "&6Celebirty is an &erenown&7 upgrade that doubles the &6gold&7 you get on all bot and player kills";
	}
}
