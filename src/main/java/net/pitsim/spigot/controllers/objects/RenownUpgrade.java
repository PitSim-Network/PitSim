package net.pitsim.spigot.controllers.objects;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.ahelp.Summarizable;
import net.pitsim.spigot.controllers.UpgradeManager;
import net.pitsim.spigot.misc.Formatter;
import net.pitsim.spigot.misc.PitLoreBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class RenownUpgrade implements Listener, Summarizable {
	public static DecimalFormat decimalFormat = new DecimalFormat("#,##0.#");
	public String name;
	public String refName;
	public int prestigeReq;
	public Class<? extends AGUIPanel> subPanel;

	public RenownUpgrade(String name, String refName, int prestigeReq) {
		this(name, refName, prestigeReq, null);
	}

	public RenownUpgrade(String name, String refName, int prestigeReq, Class<? extends AGUIPanel> subPanel) {
		this.name = name;
		this.refName = refName;
		this.prestigeReq = prestigeReq;
		this.subPanel = subPanel;
	}

	public abstract ItemStack getBaseDisplayStack();
	public abstract void addBaseDescription(PitLoreBuilder loreBuilder, Player player);

	public ItemStack getDisplayStack(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		PitLoreBuilder loreBuilder = new PitLoreBuilder();
		ChatColor chatColor = UpgradeManager.getChatColor(player, this);

		boolean hasUpgrade = UpgradeManager.hasUpgrade(player, this);
		boolean isMaxed = UpgradeManager.isMaxed(player, this);
		int renownCost = isMaxed ? -1 : UpgradeManager.getNextCost(player, this);

		addBaseDescription(loreBuilder, player);

		ALoreBuilder prestigeRequired = new ALoreBuilder().addLore(
				"",
				"&7Required Prestige: &e" + AUtil.toRoman(prestigeReq)
		);
		if(pitPlayer.prestige < prestigeReq) loreBuilder.addLore(prestigeRequired.getLore());

		ALoreBuilder cost = new ALoreBuilder(
				"",
				"&7Cost: &e" + Formatter.formatRenown(renownCost),
				"&7You Have: &e" + Formatter.formatRenown(pitPlayer.renown)
		);
		if(pitPlayer.prestige >= prestigeReq && !isMaxed) loreBuilder.addLore(cost.getLore());

		ALoreBuilder status = new ALoreBuilder("");
		if(pitPlayer.prestige < prestigeReq) {
			status.addLore("&cToo low prestige!");
		} else if(subPanel != null && hasUpgrade) {
			status.addLore("&eClick to open menu!");
		} else if(isMaxed && isTiered()) {
			status.addLore("&aMax tier unlocked!");
		} else if(isMaxed && !isTiered()) {
			status.addLore("&aUnlocked!");
		} else if(pitPlayer.renown < renownCost) {
			status.addLore("&cNot enough renown!");
		} else {
			status.addLore("&eClick to purchase!");
		}
		loreBuilder.addLore(status.getLore());

		ItemStack baseStack = getBaseDisplayStack();
		if(pitPlayer.prestige < prestigeReq) baseStack.setType(Material.BEDROCK);
		return new AItemStackBuilder(baseStack)
				.setName(chatColor + name)
				.setLore(loreBuilder)
				.getItemStack();
	}

	public boolean isTiered() {
		return this instanceof TieredRenownUpgrade;
	}

	public TieredRenownUpgrade getAsTiered() {
		if(!(this instanceof TieredRenownUpgrade)) throw new RuntimeException();
		return (TieredRenownUpgrade) this;
	}

	public UnlockableRenownUpgrade getAsUnlockable() {
		if(!(this instanceof UnlockableRenownUpgrade)) throw new RuntimeException();
		return (UnlockableRenownUpgrade) this;
	}

	public int getMaxTiers() {
		TieredRenownUpgrade renownUpgrade = getAsTiered();
		return renownUpgrade.getMaxTiers();
	}

	public List<Integer> getTierCosts() {
		TieredRenownUpgrade renownUpgrade = getAsTiered();
		return renownUpgrade.getTierCosts();
	}

	public int getUnlockCost() {
		UnlockableRenownUpgrade renownUpgrade = getAsUnlockable();
		return renownUpgrade.getUnlockCost();
	}

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
