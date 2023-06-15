package net.pitsim.pitsim.battlepass;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class PassQuest implements Listener {
	private String displayName;
	public String refName;
	public QuestType questType;
	public int weight = PassManager.DEFAULT_QUEST_WEIGHT;

	private int nextRewardIndex = 0;
	public List<QuestLevel> questLevels = new ArrayList<>();

	static {
//		Quest ideas
//		weekly quests (grinding) - kill x (mobs)
//		weekly quests (darkzone) - brew potions, kill bosses, incinerate drops, harvest souls
//		weekly quests (anti-progression) -
//		weekly quests (misc) - upgrade renown
//		weekly quests (funny) - kill a player with the judgement hopper,
//		attack the pit blob x times, kill players while not wearing a chestplate, hit megastreak without armor
//		weekly quests (enchant combinations) - Streak with unpopular enchants (don't include exe) Sweaty/Moct (cb damage, shark, beserker, king buster).

//		ideas
//		maybe add a few useless enchants and items to pair with quest
//		mess with perks
//		welcome/gg prestige/gf loss
//		bounties claimed
//		use perun without reg
	}

	public PassQuest(String displayName, String refName, QuestType questType) {
		this.displayName = displayName;
		this.refName = refName;
		this.questType = questType;

		createPossibleStates();
	}

	public abstract ItemStack getDisplayStack(PitPlayer pitPlayer, QuestLevel questLevel, double progress);

	//	Only applies to weekly quests
	public abstract void createPossibleStates();

	//	Multiplier to specific players requirement (would not advise making this number ever go down over time
//	as the player could then have the quest jump from incomplete to complete)
	public abstract double getMultiplier(PitPlayer pitPlayer);

	public QuestLevel getDailyState() {
		return questLevels.get(0);
	}

	public QuestLevel getQuestLevel() {
		if(questType == QuestType.DAILY) return getDailyState();
		return PassManager.currentPass.weeklyQuests.getOrDefault(this, null);
	}

	public boolean isQuestActive() {
		if(PassManager.currentPass == null) return false;
		if(questType == QuestType.WEEKLY && !PassManager.currentPass.weeklyQuests.containsKey(this)) return false;
		return true;
	}

	public boolean canProgressQuest(PitPlayer pitPlayer) {
		if(PassManager.currentPass == null) return false;
		PassData passData = pitPlayer.getPassData(PassManager.currentPass.startDate);
		if(questType == QuestType.WEEKLY && !PassManager.currentPass.weeklyQuests.containsKey(this)) return false;

		double progression = passData.questCompletion.getOrDefault(refName, 0.0);
//		The upper code should work fine but had to use the lower code because of it being broken at the start of the pass
//		return progression < getQuestLevel().getRequirement(pitPlayer);
		return progression != Double.MAX_VALUE;
	}

	public void progressQuest(PitPlayer pitPlayer, double amount) {
		if(!canProgressQuest(pitPlayer)) return;
		PassData passData = pitPlayer.getPassData(PassManager.currentPass.startDate);
		double newValue = passData.questCompletion.getOrDefault(refName, 0.0) + amount;
		if(newValue >= getQuestLevel().getRequirement(pitPlayer)) {
			complete(pitPlayer);
			passData.questCompletion.put(refName, Double.MAX_VALUE);
			return;
		}
		passData.questCompletion.put(refName, newValue);
	}

	public void complete(PitPlayer pitPlayer) {
		PassData passData = pitPlayer.getPassData(PassManager.currentPass.startDate);
		passData.totalPoints += getQuestLevel().rewardPoints;
		Sounds.COMPLETE_QUEST.play(pitPlayer.player);
		AOutput.send(pitPlayer.player, "&e&lQUEST!&7 Completed " + getDisplayName() +
				"&7 earning &3" + getQuestLevel().rewardPoints + " &7quest points");
	}

	public String getDisplayName() {
		return ChatColor.translateAlternateColorCodes('&', displayName);
	}

	public class QuestLevel {
		public int rewardIndex = nextRewardIndex++;

		private double requirement;
		public int rewardPoints;

		public QuestLevel(double requirement, int rewardPoints) {
			this.requirement = requirement;
			this.rewardPoints = rewardPoints;
		}

		public double getRequirement(PitPlayer pitPlayer) {
			return requirement * getMultiplier(pitPlayer);
		}
	}

	public enum QuestType {
		DAILY,
		WEEKLY
	}
}
