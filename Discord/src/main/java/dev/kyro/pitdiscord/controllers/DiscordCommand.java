package dev.kyro.pitdiscord.controllers;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public abstract class DiscordCommand {

    public String command;
    public List<String> aliases;

    public DiscordCommand(String command, String... aliases) {
        this.command = command;
        this.aliases = Arrays.asList(aliases);
    }

    public abstract void execute(GuildMessageReceivedEvent event, List<String> args);
}
