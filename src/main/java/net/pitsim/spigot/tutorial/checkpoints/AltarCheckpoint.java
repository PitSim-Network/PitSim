package net.pitsim.spigot.tutorial.checkpoints;

import net.pitsim.spigot.darkzone.altar.AltarPedestal;
import net.pitsim.spigot.controllers.MapManager;
import net.pitsim.spigot.tutorial.Midpoint;
import net.pitsim.spigot.tutorial.NPCCheckpoint;
import net.pitsim.spigot.tutorial.Tutorial;
import net.pitsim.spigot.tutorial.TutorialObjective;
import org.bukkit.Location;

public class AltarCheckpoint extends NPCCheckpoint {
	public AltarCheckpoint() {
		super(TutorialObjective.ALTAR, new Location(MapManager.getDarkzone(),
				218.5, 91, -88.5, 13, 0), Midpoint.SPAWN1);
	}

	@Override
	public void onCheckpointEngage(Tutorial tutorial) {
		tutorial.sendMessage("&eHere we have the &4&lTainted Altar&e!", 0);
		tutorial.sendMessage("&eAs you may have seen, the &4&lAltar &eslowly steals your &aOverworld &6Gold &eand &bXP&e.", 60);
		tutorial.sendMessage("&eTo regain your resources, sacrifice &fSouls &eto it.", 120);
		tutorial.sendMessage("&eBy doing this, you may also gain rewards such as &4Demonic Vouchers &eand Renown.", 180);
		tutorial.sendMessage("&eRight-click the &3Portal-Frame &eto open the &4&lAltar &eand sacrifice some &fSouls&e.", 240);
		tutorial.sendMessage("&eCome speak to me once you've done so.", 300);
	}

	@Override
	public void onCheckpointSatisfy(Tutorial tutorial) {
		tutorial.delayTask(() -> {
			for(AltarPedestal.AltarReward value : AltarPedestal.AltarReward.values()) {
				value.storedTemporaryReward.remove(tutorial.getPlayer().getUniqueId());
			}
		}, getSatisfyDelay());

		tutorial.sendMessage("&eGreat Job!", 0);
		tutorial.sendMessage("&eYou can unlock &3Altar Pedestals &elater to gain more &4Altar XP&e.", 60);
		tutorial.sendMessage("&eYou will likely need to revisit the &4&lAltar &eevery time you prestige.", 120);
	}

	@Override
	public int getEngageDelay() {
		return 300;
	}

	@Override
	public int getSatisfyDelay() {
		return 120;
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
