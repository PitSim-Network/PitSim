package dev.kyro.pitsim.adarkzone.notdarkzone;

public class Shield {
	private double shieldAmount = getMax();
	private boolean isActive = true;
	private int ticksUntilReactivation;

	public int getDisplayAmount() {
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
		shieldAmount = Math.min(shieldAmount + amount, getMax());
	}

//	Returns the remaining damage if the shield breaks;
	public double damageShield(double amount) {
		if(!isActive()) return amount;
		if(amount >= shieldAmount) {
			deactivate();
			return amount - shieldAmount;
		} else {
			shieldAmount -= amount;
		}
		return 0;
	}

	public void deactivate() {
		if(!isActive()) return;
		isActive = false;
		shieldAmount = 0;
		ticksUntilReactivation = getInitialTicksUntilReactivation();
	}

	public int getTicksUntilReactivation() {
		return ticksUntilReactivation;
	}

	public void regenerateTick() {
		if(isActive()) return;
		if(ticksUntilReactivation > 1) {
			ticksUntilReactivation--;
			return;
		}

		ticksUntilReactivation = 0;
		isActive = true;
		shieldAmount = getMax();
	}

	public int getMax() {
//		TODO: Implement
		return 100;
	}

	public int getInitialTicksUntilReactivation() {
//		TODO: Implement
		return 200;
	}
}
