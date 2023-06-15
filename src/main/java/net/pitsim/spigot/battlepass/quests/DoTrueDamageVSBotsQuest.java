package net.pitsim.spigot.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.battlepass.PassQuest;
import net.pitsim.spigot.controllers.NonManager;
import net.pitsim.spigot.controllers.PlayerManager;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.misc.Formatter;
import net.pitsim.spigot.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

public class DoTrueDamageVSBotsQuest extends PassQuest {

	public DoTrueDamageVSBotsQuest() {
		super("&9&lDeadly Streaker", "truedamagebots", QuestType.WEEKLY);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(attackEvent.trueDamage == 0) return;
		if(!PlayerManager.isRealPlayer(attackEvent.getAttackerPlayer()) || !canProgressQuest(attackEvent.getAttackerPitPlayer())
				|| NonManager.getNon(attackEvent.getDefender()) == null) return;
		progressQuest(attackEvent.getAttackerPitPlayer(), attackEvent.trueDamage);
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.DAYLIGHT_DETECTOR)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Deal &9" + Misc.getHearts(questLevel.getRequirement(pitPlayer)) + " &7of true damage to",
						"&7bots",
						"",
						"&7Progress: &3" + Formatter.formatLarge(progress / 2) + "&7/&3" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer) / 2) + " &8[" +
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
	public void createPossibleStates() {
		questLevels.add(new QuestLevel(30_000.0, 100));
		questLevels.add(new QuestLevel(45_000.0, 150));
		questLevels.add(new QuestLevel(60_000.0, 200));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
