package net.pitsim.spigot.battlepass.rewards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.cosmetics.particles.ParticleColor;
import net.pitsim.spigot.cosmetics.CosmeticManager;
import net.pitsim.spigot.cosmetics.PitCosmetic;
import net.pitsim.spigot.battlepass.PassReward;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PassCosmeticReward extends PassReward {
	public Material material;
	public int data;
	public PitCosmetic pitCosmetic;
	public ParticleColor particleColor;

	public PassCosmeticReward(Material material, PitCosmetic pitCosmetic, ParticleColor particleColor) {
		this(material, 0, pitCosmetic, particleColor);
	}

	public PassCosmeticReward(Material material, int data, PitCosmetic pitCosmetic, ParticleColor particleColor) {
		this.material = material;
		this.data = data;
		this.pitCosmetic = pitCosmetic;
		this.particleColor = particleColor;
	}

	@Override
	public boolean giveReward(PitPlayer pitPlayer) {
		PitPlayer.UnlockedCosmeticData cosmeticData = pitPlayer.unlockedCosmeticsMap.get(pitCosmetic.refName);
		if(cosmeticData != null) {
			if(!pitCosmetic.isColorCosmetic || cosmeticData.unlockedColors.contains(particleColor)) return true;
		}

		CosmeticManager.unlockCosmetic(pitPlayer, pitCosmetic, particleColor);
		if(particleColor != null) {
			AOutput.send(pitPlayer.player, "&e&lFANCY!&7 Unlocked " + pitCosmetic.getDisplayName() + "&7(" + particleColor.displayName + "&7)");
		} else {
			AOutput.send(pitPlayer.player, "&e&lFANCY!&7 Unlocked " + pitCosmetic.getDisplayName());
		}
		return true;
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, boolean hasClaimed) {
		ALoreBuilder loreBuilder = new ALoreBuilder("&7Reward: " + pitCosmetic.getDisplayName());
		if(particleColor != null) loreBuilder.addLore("&7Color: " + particleColor.displayName);
		ItemStack itemStack = new AItemStackBuilder(material, 1, data)
				.setName("&e&lCosmetic Reward")
				.setLore(loreBuilder).getItemStack();
		ItemMeta itemMeta = itemStack.getItemMeta();
		if(itemStack.getType() == Material.POTION) itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}
}
