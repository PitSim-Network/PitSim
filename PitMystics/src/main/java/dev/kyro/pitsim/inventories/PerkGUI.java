package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PerkGUI extends AGUI {

	public PerkPanel perkPanel;
	public ApplyPerkPanel applyPerkPanel;
	public MegastreakPanel megastreakPanel;
	public KillstreakPanel killstreakPanel;

	public int killstreakSlot = 0;

	public PerkGUI(Player player) {
		super(player);

		perkPanel = new PerkPanel(this);
		setHomePanel(perkPanel);
		applyPerkPanel = new ApplyPerkPanel(this);
		megastreakPanel = new MegastreakPanel(this);
		killstreakPanel = new KillstreakPanel(this);
	}

	public void megaWrapUp() {

	}

	public int getSlot(int perkNum) {

		return perkNum * 2 + 8;
	}

	public int getPerkNum(int slot) {

		return (slot - 8) / 2;
	}

	public PitPerk getActivePerk(int perkNum) {

		return getActivePerks()[perkNum - 1];
	}

	public PitPerk[] getActivePerks() {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		return pitPlayer.pitPerks;
	}

	public void setPerk(PitPerk pitPerk, int perkNum) {
		if(NonManager.getNon(player) !=  null) return;
		FileConfiguration playerData = APlayerData.getPlayerData(player);
		playerData.set("perk-" + (perkNum - 1), pitPerk.refName);
		APlayerData.savePlayerData(player);

		getActivePerks()[perkNum - 1] = pitPerk;
	}

	public void saveKillstreak(Killstreak killstreak, int slotNum) {
		if(NonManager.getNon(player) !=  null) return;
		FileConfiguration playerData = APlayerData.getPlayerData(player);
		playerData.set("killstreak-" + (slotNum - 1), killstreak.refName);
		APlayerData.savePlayerData(player);
	}

	public boolean isActive(PitPerk pitPerk) {

		for(PitPerk activePerk : getActivePerks()) {

			if(activePerk == pitPerk) return true;
		}

		return false;
	}
}
