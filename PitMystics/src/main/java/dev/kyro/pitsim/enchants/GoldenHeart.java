package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;

import java.util.List;

public class GoldenHeart extends PitEnchant {

	public GoldenHeart() {
		super("Combo: Heal", false, ApplyType.SWORDS,
				"comboheal", "ch", "combo-heal", "cheal");
	}

	@EventHandler
	public void onAttack(KillEvent killEvent) {

		int enchantLvl = killEvent.attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		EntityPlayer nmsPlayer = ((CraftPlayer) killEvent.attacker).getHandle();
		if(nmsPlayer.getAbsorptionHearts() < 6) nmsPlayer.setAbsorptionHearts(Math.min((float) (nmsPlayer.getAbsorptionHearts() + getEffect(enchantLvl)), 6));
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7gain &6+" + Misc.getHearts(getEffect(enchantLvl)) + " &7absorption on kill",
				"&7(max &7" + Misc.getHearts(6) + "&7)").getLore();
	}

	public double getEffect(int enchantLvl) {

		return Math.floor(Math.pow(enchantLvl, 1.4));
	}
}
