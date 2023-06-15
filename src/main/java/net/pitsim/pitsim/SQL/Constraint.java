package net.pitsim.pitsim.SQL;

public class Constraint extends QueryStorage {
	public String operator;

	public Constraint(String fieldName, Object value) {
		super(fieldName, value);
	}

	public Constraint(String fieldName, Object value, String operator) {
		super(fieldName, value);
		this.operator = operator;
	}
}
