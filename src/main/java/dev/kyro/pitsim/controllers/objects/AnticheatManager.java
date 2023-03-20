package dev.kyro.pitsim.controllers.objects;

import net.luckperms.api.query.Flag;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class AnticheatManager implements Listener {
	public abstract void exemptPlayer(Player player, long ms, FlagType... flags);



	public enum FlagType {
		ALL(null, null),
		SIMULATION("simulation", "MOVEMENT"),
		REACH("reach", "ATTACK"),
		KNOCKBACK("antikb", "VELOCTY"),
		GROUND_SPOOF("groundspoof", "MOVEMENT"),
		NO_FALL("nofall", "MOVEMENT");

		public String refName;
		public String polarName;

		FlagType(String refName, String polarName) {
			this.refName = refName;
			this.polarName = polarName;
		}

		public static FlagType getFlag(String refName) {
			for(FlagType flag : values()) if(flag != ALL && flag.refName.equalsIgnoreCase(refName)) return flag;
			return null;
		}

		public static FlagType getFlagPolar(String polarName) {
			for(FlagType flag : values()) if(flag != ALL && flag.polarName.equalsIgnoreCase(polarName)) return flag;
			return null;
		}

	}
}

