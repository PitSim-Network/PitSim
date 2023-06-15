package net.pitsim.spigot.battlepass.quests.daily;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.battlepass.PassQuest;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.controllers.objects.PluginMessage;
import net.pitsim.spigot.events.MessageEvent;
import net.pitsim.spigot.misc.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class DailySWGamePlayedQuest extends PassQuest {

	public DailySWGamePlayedQuest() {
		super("&f&lPlay Skywars", "dailyskywars", QuestType.DAILY);
	}

	@EventHandler
	public void onMessage(MessageEvent event) {
		PluginMessage message = event.getMessage();
		List<String> strings = message.getStrings();
		List<Integer> ints = message.getIntegers();
		if(strings.isEmpty()) return;
		if(strings.get(0).equals("SKYWARS PASS QUEST")) {
			UUID playerUUID = UUID.fromString(strings.get(1));

			Player player = Bukkit.getPlayer(playerUUID);
			if(!player.isOnline()) return;

			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
			progressQuest(pitPlayer, ints.get(0));
		}
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.EYE_OF_ENDER)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Get at least &f1 &7kill",
						"&7in &f" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer)) + " &7skywars games",
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
	public void createPossibleStates() {
		questLevels.add(new QuestLevel(2, 40));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
