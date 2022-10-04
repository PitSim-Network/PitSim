package dev.kyro.pitsim.controllers.objects;

import com.google.cloud.firestore.annotation.Exclude;
import dev.kyro.pitsim.controllers.FirestoreManager;
import dev.kyro.pitsim.misc.PrivateInfo;

import java.util.*;

public class Config {
	@Exclude
	public boolean onSaveCooldown = false;
	@Exclude
	public boolean saveQueued = false;

	public String prefix = "";
	public String errorPrefix = "&c";
	public List<String> whitelistedIPs = PrivateInfo.WHITELISTED_IPS;
	public boolean nons = true;

	public HashMap<String, Integer> boosters = new HashMap<>();

//	PitSim pass stuff
	public Date currentPass;
	public Map<String, WeeklyQuestData> activeWeeklyQuests = new HashMap<>();
	public static class WeeklyQuestData {
		public int tier;
	}

	public Security security = new Security();
	public static class Security {
		public boolean requireVerification = false;
		public boolean requireCaptcha = false;
	}

	public List<Auction> auctions = Arrays.asList(null, null, null);
	public static class Auction {
		public int item;
		public int itemData;
		public long start;
		public List<String> bids = new ArrayList<>();
	}

	public Config() {}

	@Exclude
	public void save() {
		if(onSaveCooldown && !saveQueued) {
			saveQueued = true;
			new Thread(() -> {
				try {
					Thread.sleep(1500);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
				saveQueued = false;
				save();
			}).start();
		}
		if(!saveQueued && !onSaveCooldown) {
			FirestoreManager.FIRESTORE.collection(FirestoreManager.SERVER_COLLECTION).document(FirestoreManager.CONFIG_DOCUMENT).set(this);
			System.out.println("Saving PitSim Config");
			onSaveCooldown = true;
			new Thread(() -> {
				try {
					Thread.sleep(1500);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
				onSaveCooldown = false;
			}).start();
		}
	}
}
