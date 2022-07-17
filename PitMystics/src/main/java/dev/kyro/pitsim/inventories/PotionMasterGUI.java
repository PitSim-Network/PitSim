package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class PotionMasterGUI extends AGUI {
    public PotionMasterPanel potionMasterPanel;

    public PotionMasterGUI(Player player) {
        super(player);
        potionMasterPanel = new PotionMasterPanel(this);

        setHomePanel(potionMasterPanel);
    }
}
