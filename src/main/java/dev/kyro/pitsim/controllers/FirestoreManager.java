package dev.kyro.pitsim.controllers;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.AuctionData;
import dev.kyro.pitsim.controllers.objects.Config;
import dev.kyro.pitsim.misc.FileResourcesUtils;

import java.io.IOException;
import java.io.InputStream;

public class FirestoreManager {
	public static Firestore FIRESTORE;

	public static final String SERVER_COLLECTION = Collection.getCollection(PitSim.serverName).refName[0];
	public static final String CONFIG_DOCUMENT = "config";
	public static final String AUCTION_DOCUMENT = "auction";

	public static final String PLAYERDATA_COLLECTION = SERVER_COLLECTION + "-playerdata";

	public static Config CONFIG;
	public static AuctionData AUCTION;

	public static void init() {
		try {
			AOutput.log("Loading PitSim database");
			InputStream serviceAccount = new FileResourcesUtils().getFileFromResourceAsStream("google-key.json");
			GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(credentials)
					.build();
			try {
				FirebaseApp.initializeApp(options);
			} catch(IllegalStateException exception) {
				AOutput.log("Firestore already initialized");
			}

			FIRESTORE = FirestoreClient.getFirestore();
			AOutput.log("PitSim database loaded!");
		} catch(IOException exception) {
			AOutput.log("PitSim database failed to load. Disabling plugin...");
			PitSim.INSTANCE.getServer().getPluginManager().disablePlugin(PitSim.INSTANCE);
			return;
		}

//		new Thread(FirestoreManager::fetchDocuments).start();
		FirestoreManager.fetchDocuments();
	}

	public static void fetchDocuments() {
		AOutput.log("Loading essential PitSim data");
		try {
			if(!FIRESTORE.collection(SERVER_COLLECTION).document(CONFIG_DOCUMENT).get().get().exists()) {
				CONFIG = new Config();
				CONFIG.save();
			} else {
				CONFIG = FIRESTORE.collection(SERVER_COLLECTION).document(CONFIG_DOCUMENT).get().get().toObject(Config.class);
			}

			if(PitSim.status.isDarkzone()) {
				if(!FIRESTORE.collection(SERVER_COLLECTION).document(AUCTION_DOCUMENT).get().get().exists()) {
					AUCTION = new AuctionData();
					AUCTION.save();
				} else {
					AUCTION = FIRESTORE.collection(SERVER_COLLECTION).document(AUCTION_DOCUMENT).get().get().toObject(AuctionData.class);
				}
			}

		} catch(Exception exception) {
			exception.printStackTrace();
			AOutput.log("Firestore failed to load critical data. Disabling plugin...");
			PitSim.INSTANCE.getServer().getPluginManager().disablePlugin(PitSim.INSTANCE);
		}
		AOutput.log("PitSim data loaded");
	}

	public enum Collection {
		DEV("dev"),
		KYRO("kyro"),
		PITSIM("pitsim", "darkzone");

		public String[] refName;
		Collection(String... refName) {
			this.refName = refName;
		}

		public static Collection getCollection(String serverName) {
			for(Collection value : values()) {
				for(String s : value.refName) {
					if(serverName.contains(s)) return value;
				}
			}
			throw new RuntimeException();
		}
	}
}
