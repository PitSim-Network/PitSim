package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.ahelp.HelpManager;
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

		HelpManager.HelperAgent helperAgent = HelpManager.getAgent(player);
		helperAgent.detectIntent(String.join(" ", args));

//		PacketBlock packetBlock = new PacketBlock(Material.RED_ROSE, (byte) 1, player.getLocation());
//		packetBlock.setViewers(Collections.singletonList(player));
//		packetBlock.spawnBlock();
//		packetBlock.removeAfter(100);

//		location = player.getLocation();
//
//		new BukkitRunnable() {
//			@Override
//			public void run() {
//				double x = Math.cos(Math.toRadians(degrees)) * RADIUS;
//				double z = Math.sin(Math.toRadians(degrees)) * RADIUS;
//
//				World world = ((CraftWorld) (player.getWorld())).getHandle();
//				EntityItem entityItem = new EntityItem(world);
//				entityItem.setPosition(location.getX() + x, location.getY(), location.getZ() + z);
//				entityItem.setItemStack(CraftItemStack.asNMSCopy(getItemStack()));
//
//
//				PacketPlayOutSpawnEntity spawn = new PacketPlayOutSpawnEntity(entityItem, 1, 1);
//				((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawn);
//
//				PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(entityItem.getId(), entityItem.getDataWatcher(), true);
//				((CraftPlayer) player).getHandle().playerConnection.sendPacket(meta);
//
//				degrees += 15;
//				items.add(entityItem);
//
//				if(items.size() > TOTAL_ITEMS) {
//					EntityItem removeItem = items.remove(0);
//					PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(removeItem.getId());
//					((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroy);
//				}
//			}
//		}.runTaskTimer(PitSim.INSTANCE, 0, 1);

//
//		double radius = 5;
//
//		for(int i = 0; i < 360; i += 15) {
//
//
//			new BukkitRunnable() {
//				@Override
//				public void run() {
//
//				}
//			}.runTaskLater(PitSim.INSTANCE, 20);
//		}



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











//		ItemStack itemStack = player.getItemInHand();
//		player.setItemInHand(TaintedEnchanting.enchantItem(itemStack));

//		if(itemStack == null || !itemStack.hasItemMeta() || itemStack.getType() == Material.AIR) {
//			MarketGUI marketGUI = new MarketGUI(player);
//			marketGUI.open();
//			return true;
//		}