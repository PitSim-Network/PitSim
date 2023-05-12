package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.misc.CustomPitBat;
import dev.kyro.pitsim.misc.EntityManager;
import net.minecraft.server.v1_8_R3.*;
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

		EntityManager.registerEntity("PitBat", 65, CustomPitBat.class);

		World nmsWorld = ((CraftWorld) player.getWorld()).getHandle();
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

		CustomPitBat bat = new CustomPitBat(nmsWorld, nmsPlayer);
		bat.setLocation(player.getLocation().getX(), player.getLocation().getY() + 5, player.getLocation().getZ(), 0, 0);
		nmsWorld.addEntity(bat);

		MobEffect mobEffect = new MobEffect(14, Integer.MAX_VALUE, 0, false, false);
		bat.addEffect(mobEffect);


		EntityArmorStand armorStand = new EntityArmorStand(nmsWorld, player.getLocation().getX(), player.getLocation().getY() + 5, player.getLocation().getZ());
		armorStand.setArms(true);
		armorStand.setBasePlate(false);
		armorStand.setRightArmPose(new Vector3f(0, 90, 330));
		armorStand.setInvisible(true);

		PacketPlayOutSpawnEntityLiving armorStandSpawn = new PacketPlayOutSpawnEntityLiving(armorStand);
		nmsPlayer.playerConnection.sendPacket(armorStandSpawn);

		PacketPlayOutEntityEquipment standSword = new PacketPlayOutEntityEquipment(armorStand.getId(), 0, new ItemStack(Items.DIAMOND_SWORD));
		nmsPlayer.playerConnection.sendPacket(standSword);

		PacketPlayOutAttachEntity batAttach = new PacketPlayOutAttachEntity(0, armorStand, bat);
		nmsPlayer.playerConnection.sendPacket(batAttach);

		PacketPlayOutAttachEntity attachPacket = new PacketPlayOutAttachEntity(1, bat, nmsPlayer);
		nmsPlayer.playerConnection.sendPacket(attachPacket);

//		new BukkitRunnable() {
//			int yaw = 0;
//
//			@Override
//			public void run() {
//				PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(bat.getId(), (byte) 0, (byte) 0, (byte) 0, (byte) yaw, (byte) 0, false);
//				nmsPlayer.playerConnection.sendPacket(packet);
//
////				PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook armor = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(armorStand    .getId(), (byte) 0, (byte) 0, (byte) 0, (byte) yaw, (byte) 0, false);
////				nmsPlayer.playerConnection.sendPacket(armor);
//
//				yaw += 15;
//				if(yaw > 256) yaw = 0;
//			}
//		}.runTaskTimer(PitSim.INSTANCE, 1, 1);

		return false;
	}
}










