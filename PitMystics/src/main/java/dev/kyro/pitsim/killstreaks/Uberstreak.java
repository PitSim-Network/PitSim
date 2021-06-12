package dev.kyro.pitsim.killstreaks;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.ItemManager;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.List;

public class Uberstreak extends Megastreak {

	public BukkitTask runnable;

	@Override
	public String getName() {
		return "&c&lUBER";
	}

	@Override
	public String getPrefix() {
		return "&c&lUBER";
	}

	@Override
	public List<String> getRefNames() {
		return Arrays.asList("uberstreak");
	}

	@Override
	public int getRequiredKills() {
		return 5;
	}

	public Uberstreak(PitPlayer pitPlayer) {
		super(pitPlayer);
	}

	@Override
	public void proc() {

		pitPlayer.player.playSound(pitPlayer.player.getLocation(), "mob.guardian.curse", 1000, 1);
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
				"&c&lMEGASTREAK!&7 " + pitPlayer.player.getDisplayName() + "&7 activated &d&lUBERSTREAK&7!"));
	}

	@Override
	public void reset() {

		if(!isOnMega()) return;
		MysticType mysticType;
		int rand = (int) (Math.random() * 3);
		switch(rand) {
			case 0:
				mysticType = MysticType.SWORD;
				break;
			case 1:
				mysticType = MysticType.BOW;
				break;
			default:
				mysticType = MysticType.PANTS;
				break;
		}

		ItemStack jewel = FreshCommand.getFreshItem(mysticType, PantColor.JEWEL);
		jewel = ItemManager.enableDropConfirm(jewel);
		assert jewel != null;
		NBTItem nbtItem = new NBTItem(jewel);
		nbtItem.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
		EnchantManager.setItemLore(nbtItem.getItem());

		AUtil.giveItemSafely(pitPlayer.player, nbtItem.getItem());
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
				"&d&lUBERDROP!&7 " + pitPlayer.player.getDisplayName() + "&7 obtained an &dUberdrop: &7" +
				"&3Hidden Jewel " + mysticType.displayName));
	}

	@Override
	public void kill() {

		if(!isOnMega()) return;
	}
}
