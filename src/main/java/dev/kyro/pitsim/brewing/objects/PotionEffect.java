package dev.kyro.pitsim.brewing.objects;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class PotionEffect implements Listener {
	public Player player;
	public BrewingIngredient potionType;
	public BrewingIngredient potency;
	public BrewingIngredient duration;
	public int durationOverride;
	public long creationTime;

	public int ticksLeft;

	public PotionEffect(Player player, BrewingIngredient potionType, BrewingIngredient potency, BrewingIngredient duration) {
		this.player = player;
		this.potionType = potionType;
		this.potency = potency;
		this.duration = duration;
		this.creationTime = System.currentTimeMillis();

		ticksLeft = potionType.getDuration(duration);
		AOutput.send(player, "&5&lPOTION!&7 Effected with " + potionType.color + potionType.name + " " +
				AUtil.toRoman(potency.tier) + " &7for &f" + Misc.ticksToTime(ticksLeft));
		potionType.administerEffect(player, potency, getTimeLeft());
	}

	public PotionEffect(Player player, BrewingIngredient potionType, BrewingIngredient potency, int durationOverride) {
		this.player = player;
		this.potionType = potionType;
		this.potency = potency;
		this.durationOverride = durationOverride;

		ticksLeft = durationOverride;
		AOutput.send(player, "&5&lPOTION!&7 Effected with " + potionType.color + potionType.name + " " +
				AUtil.toRoman(potency.tier) + " &7for &f" + Misc.ticksToTime(ticksLeft));
		potionType.administerEffect(player, potency, getTimeLeft());
	}

	public void tick() {
		ticksLeft--;
		potionType.administerEffect(player, potency, getTimeLeft());
		if(getTimeLeft() == 0) onExpire(false);
	}

	public int getTimeLeft() {
		return ticksLeft;
	}

	public int getOriginalTime() {
		return durationOverride == 0 ? potionType.getDuration(duration) : durationOverride;
	}

	public void onExpire(boolean hideMessage) {
		if(!hideMessage) AOutput.send(player, "&5&lPOTION! " + potionType.color + potionType.name + " &7has expired");
		PotionManager.potionEffectList.remove(this);
		if(PotionManager.playerIndex.containsKey(player) && PotionManager.playerIndex.get(player) > 0)
			PotionManager.playerIndex.put(player, PotionManager.playerIndex.get(player) - 1);
		else PotionManager.playerIndex.remove(player);
		if(PotionManager.getPotionEffects(player).size() == 0)
			PotionManager.hideActiveBossBar(PitSim.adventure.player(player), player);
	}
}
