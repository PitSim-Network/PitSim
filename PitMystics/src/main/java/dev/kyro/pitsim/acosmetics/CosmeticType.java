package dev.kyro.pitsim.acosmetics;

import dev.kyro.pitsim.asettings.SubCosmeticPanel;
import dev.kyro.pitsim.asettings.cosmeticsub.CapesPanel;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum CosmeticType {
	KILL_EFFECT("", null),
	DEATH_EFFECT("", null),
	BOUNTY_CLAIM_MESSAGE("", null),
	CAPE("&fCapes", CapesPanel.class),
	PARTICLE_TRAIL("", null),
	AURA("", null),
	MISC("", null);

	private static List<Integer> settingsGUISlots = new ArrayList<>(Arrays.asList(10, 11, 12, 13, 14, 15, 16));

	private String panelName;
	public Class<? extends SubCosmeticPanel> panelClazz;

	CosmeticType(String panelName, Class<? extends SubCosmeticPanel> panelClazz) {
		this.panelName = panelName;
		this.panelClazz = panelClazz;
	}

	public String getPanelName() {
		return ChatColor.translateAlternateColorCodes('&', panelName);
	}

	public int getSettingsGUISlot() {
		for(int i = 0; i < values().length; i++) {
			if(this != values()[i]) continue;
			return settingsGUISlots.get(i);
		}
		return -1;
	}
}
