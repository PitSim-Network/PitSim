package dev.kyro.pitsim.battlepass.quests.dzkillmobs;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.battlepass.PassManager;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.adarkzone.aaold.OldMobManager;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.CaveSpider;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class KillCaveSpidersQuest extends PassQuest {

	public KillCaveSpidersQuest() {
		super("&c&lCave Spider Slayer", "killcavespiders", QuestType.WEEKLY);
		weight = PassManager.DARKZONE_KILL_QUEST_WEIGHT;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!PlayerManager.isRealPlayer(killEvent.getKillerPlayer())) return;
		if(!OldMobManager.mobIsType(killEvent.getDead(), CaveSpider.class)) return;

		progressQuest(killEvent.getKillerPitPlayer(), 1);
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.FERMENTED_SPIDER_EYE)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Kill &c" + Misc.formatLarge(questLevel.getRequirement(pitPlayer)) + " &7cave spiders",
						"",
						"&7Progress: &3" + Misc.formatLarge(progress) + "&7/&3" + Misc.formatLarge(questLevel.getRequirement(pitPlayer)) + " &8[" +
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
		questLevels.add(new QuestLevel(600, 100));
		questLevels.add(new QuestLevel(900, 150));
		questLevels.add(new QuestLevel(1200, 200));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
