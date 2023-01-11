package dev.kyro.pitsim.adarkzone.notdarkzone;

public class Shield {
	public static final int ACTIVE_REGEN_TICKS = 40;
	public static final double ACTIVE_REGEN_AMOUNT = 5;

	private double shieldAmount = getMax();
	private boolean isActive = true;
	private int tickUntilReactivation;

	public int getAmount() {
		return (int) Math.ceil(shieldAmount);
	}

//	Only needed for calculations that require knowing the exact amount of health a player has total, such as for damage calcs
	public double getPreciseAmount() {
		return shieldAmount;
	}

	public boolean isActive() {
		return isActive;
	}

	public void addShield(double amount) {
		if(!isActive()) return;
		shieldAmount += amount;
	}

	public void damageShield(double amount) {
		if(!isActive()) return;
		if(amount >= shieldAmount) {
			deactivate();
		} else {
			shieldAmount -= amount;
		}
	}

	public void deactivate() {
		if(!isActive()) return;
		isActive = false;
		shieldAmount = 0;
		tickUntilReactivation = getInitialTicksUntilReactivation();
	}

	public void regenerateTick() {
		if(isActive()) return;
		if(tickUntilReactivation > 1) {
			tickUntilReactivation--;
			return;
		}

		tickUntilReactivation = 0;
		isActive = true;
		shieldAmount = getMax();
	}

	public int getMax() {
//		TODO: Implement
		return 100;
	}

	private int getInitialTicksUntilReactivation() {
//		TODO: Implement
		return 80;
	}
}
