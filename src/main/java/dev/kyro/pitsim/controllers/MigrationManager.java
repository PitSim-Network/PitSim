package dev.kyro.pitsim.controllers;

import com.google.cloud.firestore.DocumentSnapshot;
import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
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

public class MigrationManager implements Listener {

	@EventHandler
	public void onMessage(MessageEvent event) throws ExecutionException, InterruptedException {
		PluginMessage message = event.getMessage();
		List<String> strings = message.getStrings();

		if(strings.size() < 2 || !strings.get(0).equals("MIGRATE ITEMS")) return;
		UUID uuid = UUID.fromString(strings.get(1));
		StorageProfile profile = StorageManager.getProfile(uuid);

		PitPlayer pitPlayer;
		DocumentSnapshot documentSnapshot = FirestoreManager.FIRESTORE.collection(FirestoreManager.PLAYERDATA_COLLECTION)
				.document(uuid.toString()).get().get();

		if(documentSnapshot.exists()) {
			pitPlayer = documentSnapshot.toObject(PitPlayer.class);
		} else {
			System.out.println("CRITICAL ERROR: COULD NOT LOAD PITPLAYER FOR " + uuid);
			return;
		}

		if(pitPlayer == null) {
			System.out.println("CRITICAL ERROR: COULD NOT LOAD PITPLAYER FOR " + uuid);
			return;
		}
		pitPlayer.uuid = uuid;

		for(int i = 0; i < profile.getCachedInventory().length; i++) {
			ItemStack itemStack = profile.getCachedInventory()[i];
			if(Misc.isAirOrNull(itemStack)) continue;
			NBTItem nbtItem = new NBTItem(itemStack);
			PitItem pitItem = ItemFactory.getItem(itemStack);
			if(pitItem == null || !pitItem.isLegacyItem(itemStack, nbtItem)) continue;
			profile.getCachedInventory()[i] = pitItem.getReplacementItem(pitPlayer, itemStack, nbtItem);
		}

		for(int i = 0; i < profile.getArmor().length; i++) {
			ItemStack itemStack = profile.getArmor()[i];
			if(Misc.isAirOrNull(itemStack)) continue;
			NBTItem nbtItem = new NBTItem(itemStack);
			PitItem pitItem = ItemFactory.getItem(itemStack);
			if(pitItem == null || !pitItem.isLegacyItem(itemStack, nbtItem)) continue;
			profile.getArmor()[i] = pitItem.getReplacementItem(pitPlayer, itemStack, nbtItem);
		}

		for(Inventory page : profile.getEnderChest()) {
			for(int i = 0; i < page.getContents().length; i++) {
				ItemStack itemStack = page.getContents()[i];
				if(Misc.isAirOrNull(itemStack)) continue;
				NBTItem nbtItem = new NBTItem(itemStack);
				PitItem pitItem = ItemFactory.getItem(itemStack);
				if(pitItem == null || !pitItem.isLegacyItem(itemStack, nbtItem)) continue;
				page.getContents()[i] = pitItem.getReplacementItem(pitPlayer, itemStack, nbtItem);
			}
		}

		pitPlayer.save(true, true);
	}
}
