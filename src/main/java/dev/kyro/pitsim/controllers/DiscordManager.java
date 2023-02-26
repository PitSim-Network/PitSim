package dev.kyro.pitsim.controllers;

import org.bukkit.event.Listener;

import java.sql.*;
import java.util.UUID;

public class DiscordManager implements Listener {
	public static final String DISCORD_TABLE = "DiscordAuthentication";

	public static Connection getConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String dbUrl = "jdbc:mysql://sql.pitsim.net:3306/s9_PlayerData";
			String username = "***REMOVED***";
			String password = "***REMOVED***";
			return DriverManager.getConnection(dbUrl, username, password);
		} catch(Exception ignored) {} ;
		return null;
	}

	public static long getLastBoostRewardClaim(UUID uuid) {
		Connection connection = getConnection();
		assert connection != null;

		try {
			String sql = "SELECT last_boosting_claim FROM " + DISCORD_TABLE + " WHERE uuid = ?";
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setString(1, uuid.toString());
			ResultSet rs = stmt.executeQuery();

			if(rs.next()) {
				return rs.getLong("last_boosting_claim");
			} else return -1;
		} catch(SQLException e) {
			e.printStackTrace();
		}

		try {
			connection.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}

		return -1;
	}

	public static void setLastBoostRewardClaim(UUID uuid, long millis) {
		Connection connection = DiscordManager.getConnection();

		try {
			String sql = "UPDATE " + DISCORD_TABLE + " SET last_boosting_claim = ? WHERE uuid = ?";

			assert connection != null;

			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setLong(1, millis);
			stmt.setString(2, uuid.toString());

			stmt.executeUpdate();
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}

		try {
			connection.close();
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isBoosting(UUID uuid) {

		return false;
	}
}
