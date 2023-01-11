package dev.kyro.pitsim.adarkzone.notdarkzone;

public class Shield {
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

	public int getMax() {
//		TODO: Implement
		return 100;
	}

	public boolean isActive() {
		return isActive;
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

	private int getInitialTicksUntilReactivation() {
//		TODO: Implement
		return 80;
	}
}
