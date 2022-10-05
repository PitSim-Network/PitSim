package dev.kyro.pitsim.battlepass;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.battlepass.rewards.PassXpReward;
import dev.kyro.pitsim.controllers.FirestoreManager;
import dev.kyro.pitsim.controllers.objects.Config;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class PassManager implements Listener {
	public static List<PitSimPass> pitSimPassList = new ArrayList<>();
	public static PitSimPass currentPass;

	public static List<PassQuest> questList = new ArrayList<>();

	public static final int QUESTS_PER_WEEK = 6;

	public static void registerQuest(PassQuest quest) {
		Bukkit.getPluginManager().registerEvents(quest, PitSim.INSTANCE);
		questList.add(quest);
	}

	public static void registerPass(PitSimPass pass) {
		pass.build();
		pitSimPassList.add(pass);
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				updateCurrentPass();
			}
//		}.runTaskTimer(PitSim.INSTANCE, 0, 100);
		}.runTaskTimer(PitSim.INSTANCE, Misc.getRunnableOffset(1), 60 * 20);
	}

	@EventHandler
	public static void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		long previousDays = TimeUnit.DAYS.convert(pitPlayer.lastLogin.getTime(), TimeUnit.MILLISECONDS);
		long currentDays = TimeUnit.DAYS.convert(Misc.convertToEST(new Date()).getTime(), TimeUnit.MILLISECONDS);
		if(previousDays != currentDays) {
//			TODO: Reset daily quests
		}

		pitPlayer.lastLogin = Misc.convertToEST(new Date());
	}

	public static List<PassQuest> getDailyQuests() {
		List<PassQuest> dailyQuests = new ArrayList<>();
		for(PassQuest passQuest : questList) if(passQuest.questType == PassQuest.QuestType.DAILY) dailyQuests.add(passQuest);
		return dailyQuests;
	}

	public static List<PassQuest> getWeeklyQuests() {
		List<PassQuest> weeklyQuests = new ArrayList<>();
		for(PassQuest passQuest : questList) if(passQuest.questType == PassQuest.QuestType.WEEKLY) weeklyQuests.add(passQuest);
		return weeklyQuests;
	}

//	fetch passquest by refname
	public static PassQuest getQuest(String refName) {
		for(PassQuest passQuest : questList) if(passQuest.refName.equals(refName)) return passQuest;
		return null;
	}

	public static double getProgression(PitPlayer pitPlayer, PassQuest passQuest) {
		PassData passData = pitPlayer.getPassData(currentPass.startDate);
		return passData.questCompletion.getOrDefault(passQuest.refName, 0.0);
	}

//	Check to see if a pitplayer has completed their pass
	public static boolean hasCompletedPass(PitPlayer pitPlayer) {
		return pitPlayer.getPassData(PassManager.currentPass.startDate).getCompletedTiers() >= currentPass.tiers;
	}

//	For a given reward type, check to see if it exists in the current pass for a given tier
	public static boolean hasReward(PitSimPass.RewardType rewardType, int tier) {
		if(rewardType == PitSimPass.RewardType.FREE) {
			return currentPass.freePassRewards.containsKey(tier);
		} else if(rewardType == PitSimPass.RewardType.PREMIUM) {
			return currentPass.premiumPassRewards.containsKey(tier);
		}
		return false;
	}

//	Check to see if a pitplayer has claimed a reward
	public static boolean hasClaimedReward(PitPlayer pitPlayer, PitSimPass.RewardType rewardType, int tier) {
		PassData passData = pitPlayer.getPassData(currentPass.startDate);
		if(rewardType == PitSimPass.RewardType.FREE) {
			return passData.claimedFreeRewards.containsKey(tier);
		} else if(rewardType == PitSimPass.RewardType.PREMIUM) {
			return passData.claimedPremiumRewards.containsKey(tier);
		}
		return false;
	}

//	Check to see if a player can claim a given reward
	public static boolean canClaimReward(PitPlayer pitPlayer, PitSimPass.RewardType rewardType, int tier) {
		PassData passData = pitPlayer.getPassData(currentPass.startDate);
		if(passData.getCompletedTiers() < tier || hasClaimedReward(pitPlayer, rewardType, tier)) return false;
		if(rewardType == PitSimPass.RewardType.PREMIUM) return passData.hasPremium;
		return true;
	}

