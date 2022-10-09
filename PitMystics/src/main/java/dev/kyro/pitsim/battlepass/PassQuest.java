package dev.kyro.pitsim.battlepass;

import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class PassQuest implements Listener {
	private String displayName;
	public String refName;
	public QuestType questType;

	public int nextRewardIndex = 0;

	static {
//		Quest ideas
//		daily - player kills, bot kills, something darkzone
//		weekly quests (grinding) - kill x (mobs)
//		weekly quests (darkzone) - brew potions, kill bosses, incinerate drops, harvest souls
//		weekly quests (anti-progression) - use helmet gold, win auctions
//		weekly quests (misc) - earn guild rep, upgrade renown
//		weekly quests (funny) - punch x unique players, kill a player with the judgement hopper,
//		attack the pit blob x times, kill players while not wearing a chestplate, hit megastreak without armor
//		weekly quests (enchant combinations) - Streak with unpopular enchants (don't include exe) Sweaty/Moct (cb damage, shark, beserker, king buster).

//		ideas
//		maybe add a few useless enchants and items to pair with quest
//		mess with perks
//		welcome/gg quests
	}

	public PassQuest(String displayName, String refName, QuestType questType) {
		this.displayName = displayName;
		this.refName = refName;
		this.questType = questType;
	}

	public abstract ItemStack getDisplayItem(PitPlayer pitPlayer, QuestLevel questLevel, double progress);

//	Only applies to daily quests
	public abstract QuestLevel getDailyState();

//	Only applies to weekly quests
	public abstract List<QuestLevel> getWeeklyPossibleStates();

//	Multiplier to specific players requirement (would not advise making this number ever go down over time
//	as the player could then have the quest jump from incomplete to complete)
	public abstract double getMultiplier(PitPlayer pitPlayer);

	public QuestLevel getQuestLevel() {
		if(questType == QuestType.DAILY) return getDailyState();
		return PassManager.currentPass.weeklyQuests.getOrDefault(this, null);
	}

	public boolean canProgressQuest(PitPlayer pitPlayer) {
		PassData passData = pitPlayer.getPassData(PassManager.currentPass.startDate);
		if(questType == QuestType.WEEKLY && !PassManager.currentPass.weeklyQuests.containsKey(this)) return false;

		double progression = passData.questCompletion.getOrDefault(refName, 0.0);
		return progression < getQuestLevel().getRequirement(pitPlayer);
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

//	TODO: Play sound and send message
	public void complete(PitPlayer pitPlayer) {
		PassData passData = pitPlayer.getPassData(PassManager.currentPass.startDate);
		passData.totalPoints += getQuestLevel().rewardPoints;
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
