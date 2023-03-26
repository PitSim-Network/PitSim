package dev.kyro.pitsim.controllers;

import com.google.cloud.firestore.DocumentSnapshot;
import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.adarkzone.DarkzoneLeveling;
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

	public static final int COMPENSATION_SOULS = 50;

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
		pitPlayer.darkzoneData.altarXP = DarkzoneLeveling.getXPToLevel(prestigeInfo.darkzoneLevel);

		for(int i = 0; i < profile.getCachedInventory().length; i++) {
			ItemStack itemStack = profile.getCachedInventory()[i];

			if(!shouldConvert(itemStack)) continue;
			profile.getCachedInventory()[i] = convertItem(pitPlayer, itemStack);
		}

		for(int i = 0; i < profile.getArmor().length; i++) {
			ItemStack itemStack = profile.getArmor()[i];

			if(!shouldConvert(itemStack)) continue;
			profile.getArmor()[i] = convertItem(pitPlayer, itemStack);
		}

		for(Inventory page : profile.getEnderChest()) {
			for(int i = 0; i < page.getContents().length; i++) {
				ItemStack itemStack = page.getContents()[i];

				if(!shouldConvert(itemStack)) continue;
				page.getContents()[i] = convertItem(pitPlayer, itemStack);
			}
		}

		pitPlayer.save(true, true);
	}

	public static boolean shouldConvert(ItemStack itemStack) {
		if(Misc.isAirOrNull(itemStack)) return false;
		NBTItem nbtItem = new NBTItem(itemStack);
		PitItem pitItem = ItemFactory.getItem(itemStack);
		return pitItem != null && pitItem.isLegacyItem(itemStack, nbtItem);
	}

	public static ItemStack convertItem(PitPlayer pitPlayer, ItemStack itemStack) {
		PitItem pitItem = ItemFactory.getItem(itemStack);
		NBTItem nbtItem = new NBTItem(itemStack);
		assert pitItem != null;
		ItemStack replacementItem = pitItem.getReplacementItem(pitPlayer, itemStack, nbtItem);
		pitItem.updateItem(replacementItem);
		return replacementItem;
	}
}
