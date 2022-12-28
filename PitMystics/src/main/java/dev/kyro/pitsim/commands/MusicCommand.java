package dev.kyro.pitsim.commands;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.pitmaps.XmasMap;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MusicCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		if(pitPlayer.musicDisabled) {
			pitPlayer.musicDisabled = false;
			player.sendMessage(ChatColor.GREEN + "Music enabled!");

			if(XmasMap.radio != null && !XmasMap.isListening(player.getUniqueId())) {
				XmasMap.addToRadio(player);
			}
		} else {
			pitPlayer.musicDisabled = true;
			player.sendMessage(ChatColor.RED + "Music disabled!");
			NoteBlockAPI.stopPlaying(player);

			if(XmasMap.radio != null) XmasMap.removeFromRadio(player);
		}
		return false;
	}
}
