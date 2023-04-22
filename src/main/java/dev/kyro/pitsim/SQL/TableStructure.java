package dev.kyro.pitsim.SQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableStructure {

	List<TableColumn> columns = new ArrayList<>();

	public TableStructure(TableColumn... columns) {
		this.columns.addAll(Arrays.asList(columns));
	}

	protected void build(SQLTable table) {
		boolean exists = checkIfExists(table);
		StringBuilder initial = new StringBuilder((exists ? "ALTER TABLE " : "CREATE TABLE ") + table.tableName);

		if(exists) {
			List<TableColumn> existingColumns = getExistingColumns(table);
			List<String> columnStrings = new ArrayList<>();
			initial.append(" ");

			for(TableColumn column : columns) {

				TableColumn match = null;
				for(TableColumn existingColumn : existingColumns) {
					if(column.getType() == existingColumn.getType() && column.name.equals(existingColumn.name)) {
						match = existingColumn;
						break;
					}
				}

				if(match != null) {
					if(column.primaryKey != match.primaryKey) {
						if(!column.primaryKey) columnStrings.add("DROP INDEX IF EXISTS `PRIMARY`");
						else columnStrings.add("ADD PRIMARY KEY (" + column.name + ")");
					}

					if(column.notNull != match.notNull) {
						columnStrings.add("MODIFY COLUMN " + column.name + " " + getColumnTypeName(column) + " " +
								(column.notNull ? "NOT NULL" : "NULL"));
					}
					continue;
				}

				columnStrings.add("ADD COLUMN " + column.name + " " + getColumnTypeName(column) + " " +
						(column.primaryKey ? "PRIMARY KEY " : "") + (column.notNull ? "NOT NULL " : ""));
			}

			initial.append(String.join(", ", columnStrings));
		} else {
			initial.append("(");
			List<String> columnStrings = new ArrayList<>();
			for(TableColumn column : columns) {
				columnStrings.add(column.name + " " + getColumnTypeName(column) + " " +
						(column.primaryKey ? "PRIMARY KEY " : "") + (column.notNull ? "NOT NULL " : ""));
			}

			initial.append(String.join(", ", columnStrings));
			initial.append(")");
		}

		System.out.println(initial);

		try {
			PreparedStatement stmt = table.connection.prepareStatement(initial.toString());
			stmt.execute();

		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean checkIfExists(SQLTable table) {
		try {
			PreparedStatement check = table.connection.prepareStatement("ALTER TABLE " + table.tableName);
			check.execute();
		} catch(SQLException e) {
			return false;
		}
		return true;
	}

	private String getColumnTypeName(TableColumn column) {
		System.out.println(column.getType());
		Clazz clazz = Clazz.valueOf(column.getType().getSimpleName());
		switch(clazz) {
			case String:
				return "VARCHAR(50)";
			case Integer:
				return "INT";
			case Long:
				return "BIGINT";
			case Double:
				return "DOUBLE";
			case Float:
				return "FLOAT";
			case Boolean:
				return "BOOLEAN";
			default:
				throw new RuntimeException("Unknown type: " + clazz);
		}
	}

	private Class<?> getColumnClass(String name) {
		try {
			return Class.forName(name);
		} catch(ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private List<TableColumn> getExistingColumns(SQLTable table) {
		List<TableColumn> result = new ArrayList<>();
		try {
			PreparedStatement stmt = table.connection.prepareStatement("SELECT * FROM " + table.tableName + "");

			ResultSet rs = stmt.executeQuery();

			DatabaseMetaData dbm = table.connection.getMetaData();
			ResultSet columnRs = dbm.getPrimaryKeys(null, null, table.tableName);
			List<String> primaryColumns = new ArrayList<>();

			while(columnRs.next()) {
				primaryColumns.add(columnRs.getString("COLUMN_NAME"));
			}

			ResultSetMetaData metaData = rs.getMetaData();
			for(int i = 0; i < metaData.getColumnCount(); i++) {
				boolean primaryKey = primaryColumns.contains(metaData.getColumnName(i + 1));
				boolean notNull = metaData.isNullable(i + 1) == ResultSetMetaData.columnNoNulls;

				Class<?> clazz = getColumnClass(metaData.getColumnClassName(i + 1));
				TableColumn column = new TableColumn(clazz, metaData.getColumnName(i + 1), notNull, primaryKey);

				result.add(column);
			}

			return result;

		} catch(SQLException e) {
			throw new RuntimeException(e);
		}

	}

	enum Clazz {
		String,
		Integer,
		Long,
		Double,
		Float,
		Boolean;
	}
}


