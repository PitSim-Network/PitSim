package dev.kyro.pitsim.cosmetics.capes;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.cosmetics.CosmeticType;
import dev.kyro.pitsim.cosmetics.PitCosmetic;
import dev.kyro.pitsim.cosmetics.collections.CapeCollection;
import dev.kyro.pitsim.cosmetics.particles.EnchantmentTableParticle;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class MagicCape extends PitCosmetic {
	public CapeCollection cape;

	public MagicCape() {
		super("&5Enchanted Cloak", "magiccape", CosmeticType.CAPE);
		accountForPitch = false;

		cape = new CapeCollection(new EnchantmentTableParticle(accountForPitch, accountForYaw));
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		runnableMap.put(pitPlayer.player.getUniqueId(), new BukkitRunnable() {
			@Override
			public void run() {
				cape.draw(MagicCape.this, pitPlayer, null);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 10L));
	}

	@Override
	public void onDisable(PitPlayer pitPlayer) {
		if(runnableMap.containsKey(pitPlayer.player.getUniqueId()))
			runnableMap.get(pitPlayer.player.getUniqueId()).cancel();
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.EXP_BOTTLE)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Show off your &5magical &7style",
						"&7with this mythical cape!"
				))
				.getItemStack();
		return itemStack;
	}
}
