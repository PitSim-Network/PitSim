package dev.kyro.pitsim.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CongratulatePrestigeQuest extends PassQuest {
	public static CongratulatePrestigeQuest INSTANCE;

	public static Player recentlyPrestiged;
	public static long prestigedTick;
	public static List<UUID> alreadyCompleted = new ArrayList<>();

	public CongratulatePrestigeQuest() {
		super("&e&lGood Game", "congratulateprestige", QuestType.WEEKLY);
		INSTANCE = this;
	}

	public static void updateRecentlyPrestiged(Player player) {
		recentlyPrestiged = player;
		prestigedTick = PitSim.currentTick;
		alreadyCompleted.clear();
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if(prestigedTick + 20 * 10 > PitSim.currentTick) return;
		Player player = event.getPlayer();

		String[] words = ChatColor.stripColor(event.getMessage()).toLowerCase().replaceAll("[^A-Za-z0-9]", "").split(" ");
		if(words.length == 0 || words[0].length() < 2) return;
		for(char character : words[0].toCharArray()){
			if(character != 'g') return;
		}

		if(alreadyCompleted.contains(player.getUniqueId())) return;
		alreadyCompleted.add(player.getUniqueId());

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		progressQuest(pitPlayer, 1);
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.FIREWORK)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Say &egg! &7dwto &e" + Misc.formatLarge(questLevel.getRequirement(pitPlayer)) + " &7players",
						"&7after they prestige",
						"",
						"&7Progress: &3" + Misc.formatLarge(progress) + "&7/&3" + Misc.formatLarge(questLevel.getRequirement(pitPlayer)) + " &8[" +
								AUtil.createProgressBar("|", ChatColor.AQUA, ChatColor.GRAY, 20,
								progress / questLevel.getRequirement(pitPlayer)) + "&8]",
						"&7Reward: &3" + questLevel.rewardPoints + " &7Quest Points"
				))
				.getItemStack();
		return itemStack;
	}

	@Override
	public QuestLevel getDailyState() {
		return null;
	}

	@Override
	public List<QuestLevel> getWeeklyPossibleStates() {
		List<QuestLevel> questLevels = new ArrayList<>();
		questLevels.add(new QuestLevel(1_000, 100));
		return questLevels;
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
