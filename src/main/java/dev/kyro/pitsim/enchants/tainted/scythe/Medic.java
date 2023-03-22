package dev.kyro.pitsim.enchants.tainted.scythe;

import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.PitPlayerAttemptAbilityEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Medic extends PitEnchant {
	public static Medic INSTANCE;

	public Medic() {
		super("Medic", true, ApplyType.SCYTHES,
				"medic");
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onUse(PitPlayerAttemptAbilityEvent event) {
		Player player = event.getPlayer();
		int enchantLvl = event.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(player, 4);
		if(cooldown.isOnCooldown()) {
			Sounds.NO.play(player);
			return;
		}
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.useManaForSpell(getManaCost(enchantLvl))) {
			Sounds.NO.play(player);
			return;
		}
		cooldown.restart();

		pitPlayer.heal(getHealing(enchantLvl));
		Sounds.MEDIC.play(player);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Right-Clicking casts this spell for &b" + getManaCost(enchantLvl) + " mana&7, healing &c" +
				Misc.getHearts(getHealing(enchantLvl))
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"heals you when used";
	}

	public static int getManaCost(int enchantLvl) {
		return 50;
	}

	public static int getHealing(int enchantLvl) {
		return enchantLvl * 4 + 8;
	}
}
