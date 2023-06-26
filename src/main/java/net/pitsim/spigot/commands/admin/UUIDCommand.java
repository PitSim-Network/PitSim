package net.pitsim.spigot.commands.admin;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import net.pitsim.spigot.enums.NBTTag;
import net.pitsim.spigot.misc.Misc;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class UUIDCommand extends ACommand {
	public UUIDCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;

		ItemStack itemStack = player.getItemInHand();
		if(Misc.isAirOrNull(itemStack)) return;

		NBTItem nbtItem = new NBTItem(itemStack);
		if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) return;
		UUID uuid = UUID.fromString(nbtItem.getString(NBTTag.ITEM_UUID.getRef()));

		TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&',
				"&6&lUUID!&7 " + nbtItem.getString(NBTTag.ITEM_UUID.getRef())));
		message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, uuid.toString()));
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
				ChatColor.translateAlternateColorCodes('&', "&7Click to copy")).create()));
		player.spigot().sendMessage(message);
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}
}
