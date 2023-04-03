package dev.kyro.pitsim.misc;

import de.myzelyam.api.vanish.VanishAPI;
import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.libs.discord.DiscordWebhook;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.BossManager;
import dev.kyro.pitsim.adarkzone.DarkzoneManager;
import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.TemporaryItem;
import dev.kyro.pitsim.commands.LightningCommand;
import dev.kyro.pitsim.controllers.*;
import dev.kyro.pitsim.controllers.objects.HelmetManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.cosmetics.CosmeticManager;
import dev.kyro.pitsim.cosmetics.CosmeticType;
import dev.kyro.pitsim.cosmetics.PitCosmetic;
import dev.kyro.pitsim.cosmetics.killeffectsbot.IronKill;
import dev.kyro.pitsim.enums.DiscordLogChannel;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PitEntityType;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.megastreaks.Overdrive;
import dev.kyro.pitsim.megastreaks.Uberstreak;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.World;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Misc {

	public static boolean isEntityNoError(Entity entity, PitEntityType... entityTypes) {
		if(!(entity instanceof LivingEntity)) return false;
		LivingEntity livingEntity = (LivingEntity) entity;
		for(PitEntityType entityType : entityTypes) {
			switch(entityType) {
				case REAL_PLAYER:
					if(PlayerManager.isRealPlayer(livingEntity)) return true;
					break;
				case NON:
					if(NonManager.getNon(livingEntity) != null) return true;
					break;
				case HOPPER:
					if(HopperManager.isHopper(livingEntity)) return true;
					break;
				case PIT_MOB:
					if(DarkzoneManager.isPitMob(livingEntity)) return true;
					break;
				case PIT_BOSS:
					if(BossManager.isPitBoss(livingEntity)) return true;
					break;
			}
		}
		return false;
	}

	public static void stunEntity(LivingEntity livingEntity, int ticks) {
		Misc.applyPotionEffect(livingEntity, PotionEffectType.SLOW, ticks, 7, true, false);
		Misc.applyPotionEffect(livingEntity, PotionEffectType.JUMP, ticks, 128, true, false);
		Misc.applyPotionEffect(livingEntity, PotionEffectType.SLOW_DIGGING, ticks, 99, true, false);

		if(livingEntity instanceof Player) {
			Player player = (Player) livingEntity;
			Misc.sendTitle(player, "&cSTUNNED", ticks);
			Misc.sendSubTitle(player, "&eYou cannot move!", ticks);
			Sounds.COMBO_STUN.play(player);
		}
	}

	private static int nextEntityID = Integer.MIN_VALUE;
	public static int getNextEntityID() {
		return nextEntityID++;
	}

//	Doesn't work on all blocks
	public static Sound getBlockBreakSound(Block block) {
		World nmsWorld = ((CraftWorld) block.getWorld()).getHandle();
		net.minecraft.server.v1_8_R3.Block nmsBlock = nmsWorld.getType(
				new BlockPosition(block.getX(), block.getY(), block.getZ())).getBlock();
		try {
			return Sound.valueOf(nmsBlock.stepSound.getBreakSound().replaceAll("\\.", "_").toUpperCase());
		} catch(Exception ignored) {
			return null;
		}
	}

	public static List<Player> getNearbyRealPlayers(Location location, double range) {
		List<Player> realPlayers = new ArrayList<>();
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(onlinePlayer.getWorld() != location.getWorld()) continue;
			Location testLocation = onlinePlayer.getLocation();
			if(Math.abs(testLocation.getX() - location.getX()) > range || Math.abs(testLocation.getY() - location.getY()) > range ||
					Math.abs(testLocation.getZ() - location.getZ()) > range) continue;
			realPlayers.add(onlinePlayer);
		}
		return realPlayers;
	}

	public static boolean isEntity(Entity entity, PitEntityType... entityTypes) {
		if(!(entity instanceof LivingEntity)) return false;
		LivingEntity livingEntity = (LivingEntity) entity;
		for(PitEntityType entityType : entityTypes) {
			switch(entityType) {
				case REAL_PLAYER:
					if(PlayerManager.isRealPlayer(livingEntity)) return true;
					break;
				case NON:
					if(NonManager.getNon(livingEntity) != null) return true;
					break;
				case HOPPER:
					if(HopperManager.isHopper(livingEntity)) return true;
					break;
				case PIT_MOB:
					if(DarkzoneManager.isPitMob(livingEntity)) return true;
					break;
				case PIT_BOSS:
					if(BossManager.isPitBoss(livingEntity)) return true;
					break;
			}
		}
		return false;
	}

	public static boolean isValidMobPlayerTarget(Entity entity, Entity... excluded) {
		List<Entity> excludedList = Arrays.asList(excluded);

		if(!(entity instanceof LivingEntity) || SpawnManager.isInSpawn(entity.getLocation()) || excludedList.contains(entity)) return false;
		LivingEntity livingEntity = (LivingEntity) entity;

		PitMob pitMob = DarkzoneManager.getPitMob(livingEntity);
		PitBoss pitBoss = BossManager.getPitBoss(livingEntity);
		if(pitMob == null && pitBoss == null && !(entity instanceof Player)) return false;

		if(entity instanceof Player) {
			Player player = (Player) entity;
			if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR || VanishAPI.isInvisible(player)) return false;
		}
		return true;
	}

	public static LivingEntity getMobPlayerClosest(Location location, double radius, Entity... excluded) {
		List<Entity> excludedList = Arrays.asList(excluded);

		LivingEntity closestEntity = null;
		double closestDistance = Double.MAX_VALUE;
		for(Entity entity : location.getWorld().getNearbyEntities(location, radius, radius, radius)) {
			if(excludedList.contains(entity)) continue;
			if(!isValidMobPlayerTarget(entity, excluded)) continue;
			LivingEntity livingEntity = (LivingEntity) entity;

			double testDistance = livingEntity.getLocation().distance(location);
			if(testDistance > closestDistance) continue;
			closestEntity = livingEntity;
			closestDistance = testDistance;
		}
		return closestEntity;
	}

	public static void alertDiscord(String message) {
		DiscordWebhook discordWebhook = new DiscordWebhook(PrivateInfo.ALERTS_WEBHOOK);
		discordWebhook.setContent(message);
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					discordWebhook.execute();
				} catch(IOException exception) {
					exception.printStackTrace();
				}
			}
		}.runTaskAsynchronously(PitSim.INSTANCE);
	}

	public static void logToDiscord(DiscordLogChannel logChannel, String message) {
		new PluginMessage()
				.writeString("LOG_TO_DISCORD")
				.writeString(logChannel.name())
				.writeString(message)
				.send();
	}

	public static int getItemCount(Player player, boolean checkArmor, BiPredicate<PitItem, ItemStack> condition) {
		int count = 0;
		List<ItemStack> items = new ArrayList<>(Arrays.asList(player.getInventory().getContents()));
		if(checkArmor) items.addAll(Arrays.asList(player.getInventory().getArmorContents()));
		for(ItemStack itemStack : items) {
			PitItem pitItem = ItemFactory.getItem(itemStack);
			if(pitItem == null || !condition.test(pitItem, itemStack)) continue;
			count += itemStack.getAmount();
		}
		return count;
	}

	public static boolean removeItems(Player player, int amount, BiPredicate<PitItem, ItemStack> condition) {
		if(getItemCount(player, false, condition) < amount) return false;
		for(int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack itemStack = player.getInventory().getItem(i);
			PitItem pitItem = ItemFactory.getItem(itemStack);
			if(pitItem == null || !condition.test(pitItem, itemStack)) continue;

			if(amount >= itemStack.getAmount()) {
				player.getInventory().setItem(i, new ItemStack(Material.AIR));
				amount -= itemStack.getAmount();
				if(amount == 0) return true;
			} else {
				itemStack.setAmount(itemStack.getAmount() - amount);
				player.getInventory().setItem(i, itemStack);
				return true;
			}
		}
		throw new RuntimeException();
	}

	public static String distortMessage(String message, double chance) {
		message = ChatColor.translateAlternateColorCodes('&', message);
		char[] chars = message.toCharArray();
		String finalString = "";
		for(int i = 0; i < chars.length; i++) {
			char character = chars[i];
			if(character == ' ') {
				finalString += character;
				continue;
			}
			if(character == '\u00A7') {
				i++;
				continue;
			}

			if(Math.random() > chance) {
				finalString += chars[i];
			} else {
				String substring = message.substring(0, i);
				finalString += "\u00A7k" + chars[i] + ChatColor.getLastColors(substring);
			}
		}
		return finalString;
	}

	public static void broadcast(String message) {
		new PluginMessage()
				.writeString("BROADCAST")
				.writeString(message)
				.send();
	}

	public static List<String> getTabComplete(String current, List<String> options) {
		return getTabComplete(current, options.toArray(new String[0]));
	}

	public static List<String> getTabComplete(String current, String... options) {
		if(current == null || current.isEmpty()) return Arrays.asList(options);
		List<String> tabComplete = new ArrayList<>();
		for(String option : options) if(option.toLowerCase().startsWith(current.toLowerCase())) tabComplete.add(option);
		return tabComplete;
	}

	public static <T> T weightedRandom(Map<T, Double> weightedMap) {
		if(weightedMap.isEmpty()) throw new RuntimeException();
		// Normalize the weights
		double sum = 0.0;
		for(double weight : weightedMap.values()) sum += weight;
		Map<T, Double> normalizedWeights = new HashMap<>();
		for(Map.Entry<T, Double> entry : weightedMap.entrySet()) normalizedWeights.put(entry.getKey(), entry.getValue() / sum);

		// Select a random number between 0 and 1
		double rand = Math.random();

		// Find the element corresponding to the random number
		double total = 0.0;
		for(Map.Entry<T, Double> entry : normalizedWeights.entrySet()) {
			total += entry.getValue();
			if(total >= rand) return entry.getKey();
		}

		return normalizedWeights.entrySet().iterator().next().getKey();
	}

	public static DecimalFormat goldFormat = new DecimalFormat("#,###.##");
	public static String formatGoldFull(double amount) {
		return goldFormat.format(amount);
	}

	public static String getDisplayName(Player player) {
		String playerName = "%luckperms_prefix%%pitsim_nickname%";
		return PlaceholderAPI.setPlaceholders(player, playerName);
	}

	public static String stringifyItem(ItemStack itemStack) {
		String serializedItem = "";
		if(Misc.isAirOrNull(itemStack)) return addBraces(serializedItem);
		if(!itemStack.hasItemMeta()) return addBraces(itemStack.getType().toString());
		serializedItem += itemStack.getAmount() + "x";

		NBTItem nbtItem = new NBTItem(itemStack);
		ItemMeta itemMeta = itemStack.getItemMeta();
		serializedItem += " " + itemStack.getType();

		PitItem pitItem = ItemFactory.getItem(itemStack);

		if(pitItem != null && pitItem.isMystic) {
			TemporaryItem.TemporaryType temporaryType = pitItem.getTemporaryType(itemStack);
			TemporaryItem temporaryItem = temporaryType == null ? null : pitItem.getAsTemporaryItem();

			serializedItem += " " + nbtItem.getString(NBTTag.ITEM_UUID.getRef());

			if(temporaryType == TemporaryItem.TemporaryType.LOOSES_LIVES_ON_DEATH) {
				serializedItem += " " + temporaryItem.getLives(itemStack) + "/" + temporaryItem.getMaxLives(itemStack);
			}

			if(nbtItem.hasKey(NBTTag.IS_GEMMED.getRef())) serializedItem += " Gemmed";
			if(EnchantManager.isJewelComplete(itemStack)) serializedItem += " Jewel: " +
					EnchantManager.getEnchant(nbtItem.getString(NBTTag.ITEM_JEWEL_ENCHANT.getRef())).getDisplayName();
			serializedItem += " Enchants:";
			for(Map.Entry<PitEnchant, Integer> entry : EnchantManager.getEnchantsOnItem(itemStack).entrySet()) {
				serializedItem += " " + entry.getKey().getDisplayName() + " " + entry.getValue();
			}
		}
		if(!itemMeta.hasDisplayName()) return addBraces(serializedItem);
		serializedItem += " " + ChatColor.stripColor(itemMeta.getDisplayName());
		return addBraces(serializedItem);
	}

	public static String addBraces(String string) {
		return "{" + string + "}";
	}

	public static boolean hasSpaceForItem(Player player, ItemStack itemStack) {
		if(itemStack == null) return false;
		if(itemStack.getType() == Material.AIR) return true;

		int emptySlots = getEmptyInventorySlots(player);
		if(emptySlots != 0) return true;

		int itemsToFit = itemStack.getAmount();
		int maxStackSize = itemStack.getType().getMaxStackSize();
		for(ItemStack inventoryStack : player.getInventory()) {
			if(Misc.isAirOrNull(inventoryStack) || !inventoryStack.isSimilar(itemStack)) continue;
			int space = maxStackSize - inventoryStack.getAmount();
			if(space >= itemsToFit) return true;
			itemsToFit -= space;
		}
		return false;
	}

	public static int getEmptyInventorySlots(Player player) {
		int emptySlots = 0;
		for(int i = 0; i < 36; i++) if(Misc.isAirOrNull(player.getInventory().getItem(i))) emptySlots++;
		return emptySlots;
	}

	public static String getStateMessage(boolean state) {
		return state ? "&a&lENABLED" : "&c&lDISABLED";
	}

	public static double randomOffset(double variance) {
		return Math.random() * variance - variance / 2;
	}

	public static double randomOffsetPositive(double variance) {
		return Math.random() * variance / 2;
	}

	public static String getBountyClaimedMessage(PitPlayer pitKiller, PitPlayer pitDead, String bounty) {
		PitCosmetic pitCosmetic = null;
		for(PitCosmetic equippedCosmetic : CosmeticManager.getEquippedCosmetics(pitKiller)) {
			if(equippedCosmetic.cosmeticType != CosmeticType.BOUNTY_CLAIM_MESSAGE) continue;
			pitCosmetic = equippedCosmetic;
			break;
		}

		String killerName = PlaceholderAPI.setPlaceholders(pitKiller.player, "%luckperms_prefix%" + pitKiller.player.getDisplayName());
		String deadName = PlaceholderAPI.setPlaceholders(pitDead.player, "%luckperms_prefix%" + pitDead.player.getDisplayName());

		String message = "&6&lCLAIM!&7 ";
		if(pitCosmetic != null) {
			message += pitCosmetic.getBountyClaimMessage(killerName, deadName, bounty);
		} else {
			message += killerName + "&7 killed " + deadName + "&7 for " + bounty;
		}

		return message;
	}

	public static byte getTetrisWoolColor() {
		int randomInt = new Random().nextInt(6);
		switch(randomInt) {
			case 0:
				return 1;
			case 1:
				return 2;
			case 2:
				return 3;
			case 3:
				return 4;
			case 4:
				return 5;
			case 5:
				return 14;
		}
		return -1;
	}

	public static void addEnchantGlint(ItemStack itemStack) {
		itemStack.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		itemStack.setItemMeta(itemMeta);
	}

	public static void removeEnchantGlint(ItemStack itemStack) {
		itemStack.removeEnchantment(Enchantment.WATER_WORKER);
	}

	public static String fetchUsernameFromMojang(String uuid) {
		try {
			// Make the HTTP request to the Mojang API
			URL url = new URL("https://api.mojang.com/user/profile/" + uuid);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			// Read the response from the Mojang API
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Parse the JSON response to get the updated username=
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(response.toString());
			return (String) jsonObject.get("name");
		} catch(Exception exception) {
			exception.printStackTrace();
			return "KyroKrypt";
		}
	}

