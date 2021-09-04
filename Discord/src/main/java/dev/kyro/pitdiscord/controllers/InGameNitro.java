package dev.kyro.pitdiscord.controllers;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.pitdiscord.Constants;
import dev.kyro.pitdiscord.DiscordPlugin;
import dev.kyro.pitsim.controllers.NonManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeEqualityPredicate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class InGameNitro implements Listener {
	public Role nitroRole = DiscordManager.GUILD.getRoleById(Constants.NITRO_ROLE_ID);

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {

		APlayerData.getPlayerData(event.getPlayer());
	}

	public InGameNitro() {

		new BukkitRunnable() {
			@Override
			public void run() {
				List<Member> members = DiscordManager.GUILD.findMembers(member -> member.getRoles().contains(nitroRole)).get();
				List<String> memberIGNs = new ArrayList<>();

				for(Member member : members) {

					UUID playerUUID = null;
					for(Map.Entry<UUID, FileConfiguration> entry : APlayerData.getAllData().entrySet()) {
						if(!entry.getValue().getString("name").equals(member.getEffectiveName())) continue;
						playerUUID = entry.getKey();
						break;
					}
					if(playerUUID == null) continue;
					memberIGNs.add(member.getEffectiveName());

					User luckPermsUser = DiscordPlugin.LUCKPERMS.getUserManager().getUser(playerUUID);
					if(luckPermsUser == null) continue;
					Node node = Node.builder("group.nitro")
							.value(true)
							.expiry(Duration.ofMinutes(12))
							.build();

					for(Node playerNode : luckPermsUser.getNodes()) {
						if(!playerNode.equals(node, NodeEqualityPredicate.ONLY_KEY)) continue;
						try {
							DiscordPlugin.LUCKPERMS.getUserManager().modifyUser(playerUUID, user -> user.data().remove(playerNode)).get();
						} catch(InterruptedException | ExecutionException e) {
							e.printStackTrace();
						}
					}

					DiscordPlugin.LUCKPERMS.getUserManager().modifyUser(playerUUID, user -> user.data().add(node));
				}

				try {
					NonManager.updateNons(memberIGNs);
				} catch(Exception ignored) { }
			}
		}.runTaskTimer(DiscordPlugin.INSTANCE, 0L, 10 * 60 * 20L);
	}
}
