package net.pitsim.pitsim.cosmetics;

import net.pitsim.pitsim.settings.SubCosmeticPanel;
import net.pitsim.pitsim.settings.cosmeticsub.*;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum CosmeticType {
	PLAYER_KILL_EFFECT("&4Player Kill Effects", PlayerKillEffectsPanel.class),
	BOT_KILL_EFFECT("&cBot Kill Effects", BotKillEffectsPanel.class),
	BOUNTY_CLAIM_MESSAGE("&6Bounty Messages", BountyMessagesPanel.class),
	CAPE("&fCapes", CapesPanel.class),
	PARTICLE_TRAIL("&eParticle Trails", ParticleTrailsPanel.class),
	AURA("&aAuras", AurasPanel.class),
	MISC("&eMisc", MiscPanel.class);

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
