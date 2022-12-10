package dev.kyro.pitsim.storage;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.pitsim.PitSim;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class EnderchestGUI extends AGUI {

	public EnderchestPanel panel;

	public EnderchestGUI(Player player) {
		super(player);

		this.panel = new EnderchestPanel(this);
		setHomePanel(panel);
	}

	public enum EnderchestPages {
		MEMBER(1, "galacticvaults.limit.1"),
		LEGENDARY(3, "galacticvaults.limit.3"),
		OVERPOWERED(5, "galacticvaults.limit.5"),
		EXTRAORDINARY(7, "galacticvaults.limit.7"),
		MIRACULOUS(10, "galacticvaults.limit.10"),
		UNTHINKABLE(14, "galacticvaults.limit.14"),
		ETERNAL(18, "galacticvaults.limit.18");

		public final int pages;
		public final String permission;
		EnderchestPages(int pages, String permission) {
			this.pages = pages;
			this.permission = permission;
		}

		public static EnderchestPages getRank(Player player) {

			List<EnderchestPages> ranks = new ArrayList<>(Arrays.asList(values()));
			Collections.reverse(ranks);

			for(EnderchestPages value : ranks) {
				if(player.hasPermission(value.permission)) {
					return value;
				}
			}
			return MEMBER;
		}
	}
}
