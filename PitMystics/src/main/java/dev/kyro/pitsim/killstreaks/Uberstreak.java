package dev.kyro.pitsim.killstreaks;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.ItemManager;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.ChunkOfVile;
import dev.kyro.pitsim.misc.FunkyFeather;
import dev.kyro.pitsim.misc.ProtArmor;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Uberstreak extends Megastreak {
	MysticType mysticType;

	public Uberstreak(PitPlayer pitPlayer) {
		super(pitPlayer);
	}

	@Override
	public String getName() {
		return "&d&lUBER";
	}

	@Override
	public String getRawName() {
		return "Uberstreak";
	}

	@Override
	public String getPrefix() {
		return "&dUberstreak";
	}

	@Override
	public List<String> getRefNames() {
		return Arrays.asList("uberstreak");
	}

	@Override
	public int guiSlot() {
		return 14;
	}

	@Override
	public int levelReq() {
		return 25;
	}

	@Override
	public int getRequiredKills() {
		return 100;
	}

	@Override
	public ItemStack guiItem() {
		ItemStack item = new ItemStack(Material.GOLD_SWORD);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Triggers on: &c100 kills"));
		lore.add("");
		lore.add(ChatColor.GRAY + "On trigger:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Immune to enchants that &emove &7you."));
		lore.add("");
		lore.add(ChatColor.GRAY + "BUT:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&c\u25a0 &7Receive &c+10% &7damage per 50 kills."));
		lore.add("");
		lore.add(ChatColor.GRAY + "During the streak:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&d\u25a0 &7100 kills: &c-1 max \u2764"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&d\u25a0 &7200 kills: &c-1 max \u2764 &7(2 total)"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&d\u25a0 &7300 kills: &c-1 max \u2764 &7(3 total)"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&d\u25a0 &7400 kills: &cNo longer gain health."));
		lore.add("");
		lore.add(ChatColor.GRAY + "On death:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&e\u25a0 &7Earn a random &dUberdrop&7."));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7(If streak is at least 400)"));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	@EventHandler
	public void onHit(AttackEvent.Apply attackEvent) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.defender);
		if(pitPlayer != this.pitPlayer) return;
		if(pitPlayer.megastreak.isOnMega() && pitPlayer.megastreak.getClass() == Uberstreak.class) {
			double ks = pitPlayer.getKills();
			attackEvent.increasePercent += (ks / 5)  / 100D;
		}
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(killEvent.killer);
		if(pitPlayer != this.pitPlayer) return;
		if(pitPlayer.megastreak.isOnMega() && pitPlayer.megastreak.getClass() == Uberstreak.class) {
			double ks = pitPlayer.getKills();
			if(ks >= 199 && ks < 200) {
				pitPlayer.player.playSound(pitPlayer.player.getLocation(), "mob.guardian.curse", 1000, 1);
				pitPlayer.updateMaxHealth();
				AOutput.send(pitPlayer.player, "&d&lUBERSTREAK &c-1 max \u2764");
			}
			if(ks >= 299 && ks < 300) {
				pitPlayer.player.playSound(pitPlayer.player.getLocation(), "mob.guardian.curse", 1000, 1);
				pitPlayer.updateMaxHealth();
				AOutput.send(pitPlayer.player, "&d&lUBERSTREAK &c-1 max \u2764");
			}
			if(ks >= 399 && ks <  400) {
				pitPlayer.player.playSound(pitPlayer.player.getLocation(), "mob.guardian.curse", 1000, 1);
				pitPlayer.updateMaxHealth();
				AOutput.send(pitPlayer.player, "&d&lUBERSTREAK &cCannot heal");
			}
		}

	}

//	@EventHandler
//	public void onHeal(EntityRegainHealthEvent event) {
//		if(!(event.getEntity() instanceof Player)) return;
//		PitPlayer pitPlayer = PitPlayer.getPitPlayer((Player) event.getEntity());
//		if(pitPlayer != this.pitPlayer) return;
//		if(pitPlayer.megastreak.isOnMega() && pitPlayer.megastreak.getClass() == Uberstreak.class) {
//			if(pitPlayer.getKills() >= 400) {
//				event.setCancelled(true);
//			}
//		}
//	}

