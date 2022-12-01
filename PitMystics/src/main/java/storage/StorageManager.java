package storage;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StorageManager {
	private static final List<StorageProfile> profiles = new ArrayList<>();

	public static StorageProfile getProfile(Player player) {
		for(StorageProfile profile : profiles) {
			if(profile.getPlayer() == player) return profile;
		}

		StorageProfile profile = new StorageProfile(player);
		profiles.add(profile);

		return profile;
	}

}
