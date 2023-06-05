package dev.kyro.pitsim.SQL;

 public class TableColumn {

	public Class<?> type;
	public String name;
	public boolean notNull;
	public boolean primaryKey;
	public int varcharLength;

	public TableColumn(Class<?> type, String name) {
		this(type, name, false);
	}

	public TableColumn(Class<?> type, String name, boolean notNull) {
		this(type, name, notNull, false);
	}

	public TableColumn(Class<?> type, String name, boolean notNull, boolean primaryKey) {
		this(type, name, notNull, primaryKey, 50);
	}

	 public TableColumn(Class<?> type, String name, boolean notNull, boolean primaryKey, int varcharLength) {
		 this.type = type;
		 this.name = name;
		 this.notNull = notNull;
		 this.primaryKey = primaryKey;
		 this.varcharLength = varcharLength;
	 }

	public Class<?> getType() {
		return type;
	}
}
