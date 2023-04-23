package dev.kyro.pitsim.enums;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum RankInformation {
	MEMBER("&7Member", "group.default", 1, 2),
	LEGENDARY("&eLegendary", "group.legendary", 3, 3),
	OVERPOWERED("&5Overpowered", "group.overpowered", 5, 4),
	EXTRAORDINARY("&3Extraordinary", "group.extraordinary", 7, 5),
	MIRACULOUS("&bMiraculous", "group.miraculous", 10, 6),
	UNTHINKABLE("&6Unthinkable", "group.unthinkable", 14, 7),
	ETERNAL("&4Eternal", "pitsim.has-had-eternal", 18, 9),
	DEVELOPER("&9Developer", "group.developer", 18, 7);

	public final String rankName;
	public final String permission;
	public final int enderchestPages;
	public final int outfits;

	RankInformation(String rankName, String permission, int enderchestPages, int outfits) {
		this.rankName = rankName;
		this.enderchestPages = enderchestPages;
		this.permission = permission;
		this.outfits = outfits;
	}

	public static RankInformation getRank(Player player) {
		List<RankInformation> ranks = new ArrayList<>(Arrays.asList(values()));
		Collections.reverse(ranks);
		for(RankInformation value : ranks) if(player.hasPermission(value.permission)) return value;
		return MEMBER;
	}

	public static RankInformation getMinimumRankForPages(int pages) {
		for(RankInformation value : values()) if(value.enderchestPages >= pages) return value;
		return MEMBER;
	}

	public static RankInformation getMinimumRankForOutfits(int outfits) {
		for(RankInformation value : values()) if(value.outfits >= outfits) return value;
		return MEMBER;
	}
}