//	public static Date convertToEST(Date date) {
//		DateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
//		formatter.setTimeZone(TimeZone.getTimeZone("EST"));
//		try {
//			return formatter.parse((formatter.format(date)));
//		} catch(ParseException exception) {
//			throw new RuntimeException();
//		}
//	}

	public static String ordinalWords(int num) {

		switch(num) {
			case 1:
				return "";
			case 2:
				return "second";
			case 3:
				return "third";
			case 4:
				return "fourth";
			case 5:
				return "fifth";
			case 6:
				return "sixth";
			case 7:
				return "seventh";
			case 8:
				return "eighth";
			case 9:
				return "ninth";
			case 10:
				return "tenth";
		}
		return "";
	}

	public static void applyPotionEffect(LivingEntity entity, PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles) {
		if(entity == null || amplifier < 0 || duration == 0) return;

		if(NonManager.getNon(entity) == null && entity instanceof Player) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer((Player) entity);
			if(pitPlayer.megastreak instanceof Uberstreak) {
				Uberstreak uberstreak = (Uberstreak) pitPlayer.megastreak;
				if(uberstreak.uberEffects.contains(Uberstreak.UberEffect.NO_SPEED) && type == PotionEffectType.SPEED)
					return;
			} else if(!(pitPlayer.megastreak instanceof Overdrive) && pitPlayer.megastreak.isOnMega() && type == PotionEffectType.SLOW)
				return;
		}

		for(PotionEffect potionEffect : entity.getActivePotionEffects()) {
			if(!potionEffect.getType().equals(type) || potionEffect.getAmplifier() > amplifier) continue;
			if(potionEffect.getAmplifier() == amplifier && potionEffect.getDuration() >= duration) continue;
			entity.removePotionEffect(type);
			break;
		}
		entity.addPotionEffect(new PotionEffect(type, duration, amplifier, ambient, particles));
		if(type == PotionEffectType.POISON) {
			if(HelmetManager.abilities.get(entity) != null) {
				HelmetManager.abilities.get(entity).onDeactivate();
			}
			HelmetManager.toggledPlayers.remove(entity);
		}
	}

	public static void playKillSound(PitPlayer pitPlayer) {
		playKillSound(pitPlayer, null);
	}

	public static void playKillSound(PitPlayer pitPlayer, PitCosmetic killEffect) {
		PitCosmetic pitCosmetic = null;
		for(PitCosmetic equippedCosmetic : CosmeticManager.getEquippedCosmetics(pitPlayer)) {
			if(equippedCosmetic.cosmeticType != CosmeticType.BOT_KILL_EFFECT) continue;
			pitCosmetic = equippedCosmetic;
			break;
		}

		if(pitCosmetic != null && pitCosmetic.preventKillSound) return;
		new BukkitRunnable() {
			int count = 0;

			@Override
			public void run() {

				switch(count) {
					case 0:
						if(killEffect instanceof IronKill) Sounds.IRON_1.play(pitPlayer.player);
						else Sounds.MULTI_1.play(pitPlayer.player);
						break;
					case 1:
						if(killEffect instanceof IronKill) Sounds.IRON_2.play(pitPlayer.player);
						else Sounds.MULTI_2.play(pitPlayer.player);
						break;
					case 2:
						if(killEffect instanceof IronKill) Sounds.IRON_3.play(pitPlayer.player);
						else Sounds.MULTI_3.play(pitPlayer.player);
						break;
					case 3:
						if(killEffect instanceof IronKill) Sounds.IRON_4.play(pitPlayer.player);
						else Sounds.MULTI_4.play(pitPlayer.player);
						break;
					case 4:
						if(killEffect instanceof IronKill) Sounds.IRON_5.play(pitPlayer.player);
						else Sounds.MULTI_5.play(pitPlayer.player);
						break;
				}

				if(++count > 5) cancel();
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 2L);
	}

	/**
	 * Rounds damage and then converts to hearts.
	 * Should only be used for displaying, not calculation.
	 */
	public static String getHearts(double damage) {

		return roundString(damage / 2) + "\u2764";
	}

	/**
	 * Rounds a number to 2 decimal places and trims extra zeros.
	 * Should only be used for displaying, not calculation.
	 */
	public static String roundString(double number) {
		return new DecimalFormat("#,##0.##").format(number);
	}

	/**
	 * Converts to multiplier
	 */
	public static double getReductionMultiplier(double reduction) {
		return Math.min(Math.max(1 - (reduction / 100.0), 0), 1);
	}

	public static int linearEnchant(int level, double step, double base) {
		return (int) (level * step + base);
	}

	public static void sendTitle(Player player, String message, int length) {
		IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" +
				ChatColor.translateAlternateColorCodes('&', message) + "\"}");

		PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle);
		PacketPlayOutTitle titleLength = new PacketPlayOutTitle(5, length, 5);

		((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
	}

	public static void sendSubTitle(Player player, String message, int length) {
		IChatBaseComponent chatSubTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" +
				ChatColor.translateAlternateColorCodes('&', message) + "\"}");

		PacketPlayOutTitle subtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatSubTitle);
		PacketPlayOutTitle subTitleLength = new PacketPlayOutTitle(5, length, 5);

		((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitle);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(subTitleLength);
	}

	public static boolean isCritical(LivingEntity entity) {
		if(entity == null) return false;
		return entity.getFallDistance() > 0.0F &&
				!entity.isOnGround() &&
				!entity.isInsideVehicle() &&
				!entity.hasPotionEffect(PotionEffectType.BLINDNESS) &&
				entity.getLocation().getBlock().getType() != Material.LADDER &&
				entity.getLocation().getBlock().getType() != Material.VINE;
	}

	public static boolean isAirOrNull(ItemStack itemStack) {
		return itemStack == null || itemStack.getType() == Material.AIR;
	}

	public static void strikeLightningForPlayers(Location location, double radius) {
		List<Player> nearbyPlayers = new ArrayList<>();
		for(Entity nearbyEntity : location.getWorld().getNearbyEntities(location, radius, radius, radius)) {
			if(!(nearbyEntity instanceof Player) || NonManager.getNon((Player) nearbyEntity) != null) continue;
			nearbyPlayers.add((Player) nearbyEntity);
		}
		for(Player lightningPlayer : LightningCommand.lightningPlayers) nearbyPlayers.remove(lightningPlayer);

		Player[] lightningPlayers = new Player[nearbyPlayers.size()];
		lightningPlayers = nearbyPlayers.toArray(lightningPlayers);
		strikeLightningForPlayers(location, lightningPlayers);
	}

	public static void strikeLightningForPlayers(Location location, Player... players) {
		World world = ((CraftWorld) location.getWorld()).getHandle();

		for(Player player : players) {
			EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
			entityPlayer.playerConnection.sendPacket(new PacketPlayOutSpawnEntityWeather(
					new EntityLightning(world, location.getX(), location.getY(), location.getZ(), false, false)));

			player.playSound(location, Sound.AMBIENCE_THUNDER, 10, 1);
			player.playSound(location, Sound.EXPLODE, 10, (float) (Math.random() * 0.2 + 0.6));
		}
	}

	public static long getRunnableOffset(int minutes) {
		return (long) (Math.random() * 20 * 60 * minutes);
	}

	public static Duration parseDuration(String durationString) throws Exception {
		durationString = durationString.toLowerCase().replaceAll(" ", "");
		Matcher matcher = Pattern.compile("(\\d+)([mhdw])").matcher(durationString);
		Duration duration = Duration.ZERO;
		while(matcher.find()) {
			int number = Integer.parseInt(matcher.group(1));
			String timeUnit = matcher.group(2);
			switch(timeUnit) {
				case "m":
					duration = duration.plus(Duration.ofMinutes(number));
					break;
				case "h":
					duration = duration.plus(Duration.ofHours(number));
					break;
				case "d":
					duration = duration.plus(Duration.ofDays(number));
					break;
				case "w":
					duration = duration.plus(Duration.ofDays(number).multipliedBy(7));
			}
		}
		if(duration.equals(Duration.ZERO)) throw new Exception();
		return duration;
	}

	public static String formatDurationFull(long millis, boolean displaySeconds) {
		return formatDurationFull(Duration.ofMillis(millis), displaySeconds);
	}

	public static String formatDurationFull(Duration duration, boolean displaySeconds) {
		long millis = duration.toMillis();
		long days = millis / (24 * 60 * 60 * 1000);
		millis %= (24 * 60 * 60 * 1000);
		long hours = millis / (60 * 60 * 1000);
		millis %= (60 * 60 * 1000);
		long minutes = millis / (60 * 1000);
		millis %= (60 * 1000);
		long seconds = millis / 1000;
		if(!displaySeconds) {
			if(seconds != 0) minutes++;
			if(minutes == 60) {
				minutes = 0;
				hours++;
			}
			if(hours == 24) {
				hours = 0;
				days++;
			}
		}

		String durationString = "";
		if(days != 0) durationString += days + "d ";
		if(hours != 0) durationString += hours + "h ";
		if(minutes != 0) durationString += minutes + "m ";
		if(displaySeconds && seconds != 0) durationString += seconds + "s";
		return durationString.trim();
	}

	public static String formatDurationMostSignificant(double seconds) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		if(seconds < 60) return decimalFormat.format(seconds) + " seconds";
		if(seconds < 60 * 60) return decimalFormat.format(seconds / 60.0) + " minutes";
		if(seconds < 60 * 60 * 24) return decimalFormat.format(seconds / 60.0 / 60.0) + " hours";
		return decimalFormat.format(seconds / 60.0 / 60.0 / 24.0) + " days";
	}

	public static String formatLarge(double large) {
		DecimalFormat decimalFormat = new DecimalFormat("#,##0.#");
		if(large < 1_000) return decimalFormat.format(large);
		if(large < 1_000_000) return decimalFormat.format(large / 1_000.0) + "K";
		if(large < 1_000_000_000) return decimalFormat.format(large / 1_000_000.0) + "M";
		return decimalFormat.format(large / 1_000_000_000) + "B";
	}

	public static String formatRatio(double ratio) {
		if(ratio < 1_000) return new DecimalFormat("#,##0.###").format(ratio);
		if(ratio < 1_000_000) return new DecimalFormat("#,##0.#").format(ratio / 1_000) + "K";
		return new DecimalFormat("#,##0.#").format(ratio / 1_000_000) + "M";
	}

	public static String formatPercent(double percent) {
		return new DecimalFormat("0.0").format(percent * 100) + "%";
	}

	public static HealEvent heal(LivingEntity entity, double amount, HealEvent.HealType healType, int max, PitEnchant pitEnchant) {
		if(max == -1) max = Integer.MAX_VALUE;

		HealEvent healEvent = new HealEvent(entity, amount, healType, max);
		healEvent.pitEnchant = pitEnchant;
		Bukkit.getServer().getPluginManager().callEvent(healEvent);

		if(healType == HealEvent.HealType.HEALTH) {
			entity.setHealth(Math.min(entity.getHealth() + healEvent.getFinalHeal(), entity.getMaxHealth()));
		} else {
			EntityPlayer nmsPlayer = ((CraftPlayer) entity).getHandle();
			if(nmsPlayer.getAbsorptionHearts() < healEvent.max)
				nmsPlayer.setAbsorptionHearts(Math.min((float) (nmsPlayer.getAbsorptionHearts() + healEvent.getFinalHeal()), max));
		}
		return healEvent;
	}

	public static String ticksToTime(int ticks) {
		int seconds = (int) Math.floor(ticks / 20);
		int minutes = (int) Math.floor(seconds / 60);
		if(minutes != 0 && seconds != 0) return minutes + "m " + (seconds - (minutes * 60)) + "s";
		else if(seconds != 0) return seconds + "s";
		else return minutes + "s";
	}

	public static boolean isKyro(UUID uuid) {
		List<UUID> kyroAccounts = new ArrayList<>();
//		KyroKrypt
		kyroAccounts.add(UUID.fromString("01acbb49-6357-4502-81ca-e79f4b31a44e"));
//		BHunter
		kyroAccounts.add(UUID.fromString("a7d2e208-e475-40bf-94d7-f96c6e1238a8"));
//		UUIDSpoof
		kyroAccounts.add(UUID.fromString("1088c509-e8e0-4b7b-8faa-b9f3f72b06f6"));
//		PayForTruce
		kyroAccounts.add(UUID.fromString("777566ed-d4ad-4cf1-a4d9-0e37769357df"));
//		Fishduper
		kyroAccounts.add(UUID.fromString("1db946e6-edfe-42ac-9fd6-bf135aa5130e"));
		return kyroAccounts.contains(uuid);
	}

	public static boolean isWiji(UUID uuid) {
		List<UUID> wijiAccounts = new ArrayList<>();
//		wiji1
		wijiAccounts.add(UUID.fromString("5c06626c-66bf-48a9-84cb-f2683e6aca87"));
		return wijiAccounts.contains(uuid);
	}

	public static TextComponent createItemHover(ItemStack itemStack) {
		if(Misc.isAirOrNull(itemStack)) {
			return null;
		}

		ItemMeta itemMeta = itemStack.hasItemMeta() ? itemStack.getItemMeta() : Bukkit.getItemFactory().getItemMeta(itemStack.getType());
		String displayName = itemMeta.getDisplayName();
		if(displayName == null) {
			net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
			displayName = nmsStack.getName();
		}

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(displayName);

		List<String> lore = itemMeta.getLore();
		if(itemMeta.hasLore()) {
			for(int i = 0; i < lore.size(); i++) {
				stringBuilder.append("\n");
				stringBuilder.append(lore.get(i));
			}
		} else {
			stringBuilder.append("\n").append(ChatColor.GRAY).append("Just a plain ").append(displayName);
		}

		BaseComponent[] hoverEventComponents = new BaseComponent[]{
				new TextComponent(String.valueOf(stringBuilder))
		};

		TextComponent hoverComponent = new TextComponent(ChatColor.translateAlternateColorCodes('&', displayName));
		hoverComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverEventComponents));
		return hoverComponent;
	}

	public static Map<UUID, String> rankColorMap = new HashMap<>();

	public static String getRankColor(UUID uuid) {
		if(rankColorMap.containsKey(uuid)) return rankColorMap.get(uuid);
		try {
			String rankColor = PitSim.LUCKPERMS.getUserManager().loadUser(uuid).get().getCachedData().getMetaData().getPrefix();
//			TODO: Check-in with players to make sure this fixed it and it wasn't returning "null" as a string
			if(rankColor == null) throw new Exception();
			rankColorMap.put(uuid, rankColor);
			return rankColor;
		} catch(Exception ignored) {
			return "&7";
		}
	}

	public static void createMeta(ItemStack itemStack) {
		if(itemStack.hasItemMeta()) return;
		ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
		itemStack.setItemMeta(itemMeta);
	}

	public static int getFallTime(int totalHeight) {
		final double gravity = -0.03999999910593033;
		final double drag = 0.9800000190734863;

		double locY = 0;
		double motY = 0;

		int ticks = 0;

		while(locY <= totalHeight) {
			locY += motY;
			motY *= drag;
			motY -= gravity;

			ticks++;
		}

		return ticks;
	}

	public static String s(double value) {
		return value == 1 ? "" : "s";
	}
}
