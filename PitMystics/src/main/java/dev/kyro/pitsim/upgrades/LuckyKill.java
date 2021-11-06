package dev.kyro.pitsim.upgrades;

import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public  class LuckyKill extends RenownUpgrade {
	public LuckyKill() {
		super("Lucky Kill", "LUCKY_KILL", 10, 13, 5, true, 4);
	}

	@Override
	public ItemStack getDisplayItem(Player player, boolean isCustomPanel) {
		ItemStack item = new ItemStack(Material.NAME_TAG);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(UpgradeManager.itemNameString(this, player));
		meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		List<String> lore = new ArrayList<>();
		if(UpgradeManager.hasUpgrade(player, this)) lore.add(ChatColor.translateAlternateColorCodes('&',
				"&7Current: &e" + UpgradeManager.getTier(player, this) + "&e% &7chance"));
		if(UpgradeManager.hasUpgrade(player, this)) lore.add(ChatColor.GRAY + "Tier: " + ChatColor.GREEN + AUtil.toRoman(UpgradeManager.getTier(player, this)));
		if(UpgradeManager.hasUpgrade(player, this)) lore.add("");
		lore.add(ChatColor.GRAY + "Each tier:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Gain &f+1% &7chance when getting a"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7player kill to make it a &dLucky Kill&7."));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7(Triples all kill rewards including"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7bounties and kill requirement)"));
		meta.setLore(UpgradeManager.loreBuilder(this, player, lore, isCustomPanel));
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public AGUIPanel getCustomPanel() {return null;}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(10, 20, 30, 40);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!UpgradeManager.hasUpgrade(killEvent.killer, this)) return;
		if(!(NonManager.getNon(killEvent.dead) == null)) return;

		int tier = UpgradeManager.getTier(killEvent.killer, this);
		if(tier == 0) return;

		double chance = 0.01 * tier;

		boolean isLuckyKill = Math.random() < chance;

		if(isLuckyKill) killEvent.isLuckyKill = true;

		if(isLuckyKill) {
			AOutput.send(killEvent.killer, "&d&lLUCKY KILL! &7Rewards tripled!");

			File file = new File("plugins/NoteBlockAPI/Effects/LuckyKill.nbs");
			Song song = NBSDecoder.parse(file);
			RadioSongPlayer rsp = new RadioSongPlayer(song);
			rsp.setRepeatMode(RepeatMode.NO);
			rsp.addPlayer(killEvent.killer);
			rsp.setPlaying(true);
		}


	}
}
