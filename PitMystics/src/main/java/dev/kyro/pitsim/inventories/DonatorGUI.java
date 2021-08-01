package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class DonatorGUI extends AGUI {

        public DonatorPanel donatorPanel;
        public KillEffectPanel killEffectPanel;
        public PantsColorPanel pantsColorPanel;
        public DeathCryPanel deathCryPanel;
        public ChatColorPanel chatColorPanel;
        public ChatOptionsPanel chatOptionsPanel;

        public DonatorGUI(Player player) {
                super(player);

                donatorPanel = new DonatorPanel(this);
                setHomePanel(donatorPanel);
                killEffectPanel = new KillEffectPanel(this);
                pantsColorPanel = new PantsColorPanel(this);
                deathCryPanel = new DeathCryPanel(this);
                chatColorPanel = new ChatColorPanel(this);
                chatOptionsPanel = new ChatOptionsPanel(this);
        }
}
