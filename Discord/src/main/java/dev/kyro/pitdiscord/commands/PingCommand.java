package dev.kyro.pitdiscord.commands;

import dev.kyro.pitdiscord.controllers.DiscordCommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class PingCommand extends DiscordCommand {

	public PingCommand() {
		super("ping");
	}

	@Override
	public void execute(GuildMessageReceivedEvent event, List<String> args) {

		event.getChannel().sendMessage("Pong!").queue();
	}
}
