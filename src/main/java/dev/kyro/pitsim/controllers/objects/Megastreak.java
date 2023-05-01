package dev.kyro.pitsim.controllers.objects;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.ahelp.Summarizable;
import dev.kyro.pitsim.controllers.PerkManager;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.enums.DisplayItemType;
import dev.kyro.pitsim.megastreaks.NoMegastreak;
import dev.kyro.pitsim.megastreaks.RNGesus;
import dev.kyro.pitsim.megastreaks.Uberstreak;
import dev.kyro.pitsim.misc.Formatter;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.upgrades.TheWay;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class Megastreak implements Listener, Summarizable {
	public String displayName;
	public String refName;
	public int requiredKills;
	public int prestigeReq;
	public int baseLevelReq;

	public Megastreak(String displayName, String refName, int requiredKills, int prestigeReq, int baseLevelReq) {
		this.displayName = displayName;
		this.refName = refName;
		this.requiredKills = requiredKills;
		this.prestigeReq = prestigeReq;
		this.baseLevelReq = baseLevelReq;
	}

	public abstract String getPrefix(Player player);
	public abstract ItemStack getBaseDisplayStack(Player player);
	public abstract void addBaseDescription(PitLoreBuilder loreBuilder, Player player);

	public void proc(Player player) {}
	public void reset(Player player) {}

	public ItemStack getDisplayStack(Player player, DisplayItemType displayType) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);
		boolean isUnlocked = PerkManager.isUnlocked(player, this);
		ChatColor chatColor = PerkManager.getChatColor(player, this);
		int levelRequired = getLevelReq(player);
		PitLoreBuilder loreBuilder = new PitLoreBuilder();

		if(displayType == DisplayItemType.MAIN_PERK_PANEL && this != NoMegastreak.INSTANCE)
			loreBuilder.addLore("&7Selected: &a" + displayName, "");

		addBaseDescription(loreBuilder, player);

		ALoreBuilder prestigeRequiredLore = new ALoreBuilder().addLore(
				"",
				"&7Required Prestige: &e" + (prestigeReq == 0 ? 0 : AUtil.toRoman(prestigeReq))
		);
		ALoreBuilder levelRequiredLore = new ALoreBuilder().addLore(
				"",
				"&7Required Level: " + prestigeInfo.getOpenBracket() + PrestigeValues.getLevelColor(levelRequired) + levelRequired + prestigeInfo.getCloseBracket()
		);
		if(pitPlayer.prestige < prestigeReq) loreBuilder.addLore(prestigeRequiredLore.getLore());
		else if(pitPlayer.level < levelRequired) loreBuilder.addLore(levelRequiredLore.getLore());

		String status = "&eClick to select!";
		if(displayType == DisplayItemType.MAIN_PERK_PANEL) {
			status = "&eClick to switch megastreak!";
		} else if(this == NoMegastreak.INSTANCE) {
			status = "&eClick to remove megastreak!";
		} else if(!isUnlocked) {
			status = "&cToo low prestige!";
		} else if(this instanceof Uberstreak && pitPlayer.dailyUbersLeft == 0) {
			status = "&cDaily limit reached!";
		} else if(this instanceof RNGesus && RNGesus.isOnCooldown(pitPlayer)) {
			String statusColor = pitPlayer.renown >= RNGesus.RENOWN_COST ? "&e" : "&c";
			status = statusColor + "Click to select for " + Formatter.formatRenown(RNGesus.RENOWN_COST) + "!";
		} else if(pitPlayer.level < levelRequired) {
			status = "&cToo low level!";
		} else if(hasMegastreak(player)) {
			status = "&aAlready selected!";
		}
		if(displayType.shouldAppendStatus()) loreBuilder.addLongLine(status);

		ItemStack baseStack = getBaseDisplayStack(player);
		if(!isUnlocked) baseStack.setType(Material.BEDROCK);
		if(hasMegastreak(player) && displayType == DisplayItemType.SELECT_PANEL && this != NoMegastreak.INSTANCE) Misc.addEnchantGlint(baseStack);
		return new AItemStackBuilder(baseStack)
				.setName(chatColor + displayName)
				.setLore(loreBuilder)
				.getItemStack();
	}

	public boolean hasMegastreak(LivingEntity checkPlayer) {
		if(!PlayerManager.isRealPlayer(checkPlayer)) return false;
		Player player = (Player) checkPlayer;
		return PerkManager.isEquipped(player, this);
	}

	public int getLevelReq(Player player) {
		return Math.max(baseLevelReq - TheWay.INSTANCE.getLevelReduction(player), 0);
	}

	@Override
	public String getIdentifier() {
		if(this instanceof NoMegastreak) return null;
		return "MEGASTREAK_" + getRefName().toUpperCase().replaceAll("[- ]", "_");
	}

	@Override
	public List<String> getTrainingPhrases() {
		List<String> trainingPhrases = new ArrayList<>();
		trainingPhrases.add("what is " + getRefName() + "?");
		trainingPhrases.add("what does " + getRefName() + " do?");
		return trainingPhrases;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getRefName() {
		return refName;
	}

	public int getPrestigeReq() {
		return prestigeReq;
	}
}
