package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RenownShopGUI extends AGUI {

	public RenownShopPanel renownShopPanel;
	public RenownShopConfirmPanel renownShopConfirmPanel;
	public static ShardHunterPanel shardHunterPanel;
	public static Map<Player, RenownUpgrade> purchaseConfirmations = new HashMap<>();

	public RenownShopGUI(Player player) {
		super(player);

		renownShopPanel = new RenownShopPanel(this);
		renownShopConfirmPanel = new RenownShopConfirmPanel(this);
		shardHunterPanel = new ShardHunterPanel(this);
		setHomePanel(renownShopPanel);
	}

}