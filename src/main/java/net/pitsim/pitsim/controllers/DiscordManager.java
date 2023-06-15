package net.pitsim.pitsim.controllers;

import net.pitsim.pitsim.SQL.*;
import net.pitsim.pitsim.commands.ClaimCommand;
import net.pitsim.pitsim.controllers.objects.PluginMessage;
import net.pitsim.pitsim.events.MessageEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class DiscordManager implements Listener {
	public static final String DISCORD_TABLE = "DiscordAuthentication";

	public static long getLastBoostRewardClaim(UUID uuid) {
		SQLTable table = TableManager.getTable(DISCORD_TABLE);
		if(table == null) throw new RuntimeException("Discord table not registered!");

		ResultSet rs = table.selectRow(new Constraint("uuid", uuid.toString()), new Field("last_boosting_claim"));


		try {
			long returnVal = -1;
			if(rs.next()) {
				returnVal = rs.getLong("last_boosting_claim");
			}
			rs.close();
			return returnVal;
		} catch(SQLException e) {
			e.printStackTrace();
		}

		return Long.MAX_VALUE;
	}

	public static void setLastBoostRewardClaim(UUID uuid, long millis) {
		SQLTable table = TableManager.getTable(DISCORD_TABLE);
		if(table == null) throw new RuntimeException("Discord table not registered!");

		table.updateRow(new Constraint("uuid", uuid.toString()), new Value("last_boosting_claim", millis));
	}

	@EventHandler
	public void onMessage(MessageEvent event) {
		PluginMessage message = event.getMessage();
		List<String> strings = message.getStrings();
		List<Boolean> booleans = message.getBooleans();
		if(strings.isEmpty()) return;

		if(strings.get(0).equals("BOOSTER_CLAIM")) {
			UUID playerUUID = UUID.fromString(strings.get(1));
			Player player = Bukkit.getPlayer(playerUUID);
			boolean isBooster = booleans.get(0);
			if(player != null) ClaimCommand.callback(player, isBooster);
		}
	}
}
