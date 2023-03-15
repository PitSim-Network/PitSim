package dev.kyro.pitsim.controllers.objects;

import dev.kyro.pitsim.ahelp.Summarizable;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class RenownUpgrade implements Listener, Summarizable {
	public static List<RenownUpgrade> upgrades = new ArrayList<>();

	public String name;
	public String refName;
	public int renownCost;
	public int guiSlot;
	public int prestigeReq;
	public boolean isTiered;
	public int maxTiers;

	public RenownUpgrade INSTANCE;

	public RenownUpgrade(String name, String refName, int renownCost, int guiSlot, int prestigeReq, boolean isTiered, int maxTiers) {
		this.name = name;
		this.refName = refName;
		this.renownCost = renownCost;
		this.guiSlot = guiSlot;
		this.prestigeReq = prestigeReq;
		this.isTiered = isTiered;
		this.maxTiers = maxTiers;

		upgrades.add(this);
	}

	public abstract ItemStack getDisplayItem(Player player);

	public abstract List<Integer> getTierCosts();

	@Override
	public String getIdentifier() {
		return "UPGRADE_" + refName.toUpperCase().replaceAll("[- ]", "_");
	}

	@Override
	public List<String> getTrainingPhrases() {
		List<String> trainingPhrases = new ArrayList<>();
		trainingPhrases.add("what is " + ChatColor.stripColor(name) + "?");
		trainingPhrases.add("what does " + ChatColor.stripColor(name) + " do?");
		return trainingPhrases;
	}
}
