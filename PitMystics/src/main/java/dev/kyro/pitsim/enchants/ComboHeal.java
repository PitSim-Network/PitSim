package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.event.EventHandler;

import java.util.List;

public class ComboHeal extends PitEnchant {

	public ComboHeal() {
		super("Combo: Heal", false, ApplyType.SWORDS,
				"comboheal", "ch", "combo-heal", "cheal");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;
		PitPlayer pitAttacker = PitPlayer.getPitPlayer(attackEvent.attacker);

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.attacker);
		HitCounter.incrementCounter(pitPlayer.player, this);
		if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, 4)) return;

		pitAttacker.heal(getEffect(enchantLvl));
		EntityPlayer nmsPlayer = ((CraftPlayer) attackEvent.attacker).getHandle();
		if(nmsPlayer.getAbsorptionHearts() < 8) {
			nmsPlayer.setAbsorptionHearts(Math.min((float) (nmsPlayer.getAbsorptionHearts() + getEffect(enchantLvl)), 8));
		}

		ASound.play(attackEvent.attacker, Sound.DONKEY_HIT, 1F, 0.5F);;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Every &efourth &7strike heals",
				"&c" + Misc.getHearts(getEffect(enchantLvl)) + " &7and grants &6" + Misc.getHearts(getEffect(enchantLvl))).getLore();
	}

	public double getEffect(int enchantLvl) {

		return enchantLvl * 0.6;
	}
}
