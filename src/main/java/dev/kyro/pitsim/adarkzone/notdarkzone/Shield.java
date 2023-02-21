package dev.kyro.pitsim.adarkzone.notdarkzone;

import com.google.cloud.firestore.annotation.Exclude;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enchants.tainted.znotcodeduncommon.Fortify;

import java.util.UUID;

public class Shield {
	@Exclude
	public UUID uuid;
	@Exclude
	public PitPlayer pitPlayer;

	private double shieldAmount = getMax();
	private boolean isActive = true;
	private int ticksUntilReactivation;

	public void init(PitPlayer pitPlayer) {
		this.uuid = pitPlayer.player.getUniqueId();
		this.pitPlayer = pitPlayer;
		System.out.println("init: " + pitPlayer);
	}

	@Exclude
	public int getDisplayAmount() {
		return (int) Math.ceil(shieldAmount);
	}

//	Only needed for calculations that require knowing the exact amount of health a player has total, such as for damage calcs
	@Exclude
	public double getPreciseAmount() {
		shieldAmount = Math.min(shieldAmount, getMax());
		return shieldAmount;
	}

	@Exclude
	public boolean isActive() {
		return isActive;
	}

	@Exclude
	public void addShield(double amount) {
		if(!isActive()) return;
		shieldAmount = Math.min(getPreciseAmount() + amount, getMax());
	}

//	Returns the remaining damage if the shield breaks;
	@Exclude
	public double damageShield(double amount, double multiplier) {
		if(!isActive()) return amount;
		double shieldAmount = getPreciseAmount();
		if(amount * multiplier >= shieldAmount) {
			deactivate();
			return (amount * multiplier - shieldAmount) / multiplier;
		} else {
			this.shieldAmount -= amount * multiplier;
		}
		return 0;
	}

	@Exclude
	public void deactivate() {
		if(!isActive()) return;
		isActive = false;
		shieldAmount = 0;
		ticksUntilReactivation = getInitialTicksUntilReactivation();
	}

	@Exclude
	public int getTicksUntilReactivation() {
		return ticksUntilReactivation;
	}

	@Exclude
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

	@Exclude
	public int getMax() {
		int maxShield = 100;
		if(pitPlayer != null) maxShield += Fortify.getShieldIncrease(pitPlayer.player);
		return maxShield;
	}

	@Exclude
	public int getInitialTicksUntilReactivation() {
//		TODO: Implement
		return 200;
	}
}
