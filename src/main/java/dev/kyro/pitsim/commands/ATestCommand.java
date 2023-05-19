package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.adarkzone.DarkzoneManager;
import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.misc.CustomPitEnderman;
import dev.kyro.pitsim.misc.EntityManager;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ATestCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		EntityManager.registerEntity("PitEnderman", 58, CustomPitEnderman.class);

		World nmsWorld = ((CraftWorld) player.getWorld()).getHandle();
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

		CustomPitEnderman enderman = new CustomPitEnderman(nmsWorld, DarkzoneManager.getSubLevel(SubLevelType.ENDERMAN));
		enderman.setLocation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 0, 0);
		nmsWorld.addEntity(enderman);

		return false;
	}
}










