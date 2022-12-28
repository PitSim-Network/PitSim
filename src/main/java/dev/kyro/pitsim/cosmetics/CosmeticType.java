package dev.kyro.pitsim.cosmetics;

import dev.kyro.pitsim.settings.SubCosmeticPanel;
import dev.kyro.pitsim.settings.cosmeticsub.*;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum CosmeticType {
	PLAYER_KILL_EFFECT("&4&lPlayer Kill Effects", PlayerKillEffectsPanel.class),
	BOT_KILL_EFFECT("&c&lBot Kill Effects", BotKillEffectsPanel.class),
	BOUNTY_CLAIM_MESSAGE("&6&lBounty Messages", BountyMessagesPanel.class),
	CAPE("&f&lCapes", CapesPanel.class),
	PARTICLE_TRAIL("&e&lParticle Trails", ParticleTrailsPanel.class),
	AURA("&a&lAuras", AurasPanel.class),
	MISC("&e&lMisc", MiscPanel.class);

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
