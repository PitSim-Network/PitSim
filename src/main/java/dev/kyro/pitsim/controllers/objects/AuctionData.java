package dev.kyro.pitsim.controllers.objects;

import com.google.cloud.firestore.annotation.Exclude;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.FirestoreManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AuctionData {
	@Exclude
	public boolean onSaveCooldown = false;
	@Exclude
	public boolean saveQueued = false;

	public List<Auction> auctions = Arrays.asList(null, null, null);
	public long endTime;

	public static class Auction {
		public int item;
		public int itemData;
		public List<String> bids = new ArrayList<>();
	}

	public AuctionData() {}

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
			FirestoreManager.FIRESTORE.collection(FirestoreManager.SERVER_COLLECTION).document(FirestoreManager.AUCTION_DOCUMENT).set(this);
			AOutput.log("Saving PitSim Auction data");
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
