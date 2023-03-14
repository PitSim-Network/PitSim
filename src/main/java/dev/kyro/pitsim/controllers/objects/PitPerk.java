package dev.kyro.pitsim.controllers.objects;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.ahelp.Summarizable;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class PitPerk implements Listener, Summarizable {
	public static List<PitPerk> pitPerks = new ArrayList<>();

	public String displayName;
	public String refName;
	public ItemStack displayItem;
	public int guiSlot;
	public boolean renownUnlockable;
	public String upgradeRef;
	public PitPerk INSTANCE;
	public boolean healing;

	public PitPerk(String displayName, String refName, ItemStack displayItem, int guiSlot, boolean renownUnlockable, String upgradeRef, PitPerk instance, boolean healing) {
//        INSTANCE = this;
		this.displayName = displayName;
		this.refName = refName;
		this.displayItem = displayItem;
		this.guiSlot = guiSlot;
		this.renownUnlockable = renownUnlockable;
		this.upgradeRef = upgradeRef;
		this.INSTANCE = instance;
		this.healing = healing;

		pitPerks.add(this);
	}

	public abstract List<String> getDescription();

	public boolean playerHasUpgrade(LivingEntity checkPlayer) {
		if(!(checkPlayer instanceof Player)) return false;
		Player player = (Player) checkPlayer;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		for(PitPerk pitPerk : pitPlayer.pitPerks) {

			if(pitPerk == this) return true;
		}
		return false;
	}

	public ItemStack getDisplayItem() {
		return new AItemStackBuilder(displayItem.clone())
				.setLore(getDescription())
				.setName(displayName)
				.getItemStack();
	}

	public static PitPerk getPitPerk(String refName) {

		for(PitPerk pitPerk : pitPerks) {
			if(pitPerk.refName.equalsIgnoreCase(refName)) return pitPerk;
		}
		return null;
	}

	@Override
	public String getIdentifier() {
		return "PERK_" + refName.toUpperCase().replaceAll("[- ]", "_");
	}

	@Override
	public List<String> getTrainingPhrases() {
		List<String> trainingPhrases = new ArrayList<>();
		trainingPhrases.add("what is " + ChatColor.stripColor(displayName) + "?");
		trainingPhrases.add("what does " + ChatColor.stripColor(displayName) + " do?");
		return trainingPhrases;
	}
}
