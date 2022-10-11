package dev.kyro.pitsim.battlepass.quests.daily;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DailyPlayerKillQuest extends PassQuest {
	public static Map<UUID, Map<UUID, Long>> cooldownMap = new HashMap<>();

	public DailyPlayerKillQuest() {
		super("&c&lPlayer Kills", "dailyplayerkills", QuestType.DAILY);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!PlayerManager.isRealPlayer(killEvent.getKillerPlayer()) || !canProgressQuest(killEvent.getKillerPitPlayer())
				|| !PlayerManager.isRealPlayer(killEvent.getDeadPlayer())) return;

		Map<UUID, Long> playerCooldownMap = cooldownMap.getOrDefault(killEvent.getKillerPlayer().getUniqueId(), new HashMap<>());
		Long cooldown = playerCooldownMap.getOrDefault(killEvent.getDeadPlayer().getUniqueId(), 0L);
		if(cooldown + 60 * 1000 > System.currentTimeMillis()) return;
		playerCooldownMap.put(killEvent.getDeadPlayer().getUniqueId(), System.currentTimeMillis());

		progressQuest(killEvent.getKillerPitPlayer(), 1);
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.DIAMOND_SWORD)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Kill &c" + Misc.formatLarge(questLevel.getRequirement(pitPlayer)) + " &7players",
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
		return new QuestLevel(30.0, 100);
	}

	@Override
	public List<QuestLevel> getWeeklyPossibleStates() {
		return null;
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
