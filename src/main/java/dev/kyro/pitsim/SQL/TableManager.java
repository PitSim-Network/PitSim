package dev.kyro.pitsim.SQL;

import java.util.ArrayList;
import java.util.List;

public class TableManager {
	private static final List<SQLTable> tables = new ArrayList<>();

	public static void registerTables() {

		new SQLTable(ConnectionInfo.DEVELOPMENT, "development",
				new TableStructure(
						new TableColumn(String.class, "uuid"),
						new TableColumn(Integer.class, "xp"),
						new TableColumn(Long.class, "last_login"),
						new TableColumn(Boolean.class, "is_staff"),
						new TableColumn(Integer.class, "souls")
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
