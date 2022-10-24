package dev.kyro.pitsim.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DoTrueDamageVSPlayersQuest extends PassQuest {

	public DoTrueDamageVSPlayersQuest() {
		super("&9&lWhen Players Fly", "truedamageplayers", QuestType.WEEKLY);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!PlayerManager.isRealPlayer(attackEvent.getAttackerPlayer()) || !canProgressQuest(attackEvent.getAttackerPitPlayer())
				|| !PlayerManager.isRealPlayer(attackEvent.getDefenderPlayer()) || attackEvent.getDefenderPlayer().isOnGround()) return;
		progressQuest(attackEvent.getAttackerPitPlayer(), attackEvent.trueDamage);
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.RED_ROSE, 1, 1)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Deal &9" + Misc.getHearts(questLevel.getRequirement(pitPlayer)) + " &7of true damage to",
						"&7other players that are not",
						"&7on the ground",
						"",
						"&7Progress: &3" + Misc.formatLarge(progress / 2) + "&7/&3" + Misc.formatLarge(questLevel.getRequirement(pitPlayer) / 2) + " &8[" +
								AUtil.createProgressBar("|", ChatColor.AQUA, ChatColor.GRAY, 20, progress / questLevel.getRequirement(pitPlayer)) + "&8]",
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
		questLevels.add(new QuestLevel(2_500.0, 100));
		questLevels.add(new QuestLevel(3_750.0, 150));
		questLevels.add(new QuestLevel(5_000.0, 200));
		return questLevels;
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
