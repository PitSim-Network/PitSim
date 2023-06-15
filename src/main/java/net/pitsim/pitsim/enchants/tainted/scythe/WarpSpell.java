package net.pitsim.pitsim.enchants.tainted.scythe;

import net.pitsim.pitsim.controllers.objects.PitEnchantSpell;
import net.pitsim.pitsim.events.SpellUseEvent;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import net.pitsim.pitsim.misc.Sounds;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;

public class WarpSpell extends PitEnchantSpell {
	public WarpSpell() {
		super("Warp",
				"warp", "teleport", "tp");
		isTainted = true;
	}

	@EventHandler
	public void onUse(SpellUseEvent event) {
		if(!isThisSpell(event.getSpell())) return;
		Player player = event.getPlayer();

		Block lookBlock = player.getTargetBlock((Set<Material>) null, 15);
		Location teleportLoc = lookBlock.getLocation();

		for(Entity entity : player.getNearbyEntities(15, 15, 15)) {
			Vector direction = player.getLocation().getDirection();
			Vector towardsEntity = entity.getLocation().subtract(player.getLocation()).toVector().normalize();
			if(direction.distance(towardsEntity) >= 0.3) continue;
			teleportLoc = entity.getLocation();
			break;
		}

		Block teleportBlock = teleportLoc.getBlock();
		if(teleportBlock.getType() != Material.AIR && teleportBlock.getRelative(0, 1, 0).getType() == Material.AIR) {
			teleportLoc.add(0, 1, 0);
			teleportBlock = teleportLoc.getBlock();
		}
		if(teleportBlock.getType() != Material.AIR) {
			event.setCancelled(true);
			return;
		}

		teleportLoc.setPitch(player.getLocation().getPitch());
		teleportLoc.setYaw(player.getLocation().getYaw());

		player.teleport(teleportLoc.add(0.5, 0, 0.5));
		Misc.applyPotionEffect(player, PotionEffectType.SPEED, 40, 3, false, false);
		player.getWorld().playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 1);
		Sounds.WARP.play(player);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Right-Clicking casts this spell for &b" + getManaCost(enchantLvl) + " mana&7, " +
						"&7teleporting you forward and granting &eSpeed IV &7(2s)"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"teleports you in the direction you are looking when used";
	}

	@Override
	public int getManaCost(int enchantLvl) {
		return Math.max(75 - enchantLvl * 15, 0);
	}

	@Override
	public int getCooldownTicks(int enchantLvl) {
		return 4;
	}
}
