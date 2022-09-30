package dev.kyro.pitdiscord.commands;

import dev.kyro.pitdiscord.Constants;
import dev.kyro.pitdiscord.DiscordPlugin;
import dev.kyro.pitdiscord.controllers.DiscordCommand;
import dev.kyro.pitdiscord.controllers.DiscordManager;
import dev.kyro.pitsim.controllers.LockdownManager;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//This is disabled
public class VerifyCommand extends DiscordCommand {
	public static List<Long> recentVerificationPlayers = new ArrayList<>();

	public VerifyCommand() {
		super("verify");
	}

	@Override
	public void execute(GuildMessageReceivedEvent event, List<String> args) {

		if(recentVerificationPlayers.contains(event.getAuthor().getIdLong())) {
			event.getChannel().sendMessage("Please wait before using this command again").queue();
			return;
		}

		if(args.size() < 1) {
			event.getChannel().sendMessage("Usage: .verify <ign/uuid>").queue();
			return;
		}
		String nameUUID = args.get(0);

		boolean wasVerified = LockdownManager.removeVerifiedPlayer(event.getAuthor().getIdLong());
		boolean verificationSuccessful = LockdownManager.verify(nameUUID.toLowerCase(), event.getAuthor().getIdLong());

		if(!verificationSuccessful) {
			event.getChannel().sendMessage("That player was not found").queue();
			return;
		}

		if(wasVerified) {
			event.getChannel().sendMessage("Successfully verified! (Your previously verified account was removed)").queue();
		} else {
			event.getChannel().sendMessage("Successfully verified!").queue();
		}

		recentVerificationPlayers.add(event.getAuthor().getIdLong());
		new BukkitRunnable() {
			@Override
			public void run() {
				recentVerificationPlayers.remove(event.getAuthor().getIdLong());
			}
		}.runTaskLater(DiscordPlugin.INSTANCE, 20 * 60 * 60);

		try {
			Objects.requireNonNull(DiscordManager.JDA.getTextChannelById(Constants.VERIFICATION_LOG_CHANNEL))
					.sendMessage("Discord: `" + event.getAuthor().getAsTag() + "`" +
							"\nIGN/UUID: `" + nameUUID.toLowerCase() + "`" +
							"\nWas Verified: " + (wasVerified ? "`Yes`" : "`No`")).queue();
		} catch(Exception ignored) {
			System.out.println("verification channel does not exist");
		}
	}
}
