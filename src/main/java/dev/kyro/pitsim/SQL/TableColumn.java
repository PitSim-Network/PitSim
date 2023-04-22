package dev.kyro.pitsim.SQL;

 public class TableColumn {

	public Class<?> type;
	public String name;
	public boolean notNull;
	public boolean primaryKey;

	public TableColumn(Class<?> type, String name) {
		this(type, name, false);
	}

	public TableColumn(Class<?> type, String name, boolean notNull) {
		this(type, name, notNull, false);
	}

	public TableColumn(Class<?> type, String name, boolean notNull, boolean primaryKey) {
		this.type = type;
		this.name = name;
		this.notNull = notNull;
		this.primaryKey = primaryKey;
	}

	public Class<?> getType() {
		return type;
	}
}
