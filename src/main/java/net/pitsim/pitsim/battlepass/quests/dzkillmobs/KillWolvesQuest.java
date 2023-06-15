package net.pitsim.pitsim.battlepass.quests.dzkillmobs;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.pitsim.adarkzone.DarkzoneManager;
import net.pitsim.pitsim.adarkzone.mobs.PitWolf;
import net.pitsim.pitsim.battlepass.PassManager;
import net.pitsim.pitsim.battlepass.PassQuest;
import net.pitsim.pitsim.controllers.PlayerManager;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.events.KillEvent;
import net.pitsim.pitsim.misc.Formatter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class KillWolvesQuest extends PassQuest {

	public KillWolvesQuest() {
		super("&c&lWolf Slayer", "killwolves", QuestType.WEEKLY);
		weight = PassManager.DARKZONE_KILL_QUEST_WEIGHT;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!PlayerManager.isRealPlayer(killEvent.getKillerPlayer())) return;
		if(!(DarkzoneManager.getPitMob(killEvent.getDead()) instanceof PitWolf)) return;

		progressQuest(killEvent.getKillerPitPlayer(), 1);
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.LEASH)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Kill &c" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer)) + " &7wolves",
						"",
						"&7Progress: &3" + Formatter.formatLarge(progress) + "&7/&3" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer)) + " &8[" +
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
		questLevels.add(new QuestLevel(700, 100));
		questLevels.add(new QuestLevel(1050, 150));
		questLevels.add(new QuestLevel(1400, 200));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
