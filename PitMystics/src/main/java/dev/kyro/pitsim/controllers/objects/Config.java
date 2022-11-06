package dev.kyro.pitsim.controllers.objects;

import com.google.cloud.firestore.annotation.Exclude;
import dev.kyro.pitsim.PitSim;
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
	public Date currentPassStart;
	public CurrentPassData currentPassData = new CurrentPassData();
	public static class CurrentPassData {
		public Map<String, Integer> activeWeeklyQuests = new HashMap<>();
	}

	public Security security = new Security();
	public static class Security {
		public boolean requireVerification = false;
		public boolean requireCaptcha = false;
	}

	public Config() {}

	@Exclude
	public void save() {
		if(!Objects.equals(PitSim.serverName, "pitsim-1")) return;
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
