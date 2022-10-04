package dev.kyro.pitsim.battlepass;

import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class PassQuest implements Listener {
	public String displayName;
	public String refName;
	public QuestType questType;

	static {
//		Quest ideas
//		daily - player kills, bot kills, something darkzone
//		weekly quests (grinding) - grind xp, grind gold, hours played, prestige, kill x (mobs)
//		weekly quests (darkzone) - brew potions, kill bosses
//		weekly quests (anti-progression) - use helmet gold, win auctions
//		weekly quests (misc) - earn guild rep, mlb hits
//		weekly quests (funny) - punch x unique players, kill a player with the judgement hopper, attack the pit blob x times
//		weekly quests (dumb contracts) - kill players while not wearing a chestplate, hit megastreak without armor
	}

	public PassQuest(String displayName, String refName, QuestType questType) {
		this.displayName = displayName;
		this.refName = refName;
		this.questType = questType;
	}

	public abstract ItemStack getDisplayItem(QuestLevel questLevel, double progression);

//	Only applies to daily quests
	public abstract QuestLevel getDailyState();

//	Only applies to weekly quests
	public abstract List<QuestLevel> getWeeklyPossibleStates();

	public QuestLevel getQuestLevel() {
		if(questType == QuestType.DAILY) return getDailyState();
		return PassManager.currentPass.weeklyQuests.getOrDefault(this, null);
	}

	public boolean canProgressQuest(PitPlayer pitPlayer) {
		PassData passData = pitPlayer.getPassData(PassManager.currentPass.startDate);
		if(questType == QuestType.WEEKLY && !PassManager.currentPass.weeklyQuests.containsKey(this)) return false;

		double progression = passData.questCompletion.getOrDefault(refName, 0.0);
		return progression < getQuestLevel().requirement;
	}

	public void progressQuest(PitPlayer pitPlayer, double amount) {
		if(!canProgressQuest(pitPlayer)) return;
		PassData passData = pitPlayer.getPassData(PassManager.currentPass.startDate);
		double newValue = passData.questCompletion.getOrDefault(refName, 0.0) + amount;
		if(newValue >= getQuestLevel().requirement) {
			complete(pitPlayer);
			passData.questCompletion.put(refName, getQuestLevel().requirement);
			return;
		}
		passData.questCompletion.put(refName, newValue);
	}

//	TODO: Play sound and send message
	public void complete(PitPlayer pitPlayer) {
		PassData passData = pitPlayer.getPassData(PassManager.currentPass.startDate);
		passData.totalPoints += getQuestLevel().rewardPoints;
	}

	public static class QuestLevel {
		public double requirement;
		public int rewardPoints;

		public QuestLevel(double requirement, int rewardPoints) {
			this.requirement = requirement;
			this.rewardPoints = rewardPoints;
		}
	}

	public enum QuestType {
		DAILY,
		WEEKLY
	}
}