//	@EventHandler
//	public void onHeal(HealEvent healEvent) {
//		if(!isOnMega()) return;
//
//		healEvent.multipliers.add(0D);
//	}
//
//	@EventHandler
//	public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
//		if(!isOnMega()) return;
//		if(event.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED &&
//				event.getRegainReason() != EntityRegainHealthEvent.RegainReason.REGEN &&
//				event.getRegainReason() != EntityRegainHealthEvent.RegainReason.MAGIC_REGEN) return;
//		event.setCancelled(true);
//	}

	@Override
	public void proc() {
		String message = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		} else {
			pitPlayer.prefix = "&7[&e" + pitPlayer.playerLevel + "&7] &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		}

		pitPlayer.player.playSound(pitPlayer.player.getLocation(), "mob.guardian.curse", 1000, 1);
		for(Player player : Bukkit.getOnlinePlayers()) {
			PitPlayer pitPlayer2 = PitPlayer.getPitPlayer(player);
			if(pitPlayer2.disabledStreaks) continue;
			String message2 = ChatColor.translateAlternateColorCodes('&',
					"&c&lMEGASTREAK!&7 %luckperms_prefix%" + pitPlayer.player.getDisplayName() + "&7 activated &d&lUBERSTREAK&7!");

			player.sendMessage(PlaceholderAPI.setPlaceholders(pitPlayer.player, message2));
		}
		pitPlayer.updateMaxHealth();
		AOutput.send(pitPlayer.player, "&d&lUBERSTREAK &c-1 max \u2764");
	}

	@Override
	public void reset() {
		pitPlayer.updateMaxHealth();

		if(pitPlayer.getKills() < 400)  return;
		if(!isOnMega()) return;

		String message = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		} else {
			pitPlayer.prefix = "&7[&e" + pitPlayer.playerLevel + "&7] &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		}

		int rand = (int) (Math.random() * 4);
		if(rand == 0) {

			int rand2 = (int) (Math.random() * 3);
			if(rand2 == 0) mysticType = MysticType.SWORD;
			else if(rand2 == 1) mysticType = MysticType.BOW;
			else if(rand2 == 2) mysticType = MysticType.PANTS;

			ItemStack jewel = FreshCommand.getFreshItem(mysticType, PantColor.JEWEL);
			jewel = ItemManager.enableDropConfirm(jewel);
			assert jewel != null;
			NBTItem nbtItem = new NBTItem(jewel);
			nbtItem.setBoolean(NBTTag.IS_JEWEL.getRef(), true);

			EnchantManager.setItemLore(nbtItem.getItem());

			AUtil.giveItemSafely(pitPlayer.player, nbtItem.getItem());
			uberMessage("&3Hidden Jewel " + mysticType.displayName, pitPlayer);

		} else if(rand == 1) {
			int rand2 = (int) (Math.random() * 3);
			if(rand2 == 0) {
				FunkyFeather.giveFeather(pitPlayer.player, 1);
				uberMessage("&3Funky Feather", pitPlayer);
			} else if(rand2 == 1) {
				FunkyFeather.giveFeather(pitPlayer.player, 2);
				uberMessage("&32x Funky Feather", pitPlayer);
			}
			else if(rand2 == 2) {
				FunkyFeather.giveFeather(pitPlayer.player, 3);
				uberMessage("&33x Funky Feather", pitPlayer);
			}
		} else if(rand == 2) {
			int rand2 = (int) (Math.random() * 3);
			if(rand2 == 0) {
				ChunkOfVile.giveVile(pitPlayer.player, 2);
				uberMessage("&52x Chunk of Vile", pitPlayer);
			}
			else if(rand2 == 1) {
				ChunkOfVile.giveVile(pitPlayer.player, 4);
				uberMessage("&54x Chunk of Vile", pitPlayer);
			}
			else if(rand2 == 2) {
				ChunkOfVile.giveVile(pitPlayer.player, 6);
				uberMessage("&56x Chunk of Vile", pitPlayer);
			}
		} else if(rand == 3) {
			int rand2 = (int) (Math.random() * 4);
			if(rand2 == 0) {
				ProtArmor.getArmor(pitPlayer.player, "helmet");
				uberMessage("&bProtection I Diamond Helmet", pitPlayer);
			}
			else if(rand2 == 1) {
				ProtArmor.getArmor(pitPlayer.player, "chestplate");
				uberMessage("&bProtection I Diamond Chestplate", pitPlayer);
			}
			else if(rand2 == 2) {
				ProtArmor.getArmor(pitPlayer.player, "leggings");
				uberMessage("&bProtection I Diamond Leggings", pitPlayer);
			}
			else if(rand2 == 3) {
				ProtArmor.getArmor(pitPlayer.player, "boots");
				uberMessage("&bProtection I Diamond Boots", pitPlayer);
			}
		}



//		int rand = (int) (Math.random() * 4);
//		switch(rand) {
//			case 0:
//				int rand2 = (int) (Math.random() * 3);
//				switch(rand2) {
//					case 0:
//						mysticType = MysticType.SWORD;
//						break;
//					case 1:
//						mysticType = MysticType.BOW;
//						break;
//					default:
//						mysticType = MysticType.PANTS;
//						break;
//				}
//				mysticType = MysticType.SWORD;
//				break;
//			case 1:
//				mysticType = MysticType.BOW;
//				break;
//			default:
//				mysticType = MysticType.PANTS;
//				break;
		}

		public static void uberMessage(String message, PitPlayer pitPlayer) {
			for(Player player : Bukkit.getOnlinePlayers()) {
				PitPlayer pitPlayer2 = PitPlayer.getPitPlayer(player);
				if(pitPlayer2.disabledStreaks) continue;
				String message2 = ChatColor.translateAlternateColorCodes('&',
						"&d&lUBERDROP!&7 %luckperms_prefix%" + pitPlayer.player.getDisplayName() + "&7 obtained an &dUberdrop: &7" + message);
				player.sendMessage(PlaceholderAPI.setPlaceholders(pitPlayer.player, message2));
			}
		}



//	}
	public static void jew(MysticType mysticType) {

	}

	@Override
	public void stop() {
		HandlerList.unregisterAll(this);
	}

	@Override
	public void kill() {

		if(!isOnMega()) return;
	}
}
