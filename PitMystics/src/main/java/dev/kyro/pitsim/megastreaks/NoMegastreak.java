package dev.kyro.pitsim.megastreaks;

import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NoMegastreak extends Megastreak {

	public BukkitTask runnable;

	@Override
	public String getName() {
		return "";
	}

	@Override
	public String getRawName() {
		return "No Megastreak";
	}

	@Override
	public String getPrefix() {
		return "";
	}

	@Override
	public List<String> getRefNames() {
		return Arrays.asList("nomegastreak");
	}

	@Override
	public int getRequiredKills() {
		return Integer.MAX_VALUE;
	}

	@Override
	public int guiSlot() {
		return 10;
	}

	@Override
	public int prestigeReq() {
		return 0;
	}

	@Override
	public int levelReq() {
		return 0;
	}

	@Override
	public ItemStack guiItem() {
		ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GRAY + "Want high streaks with no rewards");
		lore.add(ChatColor.GRAY + "but no debuffs?");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public NoMegastreak(PitPlayer pitPlayer) {
		super(pitPlayer);
	}

	@Override
	public void proc() { }

	@Override
	public void reset() {

		if(runnable != null) runnable.cancel();
	}

	@Override
	public void stop() {
		HandlerList.unregisterAll(this);
	}

	@Override
	public void kill() {

		if(!isOnMega()) return;
	}
}
