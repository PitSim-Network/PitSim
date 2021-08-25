package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.RenownUpgrade;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RenownShopGUI extends AGUI {

	public RenownShopPanel renownShopPanel;
	public RenownShopConfirmPanel renownShopConfirmPanel;
	public static Map<Player, RenownUpgrade> purchaseConfirmations = new HashMap<>();

	public RenownShopGUI(Player player) {
		super(player);

		renownShopPanel = new RenownShopPanel(this);
		renownShopConfirmPanel = new RenownShopConfirmPanel(this);
		setHomePanel(renownShopPanel);
	}

}
