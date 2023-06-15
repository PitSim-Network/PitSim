package net.pitsim.pitsim.exceptions;

public class InvalidEnchantLevelException extends PitException {

	public boolean levelTooHigh;

	public InvalidEnchantLevelException(boolean levelTooHigh) {
		this.levelTooHigh = levelTooHigh;
	}
}
