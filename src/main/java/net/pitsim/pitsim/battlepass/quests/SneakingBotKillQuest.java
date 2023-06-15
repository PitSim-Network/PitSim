package net.pitsim.pitsim.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.pitsim.battlepass.PassQuest;
import net.pitsim.pitsim.controllers.NonManager;
import net.pitsim.pitsim.controllers.PlayerManager;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.events.KillEvent;
import net.pitsim.pitsim.megastreaks.NoMegastreak;
import net.pitsim.pitsim.misc.Formatter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class SneakingBotKillQuest extends PassQuest {

	public SneakingBotKillQuest() {
		super("&c&lSneaky Assassin", "sneakingbotkills", QuestType.WEEKLY);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!PlayerManager.isRealPlayer(killEvent.getKillerPlayer()) || !canProgressQuest(killEvent.getKillerPitPlayer()) ||
				NonManager.getNon(killEvent.getDead()) == null || !killEvent.getKillerPlayer().isSneaking() ||
				!(killEvent.getKillerPitPlayer().getMegastreak() instanceof NoMegastreak)) return;

		progressQuest(killEvent.getKillerPitPlayer(), 1);
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.WOOD_SWORD)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Kill &c" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer)) + " &7bots while sneaking",
						"&7and without having a",
						"&7megastreak equipped",
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
		questLevels.add(new QuestLevel(5_000.0, 100));
		questLevels.add(new QuestLevel(10_000.0, 150));
		questLevels.add(new QuestLevel(15_000.0, 200));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
