package dev.kyro.pitsim.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enchants.overworld.MegaLongBow;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Formatter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class LandMLBShotsQuest extends PassQuest {
	public static LandMLBShotsQuest INSTANCE;

	public LandMLBShotsQuest() {
		super("&a&lBow Spammer", "mlbshots", QuestType.WEEKLY);
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(attackEvent.getArrow() == null || !MegaLongBow.mlbShots.contains(attackEvent.getArrow().getUniqueId()) ||
				!PlayerManager.isRealPlayer(attackEvent.getAttackerPlayer()) || !PlayerManager.isRealPlayer(attackEvent.getDefenderPlayer()) ||
				attackEvent.getAttacker() == attackEvent.getDefender()) return;
		progressQuest(attackEvent.getAttackerPitPlayer(), 1);
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.ARROW)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Land &a" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer)) + " &7shots using a",
						"&7bow with &aMLB &7on players",
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
		questLevels.add(new QuestLevel(2_000, 100));
		questLevels.add(new QuestLevel(3_000, 150));
		questLevels.add(new QuestLevel(4_000, 200));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
