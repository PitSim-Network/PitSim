package net.pitsim.pitsim.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.battlepass.PassQuest;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.misc.Formatter;
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

	public static long prestigedTick;
	public static List<UUID> alreadyCompleted = new ArrayList<>();

	public CongratulatePrestigeQuest() {
		super("&e&lGood Game", "congratulateprestige", QuestType.WEEKLY);
		INSTANCE = this;
	}

	public static void updateRecentlyPrestiged() {
		prestigedTick = PitSim.currentTick;
		alreadyCompleted.clear();
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if(prestigedTick + 20 * 10 > PitSim.currentTick) return;
		Player player = event.getPlayer();

		String[] words = ChatColor.stripColor(event.getMessage()).toLowerCase().replaceAll("[^A-Za-z0-9]", "").split(" ");
		if(words.length == 0 || words[0].length() < 2) return;
		for(char character : words[0].toCharArray()) {
			if(character != 'g') return;
		}

		if(alreadyCompleted.contains(player.getUniqueId())) return;
		alreadyCompleted.add(player.getUniqueId());

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		progressQuest(pitPlayer, 1);
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.FIREWORK)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Say &egg!&7 to &e" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer)) + " &7players",
						"&7after they prestige",
						"",
						"&7Progress: &3" + Formatter.formatLarge(progress) + "&7/&3" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer)) + " &8[" +
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
	public void createPossibleStates() {
		questLevels.add(new QuestLevel(8, 100));
		questLevels.add(new QuestLevel(12, 150));
		questLevels.add(new QuestLevel(16, 200));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
