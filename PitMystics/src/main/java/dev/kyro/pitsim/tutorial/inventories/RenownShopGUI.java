package dev.kyro.pitsim.tutorial.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import dev.kyro.pitsim.inventories.HelmetryPanel;
import dev.kyro.pitsim.inventories.ItemClearPanel;
import dev.kyro.pitsim.inventories.ShardHunterPanel;
import dev.kyro.pitsim.inventories.WithercraftPanel;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RenownShopGUI extends AGUI {

	public RenownShopPanel renownShopPanel;
	public RenownShopConfirmPanel renownShopConfirmPanel;
	public static ShardHunterPanel shardHunterPanel;
	public static WithercraftPanel withercraftPanel;
	public static ItemClearPanel itemClearPanel;
	public static HelmetryPanel helmetryPanel;
	public static Map<Player, RenownUpgrade> purchaseConfirmations = new HashMap<>();

	public RenownShopGUI(Player player) {
		super(player);

		renownShopPanel = new RenownShopPanel(this);
		renownShopConfirmPanel = new RenownShopConfirmPanel(this);
		shardHunterPanel = new ShardHunterPanel(this);
		withercraftPanel = new WithercraftPanel(this);
		itemClearPanel = new ItemClearPanel(this);
		helmetryPanel = new HelmetryPanel(this);
		setHomePanel(renownShopPanel);
	}

}
