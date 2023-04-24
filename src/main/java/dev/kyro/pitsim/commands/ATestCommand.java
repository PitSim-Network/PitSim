package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.misc.MinecraftSkin;
import net.minecraft.server.v1_8_R3.EntityItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ATestCommand implements CommandExecutor {

	public static List<EntityItem> items = new ArrayList<>();
	public static int degrees = 0;
	public static final double RADIUS = 0.5;
	public static final double TOTAL_ITEMS = 48;
	public static Location location;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.isOp()) return false;

		for(MinecraftSkin botSkin : NonManager.botSkins) {
			System.out.println(botSkin.skinName);
		}
//
		return false;
	}

	public ItemStack getItemStack() {
		ItemStack item = new ItemStack(Material.GHAST_TEAR);
		ItemMeta meta = item.getItemMeta();
		meta.setLore(Collections.singletonList(UUID.randomUUID().toString()));
		item.setItemMeta(meta);
		return item;
	}
}










