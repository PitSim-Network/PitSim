package storage;

import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.events.MessageEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StorageManager implements Listener {
	private static final List<StorageProfile> profiles = new ArrayList<>();

	public static StorageProfile getProfile(Player player) {
		for(StorageProfile profile : profiles) {
			if(profile.getPlayer() == player) return profile;
		}

		StorageProfile profile = new StorageProfile(player);
		profiles.add(profile);

		return profile;
	}

	@EventHandler
	public void onMessage(MessageEvent event) {
		PluginMessage message = event.getMessage();
		List<String> strings = message.getStrings();

		if(strings.get(0).equals("ENDERCHEST SAVE") || strings.get(0).equals("INVENTORY SAVE")) {
			UUID uuid = UUID.fromString(strings.get(1));
			Player player = Bukkit.getPlayer(uuid);
			if(player == null) return;

			StorageProfile profile = getProfile(player);
			if(!profile.hasData()) return;

			profile.receiveSaveConfirmation(message);
		}
	}


}
