package dev.kyro.pitsim.commands;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.aitems.misc.TokenOfAppreciation;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class KTestCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.isOp()) return false;

		System.out.println("test");

//		ProgressionGUI progressionGUI = new ProgressionGUI(player);
//		progressionGUI.open();

//		giveToken(player, 1);
		player.sendMessage("recreating item");
		ItemStack newStack = ItemFactory.getItem(TokenOfAppreciation.class).getReplacementItem(PitPlayer.getPitPlayer(player),
				player.getItemInHand(), new NBTItem(player.getItemInHand()));
		player.getInventory().addItem(newStack);

		return false;
	}

	public static void giveToken(Player player, int amount) {
		ItemStack vile = new ItemStack(Material.MAGMA_CREAM);
		ItemMeta meta = vile.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD + "Token of Appreciation");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.YELLOW + "Special item");
		lore.add(ChatColor.GRAY + "A token of appreciation for understanding");
		lore.add(ChatColor.GRAY + "why we have to reset the PitSim economy.");
		lore.add(ChatColor.GRAY + "Who knows, this might do something some day.");
		lore.add("");
		String loresMessage = ChatColor.translateAlternateColorCodes('&',
				"&7To: &8[%luckperms_primary_group_name%&8] %luckperms_prefix%" + player.getName());
		lore.add(PlaceholderAPI.setPlaceholders(player, loresMessage));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7From: &8[&9Dev&8] &9wiji1"));
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		vile.setItemMeta(meta);
		vile.setAmount(amount);

		NBTItem nbtItem = new NBTItem(vile);
		nbtItem.setBoolean(NBTTag.IS_TOKEN.getRef(), true);
		AUtil.giveItemSafely(player, nbtItem.getItem());
	}
}