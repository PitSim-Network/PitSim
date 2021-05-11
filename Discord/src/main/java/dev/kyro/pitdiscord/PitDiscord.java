package dev.kyro.pitdiscord;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class PitDiscord extends ListenerAdapter {

	public final String prefix = ".";

	public PitDiscord() {

		JDABuilder builder = JDABuilder.createDefault("***REMOVED***")
				.setBulkDeleteSplittingEnabled(false)
				.setActivity(Activity.playing("pitsim.mcpro.io"));
		builder.addEventListeners(this);

		try {
			builder.build();
		} catch(Exception ignored) {}
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Message message = event.getMessage();

		if(message.getContentRaw().equalsIgnoreCase(prefix + "rape")) {

			for(Player player : Bukkit.getOnlinePlayers()) {

				final ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
				final String command = "ban " + player.getName() + " 10s you were raped";
				Bukkit.dispatchCommand(console, command);
			}

			message.getChannel().sendMessage("\u2705 Successfully raped **" + Bukkit.getOnlinePlayers().size() + "** players").queue();
		}
	}
}
