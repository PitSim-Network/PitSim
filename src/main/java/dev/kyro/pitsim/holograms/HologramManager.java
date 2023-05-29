package dev.kyro.pitsim.holograms;

import dev.kyro.arcticguilds.GuildLeaderboardData;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.LeaderboardManager;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.*;
import dev.kyro.pitsim.events.PitJoinEvent;
import dev.kyro.pitsim.events.PitQuitEvent;
import dev.kyro.pitsim.leaderboards.XPLeaderboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HologramManager implements Listener {

	public static List<Hologram> holograms = new ArrayList<>();

	public static void registerHologram(Hologram hologram) {
		holograms.add(hologram);

		if(hologram.viewMode != ViewMode.ALL) return;
		hologram.setPermittedViewers(new ArrayList<>(Bukkit.getOnlinePlayers()));
	}

	@EventHandler
	public void onJoin(PitJoinEvent event) {
		for(Hologram hologram : holograms) {
			if(hologram.viewMode != ViewMode.ALL) continue;

			hologram.addPermittedViewer(event.getPlayer());
		}
	}

	@EventHandler
	public void onLeave(PitQuitEvent event) {
		for(Hologram hologram : holograms) {
			if(hologram.viewMode != ViewMode.ALL) continue;

			hologram.removePermittedViewer(event.getPlayer());
		}
	}

	@EventHandler
	public void onShutdown(PluginDisableEvent event) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			for(Hologram hologram : holograms) {
				for(TextLine textLine : hologram.textLines) {
					textLine.removeLine(player);
				}
			}
		}
	}

	public HologramManager() {
		PitMap pitMap = MapManager.currentMap;
		if(PitSim.status.isOverworld()) createOverworldHolograms(pitMap);
		if(PitSim.status.isDarkzone()) createDarkzoneHolograms();
	}

	private void createOverworldHolograms(PitMap pitMap) {
		new Hologram(pitMap.getWelcomeHolo()) {
			@Override
			public List<String> getStrings(Player player) {
				List<String> strings = new ArrayList<>();
				strings.add("&7Welcome to &6&lPit&e&lSim&7, &e" + player.getName() + "&7!");
				strings.add("&8&m----------------------------------");
				strings.add("&eInteract with the &fSpawn NPCs&e to get started");
				strings.add("&6Join our discord: &f&ndiscord.gg/pitsim");
				strings.add("&6Run &f/vote &6for &dFree Rewards&6!");
				strings.add("&8&m----------------------------------");
				return strings;
			}
		};

		new Hologram(pitMap.getMysticWellHolo()) {
			@Override
			public List<String> getStrings(Player player) {
				List<String> strings = new ArrayList<>();
				strings.add("&d&lMystic Well");
				strings.add("&e&lRIGHT CLICK");
				return strings;
			}
		};

		new Hologram(pitMap.getKitsHolo()) {
			@Override
			public List<String> getStrings(Player player) {
				List<String> strings = new ArrayList<>();
				strings.add("&e&lKITS");
				return strings;
			}
		};

		new Hologram(pitMap.getEnderchest1Holo()) {
			@Override
			public List<String> getStrings(Player player) {
				List<String> strings = new ArrayList<>();
				strings.add("&5&lENDER CHEST");
				strings.add("&7Store items permanently");
				return strings;
			}
		};

		new Hologram(pitMap.getEnderchest2Holo()) {
			@Override
			public List<String> getStrings(Player player) {
				List<String> strings = new ArrayList<>();
				strings.add("&5&lENDER CHEST");
				strings.add("&7Store items permanently");
				return strings;
			}
		};

		new Hologram(pitMap.getUpgradesHolo()) {
			@Override
			public List<String> getStrings(Player player) {
				List<String> strings = new ArrayList<>();
				strings.add("&e&lUPGRADES");
				return strings;
			}
		};

		new Hologram(pitMap.getPassHolo()) {
			@Override
			public List<String> getStrings(Player player) {
				List<String> strings = new ArrayList<>();
				strings.add("&3&lBATTLE PASS");
				return strings;
			}
		};

		new Hologram(pitMap.getPrestigeHolo()) {
			@Override
			public List<String> getStrings(Player player) {
				List<String> strings = new ArrayList<>();
				strings.add("&e&lPRESTIGE AND RENOWN");
				return strings;
			}
		};

		new Hologram(pitMap.getLeaderboardHolo(), ViewMode.ALL, RefreshMode.AUTOMATIC_MEDIUM) {
			@Override
			public List<String> getStrings(Player player) {
				List<String> strings = new ArrayList<>();
				strings.add("&f&6&lPit&e&lSim &7Top Players");
				strings.add("&8&m---------------------");

				PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
				Leaderboard leaderboard = LeaderboardManager.getLeaderboard(pitPlayer.savedLeaderboardRef);
				if(leaderboard == null) throw new RuntimeException("Leaderboard is null for " + pitPlayer.savedLeaderboardRef);
				LeaderboardData data = LeaderboardData.getLeaderboardData(leaderboard);

				for(int i = 1; i <= 10; i++) {
					if(leaderboard.orderedLeaderboard.size() < 10) {
						strings.add("&cERROR");
						continue;
					}

					LeaderboardPosition position = leaderboard.orderedLeaderboard.get(i - 1);
					LeaderboardData.PlayerData playerData = data.getValue(position.uuid);

					String rankColor = Leaderboard.getRankColor(position.uuid);

					if(leaderboard instanceof XPLeaderboard) {
						strings.add(getLeaderboardColor(i) + String.valueOf(i) + ". " + rankColor + playerData.username + "&7 - "
								+ PrestigeValues.getLeaderboardPrefix(playerData.prestige, playerData.level));
					} else {
						strings.add(getLeaderboardColor(i) + String.valueOf(i) + ". " + rankColor + playerData.username + "&7 - " +
								leaderboard.getDisplayValue(position));
					}

				}
				strings.add("&8&m---------------------");
				return strings;
			}
		};

		new Hologram(pitMap.getGuildLeaderboardHolo()) {
			@Override
			public List<String> getStrings(Player player) {
				List<String> strings = new ArrayList<>();
				strings.add("&6&lPit&e&lSim &7Top Guilds");
				strings.add("&8&m----------------");

				DecimalFormat formatter = new DecimalFormat("#,###.#");

				for(int i = 0; i < 10; i++) {
					GuildLeaderboardData guild = GuildLeaderboardData.getGuildData(i);
					if(guild == null) {
						strings.add("&cNone!");
						continue;
					}

					strings.add(ChatColor.translateAlternateColorCodes('&', getLeaderboardColor(i + 1) + String.valueOf(i + 1)
							+ ". " + guild.getColor() + guild.name + " &7- &e" + formatter.format(guild.reputation)));
				}

				strings.add("&8&m----------------");
				return strings;
			}
		};

		new Hologram(pitMap.getKeeperHolo()) {
			@Override
			public List<String> getStrings(Player player) {
				List<String> strings = new ArrayList<>();
				strings.add("&7Change Lobbies");
				return strings;
			}
		};

		new Hologram(pitMap.getPitSimCrate()) {
			@Override
			public List<String> getStrings(Player player) {
				return Collections.singletonList("&6&lPit&e&lSim &7Crate");
			}
		};

		new Hologram(pitMap.getVoteCrate()) {
			@Override
			public List<String> getStrings(Player player) {
				return Collections.singletonList("&a&lVote &7Crate");
			}
		};
	}

	private void createDarkzoneHolograms() {
		new Hologram(MapManager.getAuctionExitHolo()) {
			@Override
			public List<String> getStrings(Player player) {
				List<String> strings = new ArrayList<>();
				strings.add("&c&lEXIT");
				return strings;
			}
		};

		new Hologram(MapManager.getSkillsHolo()) {
			@Override
			public List<String> getStrings(Player player) {
				List<String> strings = new ArrayList<>();
				strings.add("&5&lDARKZONE SKILLS");
				strings.add("&7Use &fSouls &7to progress your way");
				strings.add("&7through the &5Darkzone &7content");
				return strings;
			}
		};

		new Hologram(MapManager.getMarketHolo()) {
			@Override
			public List<String> getStrings(Player player) {
				List<String> strings = new ArrayList<>();
				strings.add("&3&lPLAYER MARKET");
				strings.add("&6Purchase &7and &eSell &7items");
				strings.add("&7with other players using");
				strings.add("&7a &fSoul-Based &7economy");
				return strings;
			}
		};

		new Hologram(MapManager.getShopHolo()) {
			@Override
			public List<String> getStrings(Player player) {
				List<String> strings = new ArrayList<>();
				strings.add("&b&lTAINTED SHOP");
				strings.add("&7Purchase and");
				strings.add("&7shred items");
				strings.add("&7for &fSouls");
				return strings;
			}
		};

		new Hologram(MapManager.getEnderChestHolo()) {
			@Override
			public List<String> getStrings(Player player) {
				List<String> strings = new ArrayList<>();
				strings.add("&5&lENDER CHEST");
				return strings;
			}
		};

		new Hologram(MapManager.getTaintedCrateHolo()) {
			@Override
			public List<String> getStrings(Player player) {
				List<String> strings = new ArrayList<>();
				strings.add("&5&lTAINTED CRATE");
				strings.add("&8&m----------------------------");
				strings.add("&7Earn &dDarkzone Items&7, &fSouls&7,");
				strings.add("&7and &5Fresh Tainted Items");
				strings.add("&7Purchase at: &6&nstore.pitsim.net");
				strings.add("&8&m----------------------------");
				return strings;
			}
		};
	}

	public static ChatColor getLeaderboardColor(int i) {
		switch(i) {
			case 3: return ChatColor.GOLD;
			case 2: return ChatColor.WHITE;
			case 1: return ChatColor.YELLOW;
		}
		return ChatColor.GRAY;
	}
}
