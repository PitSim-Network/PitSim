package dev.kyro.pitsim.SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SQLTable {
	public TableStructure structure;
	public ConnectionInfo connectionInfo;
	public String tableName;
	public Connection connection;

	public SQLTable(ConnectionInfo connectionInfo, String tableName, TableStructure structure) {
		if(TableManager.getTable(tableName) != null) throw new RuntimeException("Table already exists");

		this.structure = structure;
		this.connectionInfo = connectionInfo;
		this.tableName = tableName;

		this.connection = connectionInfo.getConnection();
		structure.build(this);

		TableManager.registerTable(this);
	}

	public void executeUpdate(String query) {
		try {
			PreparedStatement stmt = connection.prepareStatement(query);
			executeUpdate(stmt);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void executeUpdate(PreparedStatement statement) {
		try {
			statement.executeUpdate();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ResultSet executeQuery(String query) {
		try {
			return executeQuery(connection.prepareStatement(query));
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ResultSet executeQuery(PreparedStatement statement) {
		try {
			return statement.executeQuery();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void insertRow(Object... values) {
		List<String> columnStrings = new ArrayList<>();
		for(TableColumn column : structure.columns) {
			columnStrings.add(column.type.getSimpleName() + " " + column.name);
		}

		if(values.length != structure.columns.size()) throw new RuntimeException("Invalid number of values");

		StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " VALUES (");
		for(int i = 0; i < values.length; i++) {
			if(values[i].getClass() != structure.columns.get(i).type)
				throw new RuntimeException("\nCorrect values: " + String.join(", ", columnStrings));

			query.append("?");
			if(i != values.length - 1) query.append(", ");
		}
		query.append(")");

		try {
			PreparedStatement stmt = connection.prepareStatement(query.toString());
			for(int i = 0; i < values.length; i++) {
				stmt.setObject(i + 1, values[i]);
			}
			executeUpdate(stmt);
		} catch(Exception e) {
			throw new RuntimeException(e + "\nCorrect values: " + String.join(", ", columnStrings));
		}
	}

	public void close() {
		try {
			connection.close();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

}
