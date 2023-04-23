package dev.kyro.pitsim.enchants.tainted.scythe;

import dev.kyro.pitsim.controllers.objects.PitEnchantSpell;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.SpellUseEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Medic extends PitEnchantSpell {
	public static Medic INSTANCE;

	public Medic() {
		super("Medic",
				"medic");
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onUse(SpellUseEvent event) {
		if(!isThisSpell(event.getSpell())) return;
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		pitPlayer.heal(getHealing(event.getSpellLevel()));
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

	@Override
	public int getManaCost(int enchantLvl) {
		return 45;
	}

	@Override
	public int getCooldownTicks(int enchantLvl) {
		return 4;
	}

	public static int getHealing(int enchantLvl) {
		return enchantLvl * 4 + 8;
	}
}
