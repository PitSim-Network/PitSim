package net.pitsim.pitsim.controllers.objects;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.pitsim.ahelp.Summarizable;
import net.pitsim.pitsim.controllers.PerkManager;
import net.pitsim.pitsim.controllers.PlayerManager;
import net.pitsim.pitsim.enums.DisplayItemType;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import net.pitsim.pitsim.perks.NoPerk;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class PitPerk implements Listener, Summarizable {
	public String displayName;
	public String refName;
	public Class<? extends RenownUpgrade> renownUpgradeClass;

	public PitPerk(String displayName, String refName) {
		this(displayName, refName, null);
	}

	public PitPerk(String displayName, String refName, Class<RenownUpgrade> renownUpgradeClass) {
		this.displayName = displayName;
		this.refName = refName;
		this.renownUpgradeClass = renownUpgradeClass;
	}

	public abstract ItemStack getBaseDisplayStack();
	public abstract void addBaseDescription(PitLoreBuilder loreBuilder, Player player);

	public ItemStack getDisplayStack(Player player, DisplayItemType displayType) {
		return getDisplayStack(player, displayType, -1);
	}

	public ItemStack getDisplayStack(Player player, DisplayItemType displayType, int perkSlot) {
		boolean isUnlocked = PerkManager.isUnlocked(player, this);
		ChatColor chatColor = PerkManager.getChatColor(player, this);
		PitLoreBuilder loreBuilder = new PitLoreBuilder();

		if(displayType == DisplayItemType.MAIN_PERK_PANEL && this != NoPerk.INSTANCE)
			loreBuilder.addLongLine("&7Selected: &a" + displayName);

		addBaseDescription(loreBuilder, player);

		String status = "&eClick to select!";
		if(displayType == DisplayItemType.MAIN_PERK_PANEL) {
			status = "&eClick to switch this perk!";
		} else if(this == NoPerk.INSTANCE) {
			status = "&eClick to remove perk!";
		} else if(!isUnlocked) {
			status = "&cUnlocked in the renown shop!";
		} else if(hasPerk(player)) {
			status = "&aAlready selected!";
		}
		if(displayType.shouldAppendStatus()) loreBuilder.addLongLine(status);

		ItemStack baseStack = getBaseDisplayStack();
		if(!isUnlocked) baseStack.setType(Material.BEDROCK);
		if(hasPerk(player) && displayType == DisplayItemType.SELECT_PANEL && this != NoPerk.INSTANCE) Misc.addEnchantGlint(baseStack);
		return new AItemStackBuilder(baseStack)
				.setName(perkSlot == -1 ? chatColor + displayName : "&aPerk Slot #" + (perkSlot + 1))
				.setLore(loreBuilder)
				.getItemStack();
	}

	public boolean hasPerk(LivingEntity checkPlayer) {
		if(!PlayerManager.isRealPlayer(checkPlayer)) return false;
		Player player = (Player) checkPlayer;
		return PerkManager.isEquipped(player, this);
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
