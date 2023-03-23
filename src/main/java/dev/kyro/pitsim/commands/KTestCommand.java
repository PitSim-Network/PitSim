package dev.kyro.pitsim.commands;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.objects.FakeItem;
import dev.kyro.pitsim.enums.NBTTag;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

		new FakeItem(player.getItemInHand().clone(), player.getLocation().add(player.getLocation().getDirection().multiply(3)))
				.removeAfter(100)
				.onPickup((pickupPlayer, itemStack) -> {
					pickupPlayer.getInventory().addItem(itemStack);
					pickupPlayer.updateInventory();
				})
				.addViewer(player);

//		ProgressionGUI progressionGUI = new ProgressionGUI(player);
//		progressionGUI.open();

//		for(Block block : getNearbyBlocks(player.getLocation(), 100)) {
//			Block blockBelow = block.getRelative(0, -1, 0);
//			if(blockBelow == null || blockBelow.getType() != Material.AIR) continue;
//			if(Math.random() > 0.5) continue;
//
//			new BukkitRunnable() {
//				@Override
//				public void run() {
//					new PacketBlock(Material.AIR, (byte) 0, block.getLocation())
//							.setViewers(player)
//							.spawnBlock()
//							.removeAfter(30 * 20);
//
//					Location spawnLocation = block.getLocation().add(0.5, 0.5, 0.5);
//					new FallingBlock(block.getType(), block.getData(), spawnLocation)
//							.setViewers(player)
//							.spawnBlock()
//							.removeAfter(40 + new Random().nextInt(41));
//				}
//			}.runTaskLater(PitSim.INSTANCE, new Random().nextInt(30 * 20));
//		}

//		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
//		ItemStack itemStack = player.getItemInHand();
//		NBTItem nbtItem = new NBTItem(itemStack);
//
//		String string = CraftItemStack.asNMSCopy(itemStack).getTag().toString();
//		System.out.println(string);
//		player.sendMessage(ChatColor.stripColor(string));
//
//		MysticSword pitItem = ItemFactory.getItem(MysticSword.class);
//		ItemStack newStack = pitItem.getReplacementItem(pitPlayer, itemStack, nbtItem);
//		pitItem.updateItem(newStack);
//		player.getInventory().addItem(newStack);
		return false;
	}

	public static List<Block> getNearbyBlocks(Location location, int radius) {
		List<Block> blocks = new ArrayList<>();
		for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
			for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
				for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
					Block block = location.getWorld().getBlockAt(x, y, z);
					if(block == null || block.getType() == Material.AIR) continue;
					blocks.add(block);
				}
			}
		}
		return blocks;
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