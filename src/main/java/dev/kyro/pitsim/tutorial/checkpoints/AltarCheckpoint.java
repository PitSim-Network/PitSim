package dev.kyro.pitsim.tutorial.checkpoints;

import dev.kyro.pitsim.adarkzone.altar.AltarPedestal;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.tutorial.NPCCheckpoint;
import dev.kyro.pitsim.tutorial.Tutorial;
import dev.kyro.pitsim.tutorial.TutorialObjective;
import org.bukkit.Location;

public class AltarCheckpoint extends NPCCheckpoint {
	public AltarCheckpoint() {
		super(TutorialObjective.ALTAR, new Location(MapManager.getDarkzone(),
				218.5, 91, -88.5, 13, 0));
	}

	@Override
	public void onCheckpointEngage(Tutorial tutorial) {
		tutorial.sendMessage("&eHere we have the &4&lTainted Altar&e!", 0);
		tutorial.sendMessage("&eAs you may have seen, the &4&lAltar &eslowly steals your &aOverworld &6Gold &eand &bXP&e.", 40);
		tutorial.sendMessage("&eTo regain your resources, sacrifice &fSouls &eto it.", 80);
		tutorial.sendMessage("&eBy doing this, you may also gain rewards such as &4Demonic Vouchers &eand Renown.", 120);
		tutorial.sendMessage("&eRight-click the &3End-Frame &eto open the &4&lAltar &ea d sacrifice some &fSouls&e.", 160);
		tutorial.sendMessage("&eCome speak to me once you've done so.", 200);
	}

	@Override
	public void onCheckpointSatisfy(Tutorial tutorial) {
		tutorial.delayTask(() -> {
			for(AltarPedestal.AltarReward value : AltarPedestal.AltarReward.values()) {
				value.storedTemporaryReward.remove(tutorial.getPlayer().getUniqueId());
			}
		}, getSatisfyDelay());

		tutorial.sendMessage("&eGreat Job!", 0);
		tutorial.sendMessage("&eYou can unlock &3Altar Pedestals &elater to gain more &4Altar XP&e.", 40);
		tutorial.sendMessage("&eYou will likely need to revisit the &4&lAltar &eevery time you prestige.", 80);
	}

	@Override
	public int getEngageDelay() {
		return 200;
	}

	@Override
	public int getSatisfyDelay() {
		return 80;
	}

	@Override
	public boolean canEngage(Tutorial tutorial) {
		return true;
	}

	@Override
	public boolean canSatisfy(Tutorial tutorial) {
		return AltarPedestal.AltarReward.ALTAR_XP.storedTemporaryReward.containsKey(tutorial.getPlayer().getUniqueId());
	}

	@Override
	public void onCheckPointDisengage(Tutorial tutorial) {
		for(AltarPedestal.AltarReward value : AltarPedestal.AltarReward.values()) {
			value.restorePlayer(tutorial.getPlayer());
		}
	}
}
