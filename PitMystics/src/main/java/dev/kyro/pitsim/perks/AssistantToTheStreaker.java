package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AssistantToTheStreaker extends PitPerk {

	public static AssistantToTheStreaker INSTANCE;

	public AssistantToTheStreaker() {
		super("Assistant Streaker", "atts", new ItemStack(Material.SPRUCE_FENCE, 1, (short) 0), 15, false, "", INSTANCE);
		INSTANCE = this;
	}


	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&7Assists count their", "&aparticipation &7towards", "&7killstreaks.").getLore();
	}
}
