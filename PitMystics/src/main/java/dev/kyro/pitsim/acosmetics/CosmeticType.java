package dev.kyro.pitsim.acosmetics;

import dev.kyro.pitsim.asettings.SubCosmeticPanel;
import dev.kyro.pitsim.asettings.cosmeticsub.*;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum CosmeticType {
	KILL_EFFECT("&c&lKill Effects", "", KillEffectsPanel.class),
	DEATH_EFFECT("&9&lDeath Effects", "", DeathEffectsPanel.class),
	BOUNTY_CLAIM_MESSAGE("&6&lBounty Messages", "", BountyMessagesPanel.class),
	CAPE("&f&lCapes", "capes", CapesPanel.class),
	PARTICLE_TRAIL("&e&lParticle Trails", "", ParticleTrailsPanel.class),
	AURA("&a&lAuras", "", AurasPanel.class),
	MISC("&e&lMisc", "", MiscPanel.class);

	private static List<Integer> settingsGUISlots = new ArrayList<>(Arrays.asList(10, 11, 12, 13, 14, 15, 16));

	private String panelName;
	public String refName;
	public Class<? extends SubCosmeticPanel> panelClazz;

	CosmeticType(String panelName, String refName, Class<? extends SubCosmeticPanel> panelClazz) {
		this.panelName = panelName;
		this.refName = refName;
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
