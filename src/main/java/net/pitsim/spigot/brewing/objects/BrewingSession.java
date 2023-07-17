package net.pitsim.spigot.brewing.objects;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.brewing.BrewingManager;
import net.pitsim.spigot.brewing.PotionManager;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.darkzone.progression.ProgressionManager;
import net.pitsim.spigot.darkzone.progression.SkillBranch;
import net.pitsim.spigot.darkzone.progression.skillbranches.BrewingBranch;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BrewingSession {

	public Player player;
	public int brewingSlot;
	public String saveString;
	public BrewingIngredient identifier;
	public BrewingIngredient potency;
	public BrewingIngredient duration;
	public BrewingIngredient reduction;
	public long startTime;

	public BrewingSession(Player player, int brewingSlot, String saveString, BrewingIngredient identifier, BrewingIngredient potency, BrewingIngredient duration, BrewingIngredient reduction) {
		this.player = player;
		this.brewingSlot = brewingSlot;
		this.saveString = saveString;

		if(saveString != null) loadFromSave();
		else {
			this.identifier = identifier;
			this.potency = potency;
			this.duration = duration;
			this.reduction = reduction;
			startTime = System.currentTimeMillis();
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
			pitPlayer.brewingSessions.set(brewingSlot - 1, getSaveString());
		}
	}

	public void loadFromSave() {
		String[] saveValues = saveString.split(",");
		brewingSlot = Integer.parseInt(saveValues[0]);
		identifier = BrewingIngredient.getIngredientFromTier(Integer.parseInt(saveValues[1]));
		potency = BrewingIngredient.getIngredientFromTier(Integer.parseInt(saveValues[2]));
		duration = BrewingIngredient.getIngredientFromTier(Integer.parseInt(saveValues[3]));
		reduction = BrewingIngredient.getIngredientFromTier(Integer.parseInt(saveValues[4]));
		startTime = Long.parseLong(saveValues[5]);
	}

	public void givePotion() {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(ProgressionManager.isUnlocked(pitPlayer, BrewingBranch.INSTANCE, SkillBranch.MajorUnlockPosition.SECOND_PATH)) {
			double rand = Math.random();
			if(identifier.tier == 10) return;
			if(rand <= 0.25) {
				identifier = BrewingIngredient.getIngredientFromTier(Integer.parseInt(saveString.split(",")[1]) + 1);
				Sounds.SUCCESS.play(player);
				AOutput.send(player, "&5&lLUCKY BREW! &7Potion tier increased by &f1&7!");
			}
		}

		ItemStack potion = PotionManager.createPotion(identifier, potency, duration);
		AUtil.giveItemSafely(player, potion);
		BrewingManager.brewingSessions.remove(this);
		pitPlayer.brewingSessions.set(brewingSlot - 1, null);
	}

	public String getSaveString() {
		return brewingSlot + "," + identifier.tier + "," + potency.tier + "," +
				duration.tier + "," + reduction.tier + "," + startTime;
	}

}
