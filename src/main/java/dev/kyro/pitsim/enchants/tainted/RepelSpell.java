package dev.kyro.pitsim.enchants.tainted;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.PitPlayerAttemptAbilityEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import java.util.List;

public class RepelSpell extends PitEnchant {
	public RepelSpell() {
		super("Repel", true, ApplyType.SCYTHES, "repell", "rep", "repel");
		tainted = true;
	}

	@EventHandler
	public void onUse(PitPlayerAttemptAbilityEvent event) {
		int enchantLvl = event.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(event.getPlayer(), 10);
		if(cooldown.isOnCooldown()) return;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
		if(!pitPlayer.useMana(getManaCost(enchantLvl))) {
			Sounds.NO.play(event.getPlayer());
			return;
		}

		Sounds.REPEL.play(event.getPlayer().getLocation());
		cooldown.restart();

		Player player = event.getPlayer();
		for(Entity entity : player.getNearbyEntities(4, 4, 4)) {
			Vector dirVector = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
			Vector pullVector = dirVector.clone().normalize().setY(0.5).multiply(2.5).add(dirVector.clone().multiply(0.03));
			entity.setVelocity(pullVector);
		}

	}

	@Override
	public List<String> getDescription(int enchantLvl) {
		return new ALoreBuilder("&7Repel all nearby enemies from you", "&d&o-" + getManaCost(enchantLvl) + " Mana").getLore();
	}

	public static int getManaCost(int enchantLvl) {
		return 30 * (4 - enchantLvl);
	}
}
