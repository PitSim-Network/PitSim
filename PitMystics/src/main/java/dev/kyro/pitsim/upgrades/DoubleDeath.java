package dev.kyro.pitsim.upgrades;

import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DoubleDeath extends RenownUpgrade {
	public static DoubleDeath INSTANCE;
	public DoubleDeath() {
		super("Double-Death", "DOUBLE_DEATH", 15, 16, 9, true, 4);
		INSTANCE = this;
	}

	@Override
	public ItemStack getDisplayItem(Player player, boolean isCustomPanel) {
		ItemStack item = new ItemStack(Material.SKULL_ITEM);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(UpgradeManager.itemNameString(this, player));
		List<String> lore = new ArrayList<>();
		if(UpgradeManager.hasUpgrade(player, this)) lore.add(ChatColor.translateAlternateColorCodes('&',
				"&7Current: &d+" + 5 * UpgradeManager.getTier(player, this) + "% Chance"));
		if(UpgradeManager.hasUpgrade(player, this)) lore.add(ChatColor.GRAY + "Tier: " + ChatColor.GREEN + AUtil.toRoman(UpgradeManager.getTier(player, this)));
		if(UpgradeManager.hasUpgrade(player, this)) lore.add("");
		lore.add(ChatColor.GRAY + "Each tier:");
		lore.add(ChatColor.GRAY + "Gain " + ChatColor.LIGHT_PURPLE + "+5% " + ChatColor.GRAY + "chance to double");
		lore.add(ChatColor.GRAY + "megastreak death rewards.");
		lore.add(ChatColor.GRAY + "&7Does not work when you have the killstreak: \"uberstreak\" equipped on the PitSim network");
		meta.setLore(UpgradeManager.loreBuilder(this, player, lore, isCustomPanel));
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public AGUIPanel getCustomPanel() {return null;}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(5, 5, 10, 10);
	}

	public boolean isDoubleDeath(Player player) {
		if(!UpgradeManager.hasUpgrade(player, this)) return false;

		int tier = UpgradeManager.getTier(player, this);
		if(tier == 0) return false;

		double chance = 0.01 * (tier * 5);

		boolean isDouble = Math.random() < chance;

		if(isDouble) {
			AOutput.send(player, "&d&lDOUBLE DEATH! &7Megastreak death rewards doubled!");

			File file = new File("plugins/NoteBlockAPI/Effects/DoubleDeath.nbs");
			Song song = NBSDecoder.parse(file);
			RadioSongPlayer rsp = new RadioSongPlayer(song);
			rsp.setRepeatMode(RepeatMode.NO);
			rsp.addPlayer(player);
			rsp.setPlaying(true);
		}

		return isDouble;
	}

}
