package dev.kyro.pitsim.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.battlepass.PassData;
import dev.kyro.pitsim.battlepass.PassManager;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PunchUniquePlayers extends PassQuest {
	public static PunchUniquePlayers INSTANCE;

	public PunchUniquePlayers() {
		super("&f&lAwkward Greeting", "punchplayers", QuestType.WEEKLY);
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!PlayerManager.isRealPlayer(attackEvent.getAttackerPlayer()) || !PlayerManager.isRealPlayer(attackEvent.getDefenderPlayer()) ||
				attackEvent.getAttacker() == attackEvent.getDefender() || attackEvent.getArrow() != null || attackEvent.getPet() != null) return;

		PassData passData = attackEvent.getAttackerPitPlayer().getPassData(PassManager.currentPass.startDate);
		if(passData.uniquePlayersPunched.contains(attackEvent.getDefenderPlayer().getUniqueId().toString())) return;
		passData.uniquePlayersPunched.add(attackEvent.getDefenderPlayer().getUniqueId().toString());
		progressQuest(attackEvent.getAttackerPitPlayer(), 1);
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.BRICK)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Say hi to &f" + Misc.formatLarge(questLevel.getRequirement(pitPlayer)) + " &7unique players",
						"&7by punching them :P",
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
		questLevels.add(new QuestLevel(30.0, 100));
		return questLevels;
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
