package dev.kyro.pitsim.adarkzone.progression;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ProgressionGUI extends AGUI {
	public static ItemStack backItem;

	public PitPlayer pitPlayer;

	/*
	Branches:
	damage - increased dmg vs mobs, increased dmg vs bosses
	first: unlock bosses
	last:
	path 1:
	path 2: -1 item required to spawn bosses

	defense - decreased damage from mobs & bosses, increased shield
	first: unlock shield
	last:
	path 1:
	path 2: decrease time until shield resets

	enchanting/souls - increased souls from mobs, increased fresh drop?
	first: unlock the tainted well
	last:
	path 1:
	path 2:

	mana - increased max mana, increased mana regen
	first: unlock mana
	last:
	path 1:
	path 2:

	potions - decreased brew time, increased brewing "luck"
	first: unlock the brewing system
	last: unlock receiving that item as a drop that directly just buffs up a potion
	path 1: unlock catalyst crafting
	path 2: unlock crafting of ingredient that increases potency or smth
	*/

	public MainProgressionPanel mainProgressionPanel;
	public List<ProgressionPanel> progressionPanels = new ArrayList<>();

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
			ProgressionPanel panel = new ProgressionPanel(this, skillBranch);
			progressionPanels.add(panel);
		}

		setHomePanel(mainProgressionPanel);
	}
}
