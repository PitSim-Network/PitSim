package dev.kyro.pitsim.settings.scoreboard;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.ScoreboardManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class ScoreboardOption {

	public abstract String getDisplayName();
	public abstract String getRefName();
	public abstract String getValue(PitPlayer pitPlayer);
	public abstract ItemStack getBaseDisplayItem();

	public int getCurrentPosition(PitPlayer pitPlayer) {
		int count = 0;
		for(String testRefName : pitPlayer.scoreboardData.getPriorityList()) {
			if(getRefName().equals(testRefName)) return count;
			count++;
		}
		throw new RuntimeException();
	}

	public ItemStack getDisplayStack(int position, boolean isEnabled) {
		ItemStack itemStack = getBaseDisplayItem();
		ItemMeta itemMeta = itemStack.getItemMeta();
		ALoreBuilder loreBuilder = new ALoreBuilder(itemMeta.getLore()).addLore(
				"", "&7Status: " + (isEnabled ? "&aEnabled": "&cDisabled"),""
		);
		if(position != 0) loreBuilder.addLore("&eLeft-Click to increase priority");
		if(position != ScoreboardManager.scoreboardOptions.size() - 1) loreBuilder.addLore("&eRight-Click to decrease priority");
		loreBuilder.addLore("&eMiddle-Click to " + (isEnabled ? "disable" : "enable"));

		if(isEnabled) Misc.addEnchantGlint(itemStack);

		return new AItemStackBuilder(itemStack)
				.setLore(loreBuilder)
				.getItemStack();
	}
}
