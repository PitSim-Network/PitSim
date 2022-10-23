package dev.kyro.pitsim.controllers;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.AuctionData;
import dev.kyro.pitsim.controllers.objects.Config;
import dev.kyro.pitsim.misc.FileResourcesUtils;

import java.io.IOException;
import java.io.InputStream;

public class FirestoreManager {
	public static Firestore FIRESTORE;

	public static final String SERVER_COLLECTION = "pitsim";
	public static final String CONFIG_DOCUMENT = "config";
	public static final String AUCTION_DOCUMENT = "auction";

	public static final String PLAYERDATA_COLLECTION = "pitsim-playerdata";

	public static Config CONFIG;
	public static AuctionData AUCTION;

	public static void init() {

		try {
			System.out.println("Loading PitSim database");
			InputStream serviceAccount = new FileResourcesUtils().getFileFromResourceAsStream("google-key.json");
			GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(credentials)
					.build();
			try {
				FirebaseApp.initializeApp(options);
			} catch(IllegalStateException exception) {
				System.out.println("Firestore already initialized");
			}

			FIRESTORE = FirestoreClient.getFirestore();
			System.out.println("PitSim database loaded!");
		} catch(IOException exception) {
			System.out.println("PitSim database failed to load. Disabling plugin...");
			PitSim.INSTANCE.getServer().getPluginManager().disablePlugin(PitSim.INSTANCE);
			return;
		}

//		new Thread(FirestoreManager::fetchDocuments).start();
		FirestoreManager.fetchDocuments();
	}

	public static void fetchDocuments() {
		System.out.println("Loading essential PitSim data");
		try {
			if(!FIRESTORE.collection(SERVER_COLLECTION).document(CONFIG_DOCUMENT).get().get().exists()) {
				CONFIG = new Config();
				CONFIG.save();
			} else CONFIG = FIRESTORE.collection(SERVER_COLLECTION).document(CONFIG_DOCUMENT).get().get().toObject(Config.class);

			if(!FIRESTORE.collection(SERVER_COLLECTION).document(AUCTION_DOCUMENT).get().get().exists()) {
				AUCTION = new AuctionData();
				AUCTION.save();
			} else AUCTION = FIRESTORE.collection(SERVER_COLLECTION).document(AUCTION_DOCUMENT).get().get().toObject(AuctionData.class);

		} catch(Exception exception) {
			exception.printStackTrace();
			System.out.println("Firestore failed to load critical data. Disabling plugin...");
			PitSim.INSTANCE.getServer().getPluginManager().disablePlugin(PitSim.INSTANCE);
		}
		System.out.println("PitSim data loaded");
	}
}
