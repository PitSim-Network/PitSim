package dev.kyro.pitsim.controllers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.injector.SortedPacketListenerList;
import com.comphenix.protocol.injector.packet.PacketInjector;
import com.comphenix.protocol.injector.player.PlayerInjectionHandler;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.misc.effects.PacketBlock;
import dev.kyro.pitsim.misc.packets.WrapperPlayClientBlockDig;
import dev.kyro.pitsim.misc.packets.WrapperPlayServerBlockChange;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.*;

public class PacketManager implements Listener {
	public static ProtocolManager protocolManager;

	public static Map<PacketBlock, List<Player>> suppressedLocations = new HashMap<>();

	static {
		protocolManager = ProtocolLibrary.getProtocolManager();
//		PacketFilterManager packetFilterManager = (PacketFilterManager) protocolManager; // This is the actual class

		Set<PacketListener> registeredListeners = (Set<PacketListener>) getFieldValue(protocolManager, "registeredListeners");
		for(PacketListener listener : new ArrayList<>(registeredListeners)) {
			if(listener.getPlugin() == Bukkit.getPluginManager().getPlugin("PremiumVanish") &&
					listener.getSendingWhitelist().getTypes().contains(PacketType.Play.Server.PLAYER_INFO) &&
					listener.getClass().getName().contains("SilentOpenChestPacketAdapters")) {
				removePacketListener(listener);
				System.out.println("Removed packet listener from " + listener.getPlugin().getName());
			}
		}

		protocolManager.addPacketListener(
				new PacketAdapter(PitSim.INSTANCE, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
					@Override
					public void onPacketSending(PacketEvent event) {
						Player player = event.getPlayer();
						String soundName = event.getPacket().getStrings().read(0);
						if(soundName.equals("mob.villager.idle") || soundName.equals("mob.rabbit.idle")) {
							event.setCancelled(true);
						}
						Location auctions = AuctionDisplays.pedestalLocations[0];
						if(soundName.equals("mob.magmacube.big") &&
								auctions.getWorld() == player.getWorld() && auctions.distance(player.getLocation()) < 50) {
							event.setCancelled(true);
						}
					}
				});


		if(PitSim.status.isDarkzone()) {
			protocolManager.addPacketListener(
					new PacketAdapter(PitSim.INSTANCE, PacketType.Play.Server.BLOCK_CHANGE) {
						@Override
						public void onPacketSending(PacketEvent event) {

							WrapperPlayServerBlockChange wrapper = new WrapperPlayServerBlockChange(event.getPacket());
							Location location = wrapper.getLocation().toLocation(MapManager.getDarkzone());
							List<Player> viewers = null;

							for(Map.Entry<PacketBlock, List<Player>> entry : suppressedLocations.entrySet()) {
								Location stored = entry.getKey().getLocation();
								if(stored.getBlockX() == location.getBlockX() && stored.getBlockY() == location.getBlockY()
										&& stored.getBlockZ() == location.getBlockZ()) {
									viewers = entry.getValue();
								}
							}

							if(viewers == null) return;

							if(!viewers.contains(event.getPlayer())) return;

							event.setCancelled(true);
						}
					});
		}

		protocolManager.addPacketListener(
				new PacketAdapter(PitSim.INSTANCE, PacketType.Play.Client.BLOCK_DIG) {
					@Override
					public void onPacketReceiving(PacketEvent event) {
						WrapperPlayClientBlockDig wrapper = new WrapperPlayClientBlockDig(event.getPacket());
						Location location = wrapper.getLocation().toLocation(MapManager.getDarkzone());
						List<Player> viewers = null;
						PacketBlock packetBlock = null;

						for(Map.Entry<PacketBlock, List<Player>> entry : suppressedLocations.entrySet()) {
							Location stored = entry.getKey().getLocation();
							if(stored.getBlockX() == location.getBlockX() && stored.getBlockY() == location.getBlockY()
									&& stored.getBlockZ() == location.getBlockZ()) {
								viewers = entry.getValue();
								packetBlock = entry.getKey();
							}
						}

						if(viewers == null || packetBlock == null) return;

						if(!viewers.contains(event.getPlayer())) return;

						suppressedLocations.remove(packetBlock);
						PacketBlock finalPacketBlock = packetBlock;
						new BukkitRunnable() {
							@Override
							public void run() {
								finalPacketBlock.spawnBlock();
							}
						}.runTask(PitSim.INSTANCE);

					}
				});
	}

	public static void removePacketListener(PacketListener listener) {
		SortedPacketListenerList inboundListeners = (SortedPacketListenerList) getFieldValue(protocolManager, "inboundListeners");
		SortedPacketListenerList outboundListeners = (SortedPacketListenerList) getFieldValue(protocolManager, "outboundListeners");
		Set<PacketListener> registeredListeners = (Set<PacketListener>) getFieldValue(protocolManager, "registeredListeners");

		if(registeredListeners.remove(listener)) {
			ListeningWhitelist outbound = listener.getSendingWhitelist();
			ListeningWhitelist inbound = listener.getReceivingWhitelist();
			List removed;
			if(outbound != null && outbound.isEnabled()) {
				removed = outboundListeners.removeListener(listener, outbound);
				if(!removed.isEmpty()) {
					unregisterPacketListenerInInjectors(removed);
				}
			}

			if(inbound != null && inbound.isEnabled()) {
				removed = inboundListeners.removeListener(listener, inbound);
				if(!removed.isEmpty()) {
					unregisterPacketListenerInInjectors(removed);
				}
			}
		}
	}

	private static void unregisterPacketListenerInInjectors(Collection<PacketType> packetTypes) {
		PlayerInjectionHandler playerInjectionHandler = (PlayerInjectionHandler) getFieldValue(protocolManager, "playerInjectionHandler");
		PacketInjector packetInjector = (PacketInjector) getFieldValue(protocolManager, "packetInjector");

		for(PacketType packetType : packetTypes) {
			if(packetType.getSender() == PacketType.Sender.SERVER) {
				playerInjectionHandler.removePacketHandler(packetType);
			} else if(packetType.getSender() == PacketType.Sender.CLIENT) {
				packetInjector.removePacketHandler(packetType);
			}
		}
	}

	public static Object getFieldValue(Object object, String fieldName) {
		try {
			Field field;
			field = object.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(object);
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
	}
}
