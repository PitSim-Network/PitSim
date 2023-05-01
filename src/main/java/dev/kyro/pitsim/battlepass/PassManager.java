package dev.kyro.pitsim.battlepass;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.battlepass.rewards.*;
import dev.kyro.pitsim.controllers.FirestoreManager;
import dev.kyro.pitsim.controllers.objects.Config;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.cosmetics.CosmeticManager;
import dev.kyro.pitsim.cosmetics.particles.ParticleColor;
import dev.kyro.pitsim.events.PitJoinEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class PassManager implements Listener {
	public static List<PitSimPass> pitSimPassList = new ArrayList<>();
	public static PitSimPass currentPass;

	public static List<PassQuest> questList = new ArrayList<>();

	public static final int QUESTS_PER_WEEK = 4;
	public static final int DEFAULT_QUEST_WEIGHT = 10;
	public static final int DARKZONE_KILL_QUEST_WEIGHT = 5;
	public static final int POINTS_PER_TIER = 100;

	//	Create the passes
	public static void registerPasses() {
		registerPass(new PitSimPass(getDate("3/1/2022")));
		int premiumTier = 1;

		PitSimPass pitSimPass;

		pitSimPass = new PitSimPass(getDate("12/24/2022"))
				.registerReward(new PassCosmeticReward(Material.IRON_HOE, CosmeticManager.getCosmetic("reaper"),
						null), PitSimPass.RewardType.FREE, 36)
				.registerReward(new PassCosmeticReward(Material.BREWING_STAND_ITEM, CosmeticManager.getCosmetic("potionaura"),
						ParticleColor.AQUA), PitSimPass.RewardType.PREMIUM, premiumTier++)
				.registerReward(new PassCosmeticReward(Material.GOLD_SWORD, CosmeticManager.getCosmetic("alwaysexe"),
						null), PitSimPass.RewardType.PREMIUM, premiumTier++)
				.registerReward(new PassCosmeticReward(Material.BANNER, 13, CosmeticManager.getCosmetic("solidcape"),
						ParticleColor.LIGHT_PURPLE), PitSimPass.RewardType.PREMIUM, premiumTier++)
				.registerReward(new PassCosmeticReward(Material.WOOL, 6, CosmeticManager.getCosmetic("rainbowtrail"),
						null), PitSimPass.RewardType.PREMIUM, premiumTier++);

		premiumTier = 1;
		pitSimPass = new PitSimPass(getDate("2/1/2023"))
				.registerReward(new PassCosmeticReward(Material.BANNER, CosmeticManager.getCosmetic("solidcape"),
						ParticleColor.BLACK), PitSimPass.RewardType.FREE, 36)
				.registerReward(new PassCosmeticReward(Material.INK_SACK, 4,  CosmeticManager.getCosmetic("blueshell"),
						null), PitSimPass.RewardType.PREMIUM, premiumTier++)
				.registerReward(new PassCosmeticReward(Material.HUGE_MUSHROOM_2, CosmeticManager.getCosmetic("supermario"),
						null), PitSimPass.RewardType.PREMIUM, premiumTier++)
				.registerReward(new PassCosmeticReward(Material.ENCHANTMENT_TABLE, 13, CosmeticManager.getCosmetic("mysticaura"),
						null), PitSimPass.RewardType.PREMIUM, premiumTier++)
				.registerReward(new PassCosmeticReward(Material.WOOL, 1, CosmeticManager.getCosmetic("tetris"),
						null), PitSimPass.RewardType.PREMIUM, premiumTier++);

		premiumTier = 1;
		pitSimPass = new PitSimPass(getDate("3/1/2023"))
				.registerReward(new PassCosmeticReward(Material.INK_SACK, 11,  CosmeticManager.getCosmetic("potionaura"),
						ParticleColor.YELLOW), PitSimPass.RewardType.FREE, 18)
				.registerReward(new PassCosmeticReward(Material.BONE, CosmeticManager.getCosmetic("howl"),
						null), PitSimPass.RewardType.FREE, 36)
				.registerReward(new PassCosmeticReward(Material.BANNER, 10,  CosmeticManager.getCosmetic("solidcape"),
						ParticleColor.GREEN), PitSimPass.RewardType.PREMIUM, premiumTier++)
				.registerReward(new PassCosmeticReward(Material.SPONGE, CosmeticManager.getCosmetic("rat"),
						null), PitSimPass.RewardType.PREMIUM, premiumTier++)
				.registerReward(new PassCosmeticReward(Material.REDSTONE, CosmeticManager.getCosmetic("redstonetrail"),
						null), PitSimPass.RewardType.PREMIUM, premiumTier++)
				.registerReward(new PassCosmeticReward(Material.PISTON_BASE, 1, CosmeticManager.getCosmetic("electricpresence"),
						null), PitSimPass.RewardType.PREMIUM, premiumTier++)
				.registerReward(new PassCosmeticReward(Material.BANNER, 1, CosmeticManager.getCosmetic("solidcape"),
						ParticleColor.DARK_RED), PitSimPass.RewardType.PREMIUM, premiumTier++);

		premiumTier = 1;
		pitSimPass = new PitSimPass(getDate("4/1/2023"))
				.registerReward(new PassCosmeticReward(Material.EMERALD, CosmeticManager.getCosmetic("forgottopay"),
						null), PitSimPass.RewardType.FREE, 36)
				.registerReward(new PassCosmeticReward(Material.INK_SACK, 4,  CosmeticManager.getCosmetic("potionaura"),
						ParticleColor.DARK_BLUE), PitSimPass.RewardType.PREMIUM, premiumTier++)
				.registerReward(new PassCosmeticReward(Material.ANVIL, CosmeticManager.getCosmetic("ironkill"),
						null), PitSimPass.RewardType.PREMIUM, premiumTier++)
				.registerReward(new PassCosmeticReward(Material.FIREWORK_CHARGE, CosmeticManager.getCosmetic("smoketrail"),
						null), PitSimPass.RewardType.PREMIUM, premiumTier++)
				.registerReward(new PassCosmeticReward(Material.BLAZE_POWDER, CosmeticManager.getCosmetic("firecape"),
						null), PitSimPass.RewardType.PREMIUM, premiumTier++);

		registerPass(new PitSimPass(getDate("5/1/2023"))
				.registerStandardRewards()
				.registerReward(new PassCosmeticReward(Material.BANNER, 11, CosmeticManager.getCosmetic("solidcape"),
						ParticleColor.YELLOW), PitSimPass.RewardType.FREE, 18)
				.registerReward(new PassCosmeticReward(Material.INK_SACK, 10, CosmeticManager.getCosmetic("potionaura"),
						null), PitSimPass.RewardType.FREE, 36)
				.registerReward(new PassCosmeticReward(Material.BANNER, 4, CosmeticManager.getCosmetic("solidcape"),
						ParticleColor.BLUE), PitSimPass.RewardType.PREMIUM, 9)
				.registerReward(new PassCosmeticReward(Material.RED_ROSE, CosmeticManager.getCosmetic("embarassed"),
						null), PitSimPass.RewardType.PREMIUM, 18)
				.registerReward(new PassCosmeticReward(Material.SLIME_BALL,  CosmeticManager.getCosmetic("slimetrail"),
						null), PitSimPass.RewardType.PREMIUM, 27)
				.registerReward(new PassCosmeticReward(Material.TNT, CosmeticManager.getCosmetic("explode"),
						null), PitSimPass.RewardType.PREMIUM, 36)
		);

		registerPass(new PitSimPass(getDate("6/1/2023")));
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
			return nextPass.startDate.getTime() - new Date().getTime();
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
	public static void onJoin(PitJoinEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		long previousDays = TimeUnit.DAYS.convert(pitPlayer.lastLogin.getTime(), TimeUnit.MILLISECONDS);
		long currentDays = TimeUnit.DAYS.convert(new Date().getTime(), TimeUnit.MILLISECONDS);
		if(previousDays != currentDays) {
			PassData passData = pitPlayer.getPassData(currentPass.startDate);
//			passData.questCompletion
		}

		pitPlayer.lastLogin = new Date();
	}

	public static List<PassQuest> getDailyQuests() {
		List<PassQuest> dailyQuests = new ArrayList<>();
		for(PassQuest passQuest : questList)
			if(passQuest.questType == PassQuest.QuestType.DAILY) dailyQuests.add(passQuest);
		return dailyQuests;
	}

	public static List<PassQuest> getWeeklyQuests() {
		List<PassQuest> weeklyQuests = new ArrayList<>();
		for(PassQuest passQuest : questList)
			if(passQuest.questType == PassQuest.QuestType.WEEKLY) weeklyQuests.add(passQuest);
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
		Date now = new Date();
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
		}
		loadPassData();

		if(PitSim.serverName.equals("pitsim-1") || PitSim.serverName.equals("pitsimdev-1")) {
			long daysPassed = TimeUnit.DAYS.convert(new Date().getTime() - currentPass.startDate.getTime(), TimeUnit.MILLISECONDS);
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
				currentPass.weeklyQuests.put(passQuest, passQuest.questLevels.get(new Random().nextInt(passQuest.questLevels.size())));
				addedQuests = true;
			}
			if(addedQuests) {
				currentPass.writeToConfig();
				FirestoreManager.CONFIG.save();
			}
		}
	}

	public static void loadPassData() {
		currentPass.weeklyQuests.clear();
		if(!currentPass.startDate.equals(FirestoreManager.CONFIG.currentPassStart)) {
			if(PitSim.serverName.equals("pitsim-1") || PitSim.serverName.equals("pitsimdev-1")) {
				AOutput.log("Creating new pass");
				FirestoreManager.CONFIG.currentPassStart = currentPass.startDate;
				FirestoreManager.CONFIG.currentPassData = new Config.CurrentPassData();
				FirestoreManager.CONFIG.save();
			} else {
				AOutput.log("Attempting to load new pass data");
				new BukkitRunnable() {
					@Override
					public void run() {
						FirestoreManager.CONFIG.load();
					}
				}.runTaskAsynchronously(PitSim.INSTANCE);
			}
		} else {
			for(Map.Entry<String, Integer> entry : FirestoreManager.CONFIG.currentPassData.activeWeeklyQuests.entrySet()) {
				PassQuest passQuest = getQuest(entry.getKey());
				if(passQuest == null) continue;
				currentPass.weeklyQuests.put(passQuest, passQuest.questLevels.get(entry.getValue()));
			}
		}
	}

	public static Date getDate(String dateString) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		TimeZone est = TimeZone.getTimeZone("EST");
		TimeZone edt = TimeZone.getTimeZone("EDT");

		try {
			dateFormat.setTimeZone(est);
			Date date = dateFormat.parse(dateString);

			if(est.inDaylightTime(date)) {
				dateFormat.setTimeZone(edt);
				return dateFormat.parse(dateString);
			} else return date;
		} catch(Exception ignored) {
			return null;
		}
	}
}
