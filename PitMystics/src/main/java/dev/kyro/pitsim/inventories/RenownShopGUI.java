package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import dev.kyro.pitsim.upgrades.Helmetry;
import dev.kyro.pitsim.upgrades.ShardHunter;
import dev.kyro.pitsim.upgrades.Withercraft;
import net.citizensnpcs.nms.v1_8_R3.entity.WitherController;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenownShopGUI extends AGUI {

	public RenownShopPanel renownShopPanel;
	public RenownShopConfirmPanel renownShopConfirmPanel;

	public List<AGUIPanel> subPanels = new ArrayList<>();

	public ItemClearPanel itemClearPanel;
	public static Map<Player, RenownUpgrade> purchaseConfirmations = new HashMap<>();

	public RenownShopGUI(Player player) {
		super(player);

		renownShopPanel = new RenownShopPanel(this);
		renownShopConfirmPanel = new RenownShopConfirmPanel(this);
		itemClearPanel = new ItemClearPanel(this);
		subPanels.add(new ShardHunterPanel(this));
		subPanels.add(new WithercraftPanel(this));
		subPanels.add(new HelmetryPanel(this));
		setHomePanel(renownShopPanel);
	}




	public AGUIPanel getSubPanel(RenownUpgrade upgrade) {
		if(upgrade instanceof ShardHunter) return subPanels.get(0);
		if(upgrade instanceof Withercraft) return subPanels.get(1);
		if(upgrade instanceof Helmetry) return subPanels.get(2);

		return null;
	}

}
