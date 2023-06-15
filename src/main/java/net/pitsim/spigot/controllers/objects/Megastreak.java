package net.pitsim.spigot.controllers.objects;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.ahelp.Summarizable;
import net.pitsim.spigot.controllers.PerkManager;
import net.pitsim.spigot.controllers.PlayerManager;
import net.pitsim.spigot.controllers.PrestigeValues;
import net.pitsim.spigot.enums.DisplayItemType;
import net.pitsim.spigot.megastreaks.NoMegastreak;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.PitLoreBuilder;
import net.pitsim.spigot.upgrades.TheWay;
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
	public boolean hasDailyLimit;

	public Megastreak(String displayName, String refName, int requiredKills, int prestigeReq, int baseLevelReq) {
		this.displayName = displayName;
		this.refName = refName;
		this.requiredKills = requiredKills;
		this.prestigeReq = prestigeReq;
		this.baseLevelReq = baseLevelReq;
	}

	public abstract String getPrefix(Player player);
	public abstract ItemStack getBaseDisplayStack(Player player);
	public abstract void addBaseDescription(PitLoreBuilder loreBuilder, PitPlayer pitPlayer);

	public void proc(Player player) {}
	public void reset(Player player) {}

	public int getMaxDailyStreaks(PitPlayer pitPlayer) {
		return 0;
	}

	public ItemStack getDisplayStack(Player player, DisplayItemType displayType) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);
		boolean isUnlocked = PerkManager.isUnlocked(player, this);
		ChatColor chatColor = PerkManager.getChatColor(player, this);
		int levelRequired = getLevelReq(player);
		PitLoreBuilder loreBuilder = new PitLoreBuilder();

		if(displayType == DisplayItemType.MAIN_PERK_PANEL && this != NoMegastreak.INSTANCE)
			loreBuilder.addLore("&7Selected: &a" + displayName, "");

		ALoreBuilder triggerLore = new ALoreBuilder().addLore(
				"&7Trigger: &c" + requiredKills + " kills"
		);
		if(!(this instanceof NoMegastreak)) loreBuilder.addLore(triggerLore.getLore());
		loreBuilder.addLore("");

		addBaseDescription(loreBuilder, pitPlayer);

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

		if(hasDailyLimit) {
			PitPlayer.MegastreakLimit cooldown = pitPlayer.getMegastreakCooldown(this);
			String streaksLeft = (cooldown.isAtLimit(pitPlayer) ? "&c" : "&a") + cooldown.getStreaksLeft(pitPlayer);
			loreBuilder.addLore(
					"",
					"&7Daily Streaks Remaining: " + streaksLeft + "&7/" + getMaxDailyStreaks(pitPlayer) +
							(cooldown.shouldDisplayResetTime() ? " &8(" + cooldown.getTimeLeft() + ")" : "")
			);
		}

		String status = "&eClick to select!";
		if(displayType == DisplayItemType.MAIN_PERK_PANEL) {
			status = "&eClick to switch megastreak!";
		} else if(this == NoMegastreak.INSTANCE) {
			status = "&eClick to remove megastreak!";
		} else if(!isUnlocked) {
			status = "&cToo low prestige!";
		} else if(hasDailyLimit && pitPlayer.getMegastreakCooldown(this).isAtLimit(pitPlayer)) {
			status = "&cDaily limit reached!";
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

	public int getKillIncrements(PitPlayer pitPlayer, int everyX) {
		return getKillIncrements(pitPlayer, everyX, 0);
	}

	public int getKillIncrements(PitPlayer pitPlayer, int everyX, int starting) {
		return Math.max((pitPlayer.getKills() - starting) / everyX, 0);
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getCapsDisplayName() {
		String translatedName = ChatColor.translateAlternateColorCodes('&', getDisplayName());
		return ChatColor.getLastColors(translatedName) + "&l" +
				ChatColor.stripColor(translatedName).toUpperCase();
	}

	public String getRefName() {
		return refName;
	}

	public int getPrestigeReq() {
		return prestigeReq;
	}
}
