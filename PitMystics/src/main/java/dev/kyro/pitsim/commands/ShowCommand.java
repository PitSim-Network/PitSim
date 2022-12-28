package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.misc.CustomSerializer;
import dev.kyro.pitsim.misc.Misc;
import litebans.api.Database;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ShowCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;

		Player player = (Player) sender;

		if(!player.hasPermission("pitsim.show") && !player.isOp()) {
			AOutput.error(player, "&cInsufficient Permissions");
			return false;
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				String ip = player.getAddress().getAddress().getHostAddress();
				boolean isMuted = Database.get().isPlayerMuted(player.getUniqueId(), ip);
				if(isMuted) {
					AOutput.error(player, "&cYou are muted!");
					return;
				}

				new BukkitRunnable() {
					@Override
					public void run() {
						ItemStack itemStack = player.getItemInHand();
						if(Misc.isAirOrNull(itemStack)) {
							AOutput.error(player, "&c&lERROR!&7 You are not holding an item");
							return;
						}
						EnchantManager.setItemLore(itemStack, player);
						player.setItemInHand(itemStack);
						player.updateInventory();

						sendShowMessage(Misc.getDisplayName(player), itemStack);

						new PluginMessage()
								.writeString("ITEMSHOW")
								.writeString(PitSim.serverName)
								.writeString(Misc.getDisplayName(player))
								.writeString(CustomSerializer.serialize(player.getItemInHand()))
								.send();
					}
				}.runTask(PitSim.INSTANCE);
			}
		}.runTaskAsynchronously(PitSim.INSTANCE);
		return false;
	}

	public static void sendShowMessage(String displayName, ItemStack itemStack) {
		TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6SHOWOFF! " + displayName + " &7shows off their "));
		message.addExtra(Misc.createItemHover(itemStack));

		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
			if(!pitPlayer.playerChatDisabled) onlinePlayer.spigot().sendMessage(message);
		}
	}
}
