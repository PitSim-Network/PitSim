package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.APagedGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.ChatTriggerManager;
import dev.kyro.pitsim.controllers.CombatManager;
import dev.kyro.pitsim.controllers.PerkManager;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.DisplayItemType;
import dev.kyro.pitsim.megastreaks.RNGesus;
import dev.kyro.pitsim.misc.Formatter;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class MegastreakPanel extends APagedGUIPanel {
	public PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	public PerkGUI perkGUI;

	public MegastreakPanel(AGUI gui) {
		super(gui);
		perkGUI = (PerkGUI) gui;
		addBackButton(getRows() * 9 - 5);
		buildInventory();
	}

	@Override
	public void addItems() {
		for(Megastreak megastreak : PerkManager.megastreaks) addItem(createSupplier(megastreak), createConsumer(megastreak));

//		ItemStack item = new ItemStack(megastreak.getBaseDisplayStack(player).getType());
//		if(megastreak instanceof RNGesus && onRNGCooldown) item.setType(Material.ENDER_PEARL);
//		if(megastreak instanceof Uberstreak && pitPlayer.prestige >= megastreak.getPrestigeReq()) {
//			Uberstreak.checkUberReset(pitPlayer);
//			int ubersLeft = pitPlayer.dailyUbersLeft;
//			if(ubersLeft == 0)
//				lore.add(ChatColor.translateAlternateColorCodes('&', "&dDaily Uberstreaks remaining: &c0&7/" + Uberstreak.getMaxUbers(player)));
//			else
//				lore.add(ChatColor.translateAlternateColorCodes('&', "&dDaily Uberstreaks remaining: &a" + ubersLeft + "&7/" + Uberstreak.getMaxUbers(player)));
//		}
//		if(megastreak instanceof RNGesus && onRNGCooldown) {
//			lore.add(ChatColor.YELLOW + "Megastreak on cooldown! " + ChatColor.GRAY + "(" + RNGesus.getTimeLeft(pitPlayer) + ")");
//		}
//		if(megastreak instanceof Uberstreak && pitPlayer.dailyUbersLeft == 0) {
//			lore.add(ChatColor.RED + "Daily limit reached!");
//			meta.setDisplayName(ChatColor.RED + megastreak.getRefName());
//		} else if(pitPlayer.level < megastreak.getLevelReq(player)) {
//			PrestigeValues.PrestigeInfo info = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);
//			lore.add(ChatColor.translateAlternateColorCodes('&', "&cUnlocked at level " + info.getOpenBracket() + PrestigeValues.getLevelColor(megastreak.getLevelReq(player)) + megastreak.getLevelReq(player) + info.getCloseBracket()));
//			meta.setDisplayName(ChatColor.RED + megastreak.getRefName());
//		} else if(megastreak instanceof RNGesus && pitPlayer.renown < 1 && onRNGCooldown) {
//			lore.add(ChatColor.RED + "Click to select for " + RNGesus.RENOWN_COST + " renown!");
//			meta.setDisplayName(ChatColor.RED + megastreak.getRefName());
//		} else if(megastreak instanceof RNGesus && pitPlayer.renown >= 1 && onRNGCooldown) {
//			lore.add(ChatColor.YELLOW + "Click to select for " + RNGesus.RENOWN_COST + " renown!");
//			meta.setDisplayName(ChatColor.YELLOW + megastreak.getRefName());
//		}
	}

	public Supplier<ItemStack> createSupplier(Megastreak megastreak) {
		return () -> megastreak.getDisplayStack(player, DisplayItemType.SELECT_PANEL);
	}

	public Consumer<InventoryClickEvent> createConsumer(Megastreak megastreak) {
		return event -> {
			if(!PerkManager.isUnlocked(player, megastreak)) {
				AOutput.error(player, "&c&lERROR!&7 You are not high enough prestige to equip this");
				Sounds.ERROR.play(player);
				return;
			}

			if(pitPlayer.level < megastreak.getLevelReq(player)) {
				AOutput.error(player, "&c&lERROR!&7 You are not high enough level to equip this");
				Sounds.ERROR.play(player);
				return;
			}

			if(CombatManager.isInCombat(player)) {
				AOutput.error(player, "&c&lERROR!&7 You cannot do this while in combat");
				Sounds.ERROR.play(player);
				return;
			}

			if(megastreak instanceof RNGesus && RNGesus.isOnCooldown(pitPlayer)) {
				if(pitPlayer.renown >= RNGesus.RENOWN_COST) {
					pitPlayer.renown = pitPlayer.renown - RNGesus.RENOWN_COST;
					pitPlayer.rngCooldown = 0;
					pitPlayer.getMegastreak().reset(player);
					pitPlayer.setMegastreak(megastreak);
					openPanel(perkGUI.getHomePanel());

					AOutput.send(player, "&aEquipped &6RNGsus &afor " + Formatter.formatRenown(RNGesus.RENOWN_COST) + "!");
					Sounds.SUCCESS.play(player);
					ChatTriggerManager.sendPerksInfo(pitPlayer);
				} else {
					AOutput.error(player, "&cYou do not have enough renown!");
					Sounds.NO.play(player);
				}
				return;
			}

			if(PerkManager.isEquipped(player, megastreak)) {
				AOutput.error(perkGUI.player, "&c&lERROR!&7 This megastreak is already equipped");
				Sounds.ERROR.play(player);
				return;
			}

			megastreak.reset(player);
			pitPlayer.setMegastreak(megastreak);
			Sounds.SUCCESS.play(player);
			ChatTriggerManager.sendPerksInfo(pitPlayer);
			openPreviousGUI();
		};
	}

	@Override
	public String getName() {
		return "&7Choose a &cMegastreak";
	}

//	@Override
//	public void onClick(InventoryClickEvent event) {
//		boolean prestige = false;
//		boolean has = false;
//		boolean level = false;
//		boolean uberCd = false;
//
//		int slot = event.getSlot();
//		if(event.getClickedInventory().getHolder() == this) {
//
//			if(slot == 22) {
//				openPanel(perkGUI.getHomePanel());
//			}
//
//			for(Megastreak megastreak : PerkManager.megastreaks) {
//				if(megastreak.guiSlot() == slot) {
//					PitPlayer pitPlayer = PitPlayer.getPitPlayer(perkGUI.player);
//					pitPlayer.setKills(0);
//					if(megastreak instanceof NoMegastreak) {
//						if(pitPlayer.prestige < 0) prestige = true;
//						if(pitPlayer.level < 0) level = true;
//						if(pitPlayer.megastreak instanceof NoMegastreak) has = true;
//						if(!has && !prestige && !level) {
//							pitPlayer.megastreak.stop();
//							pitPlayer.megastreak = new NoMegastreak(pitPlayer);
//							perkGUI.megaWrapUp();
//						}
//					} else if(megastreak instanceof Overdrive) {
//						if(pitPlayer.prestige < 0) prestige = true;
//						if(pitPlayer.level < 0) level = true;
//						if(pitPlayer.megastreak instanceof Overdrive) has = true;
//						if(!has && !prestige && !level) {
//							pitPlayer.megastreak.stop();
//							pitPlayer.megastreak = new Overdrive(pitPlayer);
//							perkGUI.megaWrapUp();
//						}
//					} else if(megastreak instanceof Highlander) {
//						if(pitPlayer.prestige < megastreak.prestigeReq()) prestige = true;
//						if(pitPlayer.megastreak instanceof Highlander) has = true;
//						if(pitPlayer.level < 0) level = true;
//						if(pitPlayer.level < megastreak.getFinalLevelReq(player)) level = true;
//						if(!has && !prestige && !level) {
//							pitPlayer.megastreak.stop();
//							pitPlayer.megastreak = new Highlander(pitPlayer);
//							perkGUI.megaWrapUp();
//						}
//					} else if(megastreak instanceof Beastmode) {
//						if(pitPlayer.prestige < megastreak.prestigeReq()) prestige = true;
//						if(pitPlayer.megastreak instanceof Beastmode) has = true;
//						if(pitPlayer.level < megastreak.getFinalLevelReq(player)) level = true;
//						if(!has && !prestige && !level) {
//							pitPlayer.megastreak.stop();
//							pitPlayer.megastreak = new Beastmode(pitPlayer);
//							perkGUI.megaWrapUp();
//						}
//					} else if(megastreak instanceof Uberstreak) {
//						if(pitPlayer.prestige < megastreak.prestigeReq()) prestige = true;
//						if(pitPlayer.megastreak instanceof Uberstreak) has = true;
//						if(pitPlayer.level < megastreak.getFinalLevelReq(player)) level = true;
//						if(pitPlayer.dailyUbersLeft <= 0) uberCd = true;
//						if(!has && !prestige && !uberCd && !level) {
//							pitPlayer.megastreak.stop();
//							pitPlayer.megastreak = new Uberstreak(pitPlayer);
//							perkGUI.megaWrapUp();
//						}
//					} else if(megastreak instanceof ToTheMoon) {
//						if(pitPlayer.prestige < megastreak.prestigeReq()) prestige = true;
//						if(pitPlayer.megastreak instanceof ToTheMoon) has = true;
//						if(pitPlayer.level < megastreak.getFinalLevelReq(player)) level = true;
//						if(!has && !prestige && !uberCd && !level) {
//							pitPlayer.megastreak.stop();
//							pitPlayer.megastreak = new ToTheMoon(pitPlayer);
//							perkGUI.megaWrapUp();
//						}
//					} else if(megastreak instanceof RNGesus) {
//						if(pitPlayer.prestige < megastreak.prestigeReq()) prestige = true;
//						if(pitPlayer.megastreak instanceof RNGesus) has = true;
//						if(pitPlayer.level < megastreak.getFinalLevelReq(player)) level = true;
//						if(!has && !prestige && !uberCd && !level) {
//						}
//					}
//					if(megastreak instanceof RNGesus && !has && !prestige && !uberCd && !level) {
//						if(!RNGesus.isOnCooldown(pitPlayer)) {
//							pitPlayer.megastreak.stop();
//							pitPlayer.megastreak = new RNGesus(pitPlayer);
//							perkGUI.megaWrapUp();
//
//							openPanel(perkGUI.getHomePanel());
//							Sounds.SUCCESS.play(player);
//							ChatTriggerManager.sendPerksInfo(pitPlayer);
//						} else if(pitPlayer.renown >= RNGesus.RENOWN_COST) {
//							pitPlayer.renown = pitPlayer.renown - RNGesus.RENOWN_COST;
//							AOutput.send(player, "&aEquipped &6RNGsus &afor &e" + RNGesus.RENOWN_COST + " Renown!");
//							pitPlayer.rngCooldown = 0;
//							pitPlayer.megastreak.stop();
//							pitPlayer.megastreak = new RNGesus(pitPlayer);
//							perkGUI.megaWrapUp();
//
//							openPanel(perkGUI.getHomePanel());
//							Sounds.SUCCESS.play(player);
//							ChatTriggerManager.sendPerksInfo(pitPlayer);
//						} else {
//							AOutput.error(player, "&cYou do not have enough renown!");
//							Sounds.ERROR.play(player);
//						}
//					} else if(!prestige && !has && !uberCd && !level) {
//						openPanel(perkGUI.getHomePanel());
//						Sounds.SUCCESS.play(player);
//						ChatTriggerManager.sendPerksInfo(pitPlayer);
//					}
//					if(prestige) {
//						AOutput.error(player, "&cYou aren't high enough prestige to use this!");
//						Sounds.ERROR.play(player);
//					}
//					if(has) {
//						AOutput.error(player, "&cThat megastreak is already equipped");
//						Sounds.ERROR.play(player);
//					}
//					if(level) {
//						AOutput.error(player, "&cYou are not high enough level to use this megastreak!");
//						Sounds.ERROR.play(player);
//					}
//					if(uberCd) {
//						AOutput.error(player, "&cYou have reached the daily limit for this megastreak");
//						Sounds.ERROR.play(player);
//					}
//				}
//			}
//		}
//	}


	@Override
	public void setInventory() {
		super.setInventory();
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 7, false);
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		setInventory();

//		boolean onRNGCooldown = RNGesus.isOnCooldown(pitPlayer);
//		for(Megastreak megastreak : PerkManager.megastreaks) {
//			ItemStack item = new ItemStack(megastreak.getBaseDisplayStack().getType());
//			if(megastreak instanceof RNGesus && onRNGCooldown) item.setType(Material.ENDER_PEARL);
//			ItemMeta meta = item.getItemMeta();
//			List<String> lore = new ArrayList<>(megastreak.getBaseDisplayStack().getItemMeta().getLore());
//			lore.add("");
//			if(megastreak instanceof Uberstreak && pitPlayer.prestige >= megastreak.getPrestigeReq()) {
//				Uberstreak.checkUberReset(pitPlayer);
//				int ubersLeft = pitPlayer.dailyUbersLeft;
//				if(ubersLeft == 0)
//					lore.add(ChatColor.translateAlternateColorCodes('&', "&dDaily Uberstreaks remaining: &c0&7/" + Uberstreak.getMaxUbers(player)));
//				else
//					lore.add(ChatColor.translateAlternateColorCodes('&', "&dDaily Uberstreaks remaining: &a" + ubersLeft + "&7/" + Uberstreak.getMaxUbers(player)));
//			}
//			if(megastreak instanceof RNGesus && onRNGCooldown) {
//				lore.add(ChatColor.YELLOW + "Megastreak on cooldown! " + ChatColor.GRAY + "(" + RNGesus.getTimeLeft(pitPlayer) + ")");
//			}
//			if(pitPlayer.megastreak.getClass() == megastreak.getClass() && !(megastreak instanceof NoMegastreak)) {
//				lore.add(ChatColor.GREEN + "Already selected!");
//				meta.setDisplayName(ChatColor.GREEN + megastreak.getRefName());
//				meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
//			} else if(pitPlayer.prestige < megastreak.getPrestigeReq() && !(megastreak instanceof NoMegastreak)) {
//				lore.add(ChatColor.RED + "Unlocked at prestige " + ChatColor.YELLOW + AUtil.toRoman(megastreak.getPrestigeReq()));
//				meta.setDisplayName(ChatColor.RED + megastreak.getRefName());
//			} else if(megastreak instanceof Uberstreak && pitPlayer.dailyUbersLeft == 0) {
//				lore.add(ChatColor.RED + "Daily limit reached!");
//				meta.setDisplayName(ChatColor.RED + megastreak.getRefName());
//			} else if(pitPlayer.level < megastreak.getLevelReq(player)) {
//				PrestigeValues.PrestigeInfo info = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);
//				lore.add(ChatColor.translateAlternateColorCodes('&', "&cUnlocked at level " + info.getOpenBracket() + PrestigeValues.getLevelColor(megastreak.getLevelReq(player)) + megastreak.getLevelReq(player) + info.getCloseBracket()));
//				meta.setDisplayName(ChatColor.RED + megastreak.getRefName());
//			} else if(megastreak instanceof RNGesus && pitPlayer.renown < 1 && onRNGCooldown) {
//				lore.add(ChatColor.RED + "Click to select for " + RNGesus.RENOWN_COST + " renown!");
//				meta.setDisplayName(ChatColor.RED + megastreak.getRefName());
//			} else if(megastreak instanceof RNGesus && pitPlayer.renown >= 1 && onRNGCooldown) {
//				lore.add(ChatColor.YELLOW + "Click to select for " + RNGesus.RENOWN_COST + " renown!");
//				meta.setDisplayName(ChatColor.YELLOW + megastreak.getRefName());
//			} else if(!(megastreak instanceof NoMegastreak)) {
//				lore.add(ChatColor.YELLOW + "Click to select!");
//				meta.setDisplayName(ChatColor.YELLOW + megastreak.getRefName());
//			}
//			if(megastreak.getRefName().equalsIgnoreCase("Uberstreak")) {
//				meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false);
//			}
//			if(megastreak instanceof NoMegastreak) {
//				meta.setDisplayName(ChatColor.RED + megastreak.getRefName());
//				lore.add(ChatColor.YELLOW + "Click to remove megastreak!");
//			}
//			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
//			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
//			meta.setLore(lore);
//			item.setItemMeta(meta);
//
//			getInventory().setItem(megastreak.guiSlot(), item);
//		}
//
//		ItemStack back = new ItemStack(Material.ARROW);
//		ItemMeta meta = back.getItemMeta();
//		meta.setDisplayName(ChatColor.GREEN + "Go Back");
//		List<String> lore = new ArrayList<>();
//		lore.add(ChatColor.GRAY + "To Perks");
//		meta.setLore(lore);
//		back.setItemMeta(meta);
//
//		getInventory().setItem(22, back);
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
	}
}
