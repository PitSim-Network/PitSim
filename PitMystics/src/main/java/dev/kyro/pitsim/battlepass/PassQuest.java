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

	//	Only applies to weekly quests
	public abstract List<QuestLevel> getPossibleStates();

	public boolean canProgressQuest(QuestLevel questLevel, PitPlayer pitPlayer) {
		PassData passData = pitPlayer.getPassData(PassManager.currentPass.startDate);
		if(questType == QuestType.WEEKLY && !PassManager.currentPass.weeklyQuests.contains(this)) return false;

		double progression = passData.questCompletion.getOrDefault(questType.name(), 0.0);
		if(progression > questLevel.requirement) return false;

		return true;
	}

	public void progressQuest(QuestLevel questLevel, PitPlayer pitPlayer, double amount) {
		if(!canProgressQuest(questLevel, pitPlayer)) return;
		PassData passData = pitPlayer.getPassData(PassManager.currentPass.startDate);
		double newValue = passData.questCompletion.getOrDefault(refName, 0.0) + amount;
		if(newValue >= questLevel.requirement) {
			complete(questLevel, pitPlayer);
			passData.questCompletion.put(refName, questLevel.requirement);
			return;
		}
		passData.questCompletion.put(refName, newValue);
	}

	public void complete(QuestLevel questLevel, PitPlayer pitPlayer) {

	}

	public static class QuestLevel {
		public double requirement;
		public int rewardPoints;
	}

	public enum QuestType {
		DAILY,
		WEEKLY
	}
}