//	Claim a reward for a pitplayer
	public static void claimReward(PitPlayer pitPlayer, PitSimPass.RewardType rewardType, int tier) {
		PassData passData = pitPlayer.getPassData(currentPass.startDate);
		if(rewardType == PitSimPass.RewardType.FREE) {
			passData.claimedFreeRewards.put(tier, true);
			currentPass.freePassRewards.get(tier).giveReward(pitPlayer);
		} else if(rewardType == PitSimPass.RewardType.PREMIUM) {
			passData.claimedPremiumRewards.put(tier, true);
			currentPass.premiumPassRewards.get(tier).giveReward(pitPlayer);
		}
	}

//	Create the passes
	static {
		registerPass(new PitSimPass(getDate("9/1/2022")));

		PitSimPass pitSimPass = new PitSimPass(getDate("10/1/2022"))
				.registerReward(new PassXpReward(20L), PitSimPass.RewardType.FREE, 1)
				.registerReward(new PassXpReward(40L), PitSimPass.RewardType.PREMIUM, 2)
				.registerReward(new PassXpReward(60L), PitSimPass.RewardType.FREE, 2)
				.registerReward(new PassXpReward(80L), PitSimPass.RewardType.PREMIUM, 3)
				.registerReward(new PassXpReward(800_000L), PitSimPass.RewardType.PREMIUM, 30);
		registerPass(pitSimPass);

		updateCurrentPass();
	}

	public static void updateCurrentPass() {
		Date now = Misc.convertToEST(new Date());
		PitSimPass newPass = null;
		boolean foundCurrentPass = false;
		for(int i = 0; i < pitSimPassList.size(); i++) {
			PitSimPass testPass = pitSimPassList.get(i);
			if(now.getTime() > testPass.startDate.getTime()) continue;
			newPass = pitSimPassList.get(i - 1);
			foundCurrentPass = true;
			break;
		}
		if(!foundCurrentPass) newPass = pitSimPassList.get(pitSimPassList.size() - 1);

		if(newPass == currentPass) {
			long daysPassed = TimeUnit.DAYS.convert(Misc.convertToEST(new Date()).getTime() - currentPass.startDate.getTime(), TimeUnit.MILLISECONDS);
			int weeksPassed = (int) (daysPassed / 7) + 1;
			int newQuests = weeksPassed * QUESTS_PER_WEEK - currentPass.weeklyQuests.size();

			List<PassQuest> possibleWeeklyQuests = PassManager.getWeeklyQuests();
			possibleWeeklyQuests.removeAll(currentPass.weeklyQuests.keySet());
			Collections.shuffle(possibleWeeklyQuests);
			for(int i = 0; i < newQuests; i++) {
				if(possibleWeeklyQuests.isEmpty()) {
					newQuests = 0;
					break;
				}
				PassQuest passQuest = possibleWeeklyQuests.remove(0);
				System.out.println(passQuest.getWeeklyPossibleStates());
				currentPass.weeklyQuests.put(passQuest, passQuest.getWeeklyPossibleStates().get(new Random().nextInt(passQuest.getWeeklyPossibleStates().size())));
			}
			if(newQuests != 0) {
				currentPass.writeToConfig();
				FirestoreManager.CONFIG.save();
			}
			return;
		}

		currentPass = newPass;
		loadPassData();
	}

//	TODO: Main server only
	public static void loadPassData() {
		if(currentPass.startDate != FirestoreManager.CONFIG.currentPassStart) {
			FirestoreManager.CONFIG.currentPassData = new Config.CurrentPassData();
			FirestoreManager.CONFIG.save();
		}

		for(Map.Entry<String, Integer> entry : FirestoreManager.CONFIG.currentPassData.activeWeeklyQuests.entrySet()) {
			PassQuest passQuest = getQuest(entry.getKey());
			if(passQuest == null) continue;
			currentPass.weeklyQuests.put(passQuest, passQuest.getWeeklyPossibleStates().get(entry.getValue()));
		}
	}

	public static Date getDate(String dateString) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		try {
			return Misc.convertToEST(dateFormat.parse(dateString));
		} catch(ParseException ignored) {}
		return null;
	}
}
