package net.pitsim.spigot.adarkzone.progression;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.misc.Misc;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProgressionGUI extends AGUI {
	public static ItemStack backItem;

	public PitPlayer pitPlayer;

	public MainProgressionPanel mainProgressionPanel;
	public List<SkillBranchPanel> skillBranchPanels = new ArrayList<>();

	static {
		backItem = new AItemStackBuilder(Material.BARRIER)
				.setName("&c&lBack")
				.setLore(new ALoreBuilder(
						"&7Click to go to the previous screen"
				))
				.getItemStack();
	}

	public ProgressionGUI(Player player) {
		super(player);
		this.pitPlayer = PitPlayer.getPitPlayer(player);

		this.mainProgressionPanel = new MainProgressionPanel(this);

		for(SkillBranch skillBranch : ProgressionManager.skillBranches) {
			SkillBranchPanel panel = new SkillBranchPanel(this, skillBranch);
			skillBranchPanels.add(panel);
		}

		setHomePanel(mainProgressionPanel);
	}

	public ItemStack createSoulsDisplay() {
		DecimalFormat decimalFormat = new DecimalFormat("#,##0");
		return new AItemStackBuilder(Material.GHAST_TEAR)
				.setName("&f&lSouls")
				.setLore(new ALoreBuilder(
						"&7You have &f" + decimalFormat.format(pitPlayer.taintedSouls) + " Soul" + Misc.s(pitPlayer.taintedSouls)
				)).getItemStack();
	}
}
