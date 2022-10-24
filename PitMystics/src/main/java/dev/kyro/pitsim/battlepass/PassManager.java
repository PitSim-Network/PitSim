package dev.kyro.pitsim.battlepass;

import dev.kyro.pitsim.ParticleColor;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.acosmetics.CosmeticManager;
import dev.kyro.pitsim.battlepass.rewards.*;
import dev.kyro.pitsim.controllers.FirestoreManager;
import dev.kyro.pitsim.controllers.objects.Config;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class PassManager implements Listener {
	public static List<PitSimPass> pitSimPassList = new ArrayList<>();
	public static PitSimPass currentPass;

	public static List<PassQuest> questList = new ArrayList<>();

	public static final int QUESTS_PER_WEEK = 6;
	public static final int DEFAULT_QUEST_WEIGHT = 10;
	public static final int DARKZONE_KILL_QUEST_WEIGHT = 5;
	public static final int POINTS_PER_TIER = 200;

	//	Create the passes
	public static void registerPasses() {
		registerPass(new PitSimPass(getDate("1/1/2022")));

		PitSimPass pitSimPass = new PitSimPass(getDate("9/12/2022"))
				.registerReward(new PassXpReward(2_500L), PitSimPass.RewardType.FREE, 1)
				.registerReward(new PassXpReward(5_000L), PitSimPass.RewardType.PREMIUM, 1)
				.registerReward(new PassGoldReward(50_000), PitSimPass.RewardType.PREMIUM, 2)
				.registerReward(new PassRenownReward(4), PitSimPass.RewardType.PREMIUM, 3)
				.registerReward(new PassVileReward(10), PitSimPass.RewardType.PREMIUM, 4)
				.registerReward(new PassFeatherReward(1), PitSimPass.RewardType.PREMIUM, 5)
				.registerReward(new PassSwordReward(2), PitSimPass.RewardType.PREMIUM, 6)
				.registerReward(new PassPantsReward(2), PitSimPass.RewardType.PREMIUM, 7)
				.registerReward(new PassBowReward(2), PitSimPass.RewardType.PREMIUM, 8)
				.registerReward(new PassCosmeticReward(Material.BREWING_STAND_ITEM, CosmeticManager.getCosmetic("potionaura"),
						ParticleColor.AQUA), PitSimPass.RewardType.PREMIUM, 9)
				.registerReward(new PassXpReward(800_000L), PitSimPass.RewardType.PREMIUM, 27);
		registerPass(pitSimPass);

		registerPass(new PitSimPass(getDate("1/1/2023")));

		updateCurrentPass();
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				updateCurrentPass();
			}
//		}.runTaskTimer(PitSim.INSTANCE, 0, 100);
		}.runTaskTimer(PitSim.INSTANCE, Misc.getRunnableOffset(1) + 60 * 20, 60 * 20);
	}

	public static String getFormattedTimeUntilNextPass() {
		DecimalFormat format = new DecimalFormat("#00");
		long timeUntil = getTimeUntilNextPass();
		if(timeUntil == -1) return "&c&lINDEFINITE";
		long days = timeUntil / (1000 * 60 * 60 * 24);
		timeUntil %= (1000 * 60 * 60 * 24);
		long hours = timeUntil / (1000 * 60 * 60);
		timeUntil %= (1000 * 60 * 60);
		long minutes = timeUntil / (1000 * 60);
		return "&3" + format.format(days) + "&7d &3" + format.format(hours) + "&7h &3" + format.format(minutes) + "&7m";
	}

	public static long getTimeUntilNextPass() {
		for(int i = 0; i < pitSimPassList.size(); i++) {
			PitSimPass testPass = pitSimPassList.get(i);
			if(testPass != currentPass) continue;
			if(i + 1 == pitSimPassList.size()) return -1;
			PitSimPass nextPass = pitSimPassList.get(i + 1);
			return nextPass.startDate.getTime() - Misc.convertToEST(new Date()).getTime();
		}
		return -1;
	}

	public static void registerQuest(PassQuest quest) {
		Bukkit.getPluginManager().registerEvents(quest, PitSim.INSTANCE);
		questList.add(quest);
	}

	public static void registerPass(PitSimPass pass) {
		pass.build();
		pitSimPassList.add(pass);
	}

	@EventHandler
	public static void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		long previousDays = TimeUnit.DAYS.convert(pitPlayer.lastLogin.getTime(), TimeUnit.MILLISECONDS);
		long currentDays = TimeUnit.DAYS.convert(Misc.convertToEST(new Date()).getTime(), TimeUnit.MILLISECONDS);
		if(previousDays != currentDays) {
			PassData passData = pitPlayer.getPassData(currentPass.startDate);
//			passData.questCompletion
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

	public static List<PassQuest> getWeightedRandomQuests(List<PassQuest> possibleQuests) {
		List<PassQuest> weightedRandomQuests = new ArrayList<>();
		for(PassQuest quest : possibleQuests) weightedRandomQuests.addAll(Collections.nCopies(quest.weight, quest));
		return weightedRandomQuests;
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
	public static boolean claimReward(PitPlayer pitPlayer, PitSimPass.RewardType rewardType, int tier) {
		PassData passData = pitPlayer.getPassData(currentPass.startDate);
		boolean success = false;
		if(rewardType == PitSimPass.RewardType.FREE) {
			success = currentPass.freePassRewards.get(tier).giveReward(pitPlayer);
			if(success) passData.claimedFreeRewards.put(tier, true);
		} else if(rewardType == PitSimPass.RewardType.PREMIUM) {
			success = currentPass.premiumPassRewards.get(tier).giveReward(pitPlayer);
			if(success) passData.claimedPremiumRewards.put(tier, true);
		}
		if(success) {
			Sounds.GIVE_REWARD.play(pitPlayer.player);
		} else {
			Sounds.NO.play(pitPlayer.player);
		}
		return success;
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

		if(newPass != currentPass) {
			currentPass = newPass;
			loadPassData();
		}

		long daysPassed = TimeUnit.DAYS.convert(Misc.convertToEST(new Date()).getTime() - currentPass.startDate.getTime(), TimeUnit.MILLISECONDS);
		int weeksPassed = (int) (daysPassed / 7) + 1;
		int newQuests = weeksPassed * QUESTS_PER_WEEK - currentPass.weeklyQuests.size();

		List<PassQuest> possibleWeeklyQuests = getWeeklyQuests();
		possibleWeeklyQuests.removeAll(currentPass.weeklyQuests.keySet());
		List<PassQuest> weightedWeeklyQuests = getWeightedRandomQuests(possibleWeeklyQuests);
		boolean addedQuests = false;
		for(int i = 0; i < newQuests; i++) {
			if(weightedWeeklyQuests.isEmpty()) break;
			PassQuest passQuest = weightedWeeklyQuests.get(new Random().nextInt(weightedWeeklyQuests.size()));
			weightedWeeklyQuests.removeAll(Collections.singleton(passQuest));
			currentPass.weeklyQuests.put(passQuest, passQuest.getWeeklyPossibleStates().get(new Random().nextInt(passQuest.getWeeklyPossibleStates().size())));
			addedQuests = true;
		}
		if(addedQuests) {
			currentPass.writeToConfig();
			FirestoreManager.CONFIG.save();
		}
	}

//	TODO: Main server only, but also run when you send updated data via plugin message (wiji)
	public static void loadPassData() {
		if(!currentPass.startDate.equals(FirestoreManager.CONFIG.currentPassStart)) {
			FirestoreManager.CONFIG.currentPassStart = currentPass.startDate;
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
