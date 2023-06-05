package dev.kyro.pitsim.SQL;

import java.util.ArrayList;
import java.util.List;

public class TableManager {
	private static final List<SQLTable> tables = new ArrayList<>();

	public static void registerTables() {

		new SQLTable(ConnectionInfo.PLAYER_DATA, "DiscordAuthentication",
				new TableStructure(
						new TableColumn(String.class, "uuid", false, true),
						new TableColumn(Long.class, "discord_id", true),
						new TableColumn(String.class, "access_token"),
						new TableColumn(String.class, "refresh_token"),
						new TableColumn(Long.class, "last_refresh", true),
						new TableColumn(Long.class, "last_link", true),
						new TableColumn(Long.class, "last_boosting_claim", true)
				));

		new SQLTable(ConnectionInfo.PLAYER_DATA, "HelpRequests",
				new TableStructure(
						new TableColumn(String.class, "query", false, true, 255),
						new TableColumn(String.class, "intent", true)
				));
	}

	protected static void registerTable(SQLTable table) {
		tables.add(table);
	}

	public static SQLTable getTable(String tableName) {
		for(SQLTable table : tables) {
			if(table.tableName.equals(tableName)) return table;
		}
		return null;
	}
}
