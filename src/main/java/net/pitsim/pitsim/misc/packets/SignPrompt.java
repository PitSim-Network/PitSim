package net.pitsim.pitsim.misc.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import net.pitsim.pitsim.PitSim;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SignPrompt {
	public static Map<Player, SignCallback> callbackMap = new HashMap<>();

	public static void registerSignUpdateListener() {
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();

		manager.addPacketListener(new PacketAdapter(PitSim.INSTANCE, PacketType.Play.Client.UPDATE_SIGN) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				Player player = event.getPlayer();
				WrapperPlayClientUpdateSign packet = new WrapperPlayClientUpdateSign(event.getPacket());
				if(!callbackMap.containsKey(player)) return;

				String input = new TextComponent(packet.getLines()[0].getJson()).getText();
				SignCallback callback = callbackMap.remove(player);
				callback.run(input);
			}
		});
	}

	public static void promptPlayer(Player player, String line1, String line2, String line3, String line4, SignCallback callback) {
		BlockPosition position = new BlockPosition(player.getLocation().getBlockX(), 0, player.getLocation().getBlockZ());

		NbtCompound nbt = NbtFactory.ofCompound("SignData");
		nbt.put("Text1", ComponentSerializer.toString(TextComponent.fromLegacyText(line1)));
		nbt.put("Text2", ComponentSerializer.toString(TextComponent.fromLegacyText(line2)));
		nbt.put("Text3", ComponentSerializer.toString(TextComponent.fromLegacyText(line3)));
		nbt.put("Text4", ComponentSerializer.toString(TextComponent.fromLegacyText(line4)));
		nbt.put("id", "minecraft:sign");
		nbt.put("x", position.getX());
		nbt.put("y", position.getY());
		nbt.put("z", position.getZ());

		WrapperPlayServerBlockChange block = new WrapperPlayServerBlockChange();
		block.setLocation(position);
		block.setBlockData(WrappedBlockData.createData(Material.WALL_SIGN));
		block.sendPacket(player);

		WrapperPlayServerUpdateSign text = new WrapperPlayServerUpdateSign();
		text.setLocation(position);
		text.setLines(new WrappedChatComponent[] {
				WrappedChatComponent.fromLegacyText(line1),
				WrappedChatComponent.fromLegacyText(line2),
				WrappedChatComponent.fromLegacyText(line3),
				WrappedChatComponent.fromLegacyText(line4),
		});
		text.sendPacket(player);

		WrapperPlayServerOpenSignEditor editor = new WrapperPlayServerOpenSignEditor();
		editor.setLocation(position);
		editor.sendPacket(player);

		callbackMap.put(player, callback);
	}
}