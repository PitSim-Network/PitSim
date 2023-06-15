package net.pitsim.pitsim.commands.admin;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.misc.Misc;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NBTCommand extends ACommand {
	public NBTCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;

		ItemStack itemStack = player.getItemInHand();
		if(Misc.isAirOrNull(itemStack)) return;

		NBTItem nbtItem = new NBTItem(itemStack);
		NBTTagCompound nbtTagCompound = (NBTTagCompound) nbtItem.getCompound();
		System.out.println(nbtItem);

		List<String> messages = new ArrayList<>();
		encodeNBTCompound(messages, nbtTagCompound, 0);
		AOutput.send(player, "&7");
		for(String line : messages) {
			AOutput.send(player, "&7" + line);
		}
		AOutput.send(player, "&7");
	}

	public static void encodeNBTBase(List<String> messages, String compoundName, NBTBase nbtBase, int layer) {
		String prefix = "";
		for(int i = 0; i < layer; i++) prefix += "  ";
		if(nbtBase instanceof NBTTagCompound) {
			messages.add(prefix + compoundName + ":");
			encodeNBTCompound(messages, (NBTTagCompound) nbtBase, layer + 1);
		} else if(nbtBase instanceof NBTTagList) {
			messages.add(prefix + compoundName + ":");
			encodeNBTList(messages, (NBTTagList) nbtBase, layer + 1);
		} else {
			if(nbtBase instanceof NBTTagString) {
				NBTTagString nbtTagString = (NBTTagString) nbtBase;
				messages.add(prefix + compoundName + ": &f" + nbtTagString.a_());
			} else {
				messages.add(prefix + compoundName + ": &f" + nbtBase.toString());
			}
		}
	}

	public static void encodeNBTCompound(List<String> messages, NBTTagCompound compound, int layer) {
		for(String key : compound.c()) {
			NBTBase nbtBase = compound.get(key);
			encodeNBTBase(messages, key, nbtBase, layer);
		}
	}

	public static void encodeNBTList(List<String> messages, NBTTagList list, int layer) {
		for(int i = 0; i < list.size(); i++) {
			NBTBase nbtBase = list.g(i);
			encodeNBTBase(messages, i + "", nbtBase, layer);
		}
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}
}
