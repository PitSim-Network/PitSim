package dev.kyro.pitsim.misc;

public class MutableInteger {
	private int value;

	public MutableInteger(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public void increment() {
		increment(1);
	}

	public void increment(int amount) {
		value += amount;
	}

	public void decrement() {
		decrement(1);
	}

	public void decrement(int amount) {
		value -= amount;
	}
}