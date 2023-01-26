package dev.kyro.pitsim.controllers.objects;

import net.luckperms.api.query.Flag;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class AnticheatManager implements Listener {
	public abstract void exemptPlayer(Player player, long ms, FlagType... flags);



	public enum FlagType {
		ALL(null),
		SIMULATION("simulation"),
		REACH("reach"),
		KNOCKBACK("antikb"),
		GROUND_SPOOF("groundspoof"),
		NO_FALL("nofall"),
		;

		public String refName;

		FlagType(String refName) {
			this.refName = refName;
		}

		public static FlagType getFlag(String refName) {
			for(FlagType flag : values()) if(flag != ALL && flag.refName.equalsIgnoreCase(refName)) return flag;
			return null;
		}
	}
}

