package dev.kyro.pitsim.enchants.tainted.znotcodedrare;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurpleThumb extends PitEnchant {
	public static PurpleThumb INSTANCE;
	public static Map<Player, List<Flower>> flowerMap = new HashMap<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					int enchantLvl = EnchantManager.getEnchantLevel(player, INSTANCE);
					if(enchantLvl == 0) {
						if(flowerMap.containsKey(player)) {
							List<Flower> flowers = flowerMap.get(player);
							for(Flower flower : flowers) flower.remove();
						}
						continue;
					}

					if(!)
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	public PurpleThumb() {
		super("Purple Thumb", true, ApplyType.CHESTPLATES,
				"purplethumb", "thumb");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7I can't be asked to code this"
		).getLore();
	}

	private static class Flower {
		public Player player;
		public FlowerType flowertype;
		public Block block;

		public Flower(Player player, FlowerType flowertype, Block block) {
			this.player = player;
			this.flowertype = flowertype;
			this.block = block;
		}

		public void drawParticles() {

		}

		public void remove() {

		}
	}

	private enum FlowerType {

	}
}
