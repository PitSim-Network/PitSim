package dev.kyro.pitsim.killstreaks;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
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
	public void proc() {

		pitPlayer.player.getWorld().playSound(pitPlayer.player.getLocation(), Sound.WITHER_SPAWN, 1000, 1);
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				Misc.applyPotionEffect(pitPlayer.player, PotionEffectType.SPEED, 200, 0, true, false);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 60L);
	}

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
