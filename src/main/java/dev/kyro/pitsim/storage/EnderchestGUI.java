package dev.kyro.pitsim.storage;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

import java.util.*;

public class EnderchestGUI extends AGUI {

	public EnderchestPanel panel;
	public UUID storagePlayer;

	public EnderchestGUI(Player openPlayer, UUID storagePlayer) {
		super(openPlayer);

		this.storagePlayer = storagePlayer;
		this.panel = new EnderchestPanel(this, storagePlayer);
		setHomePanel(panel);
	}

	public enum EnderchestPages {
		MEMBER("&7Member", 1, "group.default"),
		LEGENDARY("&eLegendary", 3, "group.legendary"),
		OVERPOWERED("&5Overpowered", 5, "group.overpowered"),
		EXTRAORDINARY("&3Extraordinary", 7, "group.extraordinary"),
		MIRACULOUS("&bMiraculous", 10, "group.miraculous"),
		UNTHINKABLE("&6Unthinkable", 14, "group.unthinkable"),
		ETERNAL("&4Eternal", 18, "group.eternal"),
		DEVELOPER("&9Developer", 18, "group.developer");

		public final String rankName;
		public final int pages;
		public final String permission;

		EnderchestPages(String rankName, int pages, String permission) {
			this.rankName = rankName;
			this.pages = pages;
			this.permission = permission;
		}

		public static EnderchestPages getRank(Player player) {
			List<EnderchestPages> ranks = new ArrayList<>(Arrays.asList(values()));
			Collections.reverse(ranks);
			for(EnderchestPages value : ranks) if(player.hasPermission(value.permission)) return value;
			return MEMBER;
		}

		public static EnderchestPages getMinimumRequiredRank(int pages) {
			for(EnderchestPages value : values()) if(value.pages >= pages) return value;
			return MEMBER;
		}
	}
}
