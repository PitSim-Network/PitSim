package dev.kyro.pitsim.enums;

import dev.kyro.pitsim.PitSim;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ExecutionException;

public enum RankInformation {
	MEMBER("&7Member", "group.default", 1, 2),
	LEGENDARY("&eLegendary", "group.legendary", 3, 3),
	OVERPOWERED("&5Overpowered", "group.overpowered", 5, 4),
	EXTRAORDINARY("&3Extraordinary", "group.extraordinary", 7, 5),
	MIRACULOUS("&bMiraculous", "group.miraculous", 10, 6),
	UNTHINKABLE("&6Unthinkable", "group.unthinkable", 14, 7),
	ETERNAL("&4Eternal", "pitsim.has-had-eternal", 18, 9),
	DEVELOPER("&9Developer", "group.developer", 18, 9);

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

	public static RankInformation getRank(UUID player) {
		List<RankInformation> ranks = new ArrayList<>(Arrays.asList(values()));
		Collections.reverse(ranks);

		try {
			List<String> perms = new ArrayList<>(PitSim.LUCKPERMS.getUserManager().loadUser(player)
					.get().getCachedData().getPermissionData().getPermissionMap().keySet());

			for(RankInformation value : ranks) if(perms.contains(value.permission)) return value;
		} catch(InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}

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