package dev.kyro.pitsim.controllers;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.adarkzone.DarkzoneLeveling;
import dev.kyro.pitsim.adarkzone.progression.DarkzoneData;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.events.MessageEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.storage.StorageManager;
import dev.kyro.pitsim.storage.StorageProfile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static dev.kyro.pitsim.controllers.FirestoreManager.PLAYERDATA_COLLECTION;

public class MigrationManager implements Listener {

	@EventHandler
	public void onMessage(MessageEvent event) throws ExecutionException, InterruptedException {
		PluginMessage message = event.getMessage();
		List<String> strings = message.getStrings();

		if(strings.size() < 2 || !strings.get(0).equals("MIGRATE ITEMS")) return;
		UUID uuid = UUID.fromString(strings.get(1));
		StorageProfile profile = StorageManager.getProfile(uuid);

		PitPlayer pitPlayer;
		DocumentSnapshot documentSnapshot = FirestoreManager.FIRESTORE.collection(PLAYERDATA_COLLECTION)
				.document(uuid.toString()).get().get();

		if(documentSnapshot.exists()) {
			pitPlayer = documentSnapshot.toObject(PitPlayer.class);
		} else {
			AOutput.log("CRITICAL ERROR: COULD NOT LOAD PITPLAYER FOR " + uuid);
			return;
		}

		if(pitPlayer == null) {
			AOutput.log("CRITICAL ERROR: COULD NOT LOAD PITPLAYER FOR " + uuid);
			return;
		}
		pitPlayer.uuid = uuid;
		pitPlayer.darkzoneData.preDarkzoneUpdatePrestige = pitPlayer.prestige;

		PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);
		pitPlayer.darkzoneData.altarXP = DarkzoneLeveling.getXPToLevel(prestigeInfo.getDarkzoneLevel());

		for(int i = 0; i < profile.getCachedInventory().length; i++) {
			ItemStack itemStack = profile.getCachedInventory()[i];

			PitItem pitItem = getPitItemFromLegacy(itemStack);
			if(pitItem == null) continue;
			profile.getCachedInventory()[i] = convertItem(pitPlayer, pitItem, itemStack);
		}

		for(int i = 0; i < profile.getArmor().length; i++) {
			ItemStack itemStack = profile.getArmor()[i];

			PitItem pitItem = getPitItemFromLegacy(itemStack);
			if(pitItem == null) continue;
			profile.getArmor()[i] = convertItem(pitPlayer, pitItem, itemStack);
		}

		for(Inventory page : profile.getEnderChest()) {
			for(int i = 9; i < page.getSize() - 9; i++) {
				ItemStack itemStack = page.getContents()[i];
				if(Misc.isAirOrNull(itemStack)) continue;

				PitItem pitItem = getPitItemFromLegacy(itemStack);
				if(pitItem == null) continue;
				page.setItem(i, convertItem(pitPlayer, pitItem, itemStack));
			}
		}

		pitPlayer.save(true, true);
	}

	@EventHandler
	public void onAltarMigrationMessage(MessageEvent event) throws ExecutionException, InterruptedException {

	}

	public static PitItem getPitItemFromLegacy(ItemStack itemStack) {
		if(Misc.isAirOrNull(itemStack)) return null;
		NBTItem nbtItem = new NBTItem(itemStack);
		for(PitItem pitItem : ItemFactory.pitItems) if(pitItem.isLegacyItem(itemStack, nbtItem)) return pitItem;
		return null;
	}

	public static ItemStack convertItem(PitPlayer pitPlayer, PitItem pitItem, ItemStack itemStack) {
		NBTItem nbtItem = new NBTItem(itemStack);
		if(nbtItem.hasKey(NBTTag.CUSTOM_ITEM.getRef())) throw new RuntimeException();
		ItemStack replacementItem = pitItem.getReplacementItem(pitPlayer, itemStack, nbtItem);
		if(replacementItem == null) return null;
		pitItem.updateItem(replacementItem);
		return replacementItem;
	}

	public static void migrateAltar() {
		ApiFuture<QuerySnapshot> future = FirestoreManager.FIRESTORE.collection(PLAYERDATA_COLLECTION).get();
		List<QueryDocumentSnapshot> documents;
		try {
			documents = future.get().getDocuments();
		} catch(InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
		for(QueryDocumentSnapshot document : documents) {
			migrateAltarPlayer(document);
		}
	}

	public static void migrateAltarPlayer(DocumentSnapshot document) {

		UUID uuid = UUID.fromString(document.getId());

		PitPlayer pitPlayer;

		if(document.exists()) {
			pitPlayer = document.toObject(PitPlayer.class);
		} else {
			AOutput.log("CRITICAL ERROR: COULD NOT LOAD PITPLAYER FOR " + uuid);
			return;
		}

		if(pitPlayer == null) {
			AOutput.log("CRITICAL ERROR: COULD NOT LOAD PITPLAYER FOR " + uuid);
			return;
		}
		pitPlayer.uuid = uuid;
		pitPlayer.darkzoneData.preDarkzoneUpdatePrestige = pitPlayer.prestige;

		DarkzoneData.SkillBranchData skillBranchData = pitPlayer.darkzoneData.skillBranchUnlocks.getOrDefault("altar",
				new DarkzoneData.SkillBranchData());

		int level = getProgressionTier(pitPlayer.prestige);

		if(level >= 1) skillBranchData.majorUnlocks.add("unlock-basic-pedestals");
		if(level >= 5) skillBranchData.majorUnlocks.add("unlock-pedestal-wealth");

		int pathLevel = level - (level >= 1 ? 1 : 0) - (level >= 5 ? 1 : 0);

		skillBranchData.pathUnlocks.put("altar-xp", Math.max(pathLevel,
				skillBranchData.pathUnlocks.getOrDefault("altar-xp", 0)));
		pitPlayer.darkzoneData.skillBranchUnlocks.put("altar", skillBranchData);


		PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);
		pitPlayer.darkzoneData.altarXP = DarkzoneLeveling.getXPToLevel(prestigeInfo.getDarkzoneLevel());

		pitPlayer.save(false, true);
	}

	public static int getProgressionTier(int prestige) {
		if(prestige < 15) return 0;
		else if(prestige < 20) return 1;
		else if(prestige < 25) return 2;
		else if(prestige < 30) return 3;
		else if(prestige < 40) return 4;
		else if(prestige < 45) return 5;
		else if(prestige < 50) return 6;
		else if(prestige < 55) return 7;
		else return 8;
	}
}
