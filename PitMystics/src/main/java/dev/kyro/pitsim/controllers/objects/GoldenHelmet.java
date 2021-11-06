package dev.kyro.pitsim.controllers.objects;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.HelmetSystem;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.helmetabilities.*;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GoldenHelmet {

	public static List<GoldenHelmet> helmets = new ArrayList<>();
	DecimalFormat formatter = new DecimalFormat("#,###.#");

	public Player owner;
	public ItemStack item;
	public int gold;
	public HelmetAbility ability = null;
	public UUID uuid;

	public GoldenHelmet(ItemStack item, Player owner) {
		this.item = item;
		this.owner  = owner;
		NBTItem nbtItem = new NBTItem(item);
		this.gold = nbtItem.getInteger(NBTTag.GHELMET_GOLD.getRef());
		this.ability = generateInstance(owner, nbtItem.getString(NBTTag.GHELMET_ABILITY.getRef()));
		this.uuid = UUID.fromString(nbtItem.getString(NBTTag.GHELMET_UUID.getRef()));
	}


	public static GoldenHelmet getHelmetItem(ItemStack helmet, Player player) {

		GoldenHelmet goldenHelmet = null;
		for(GoldenHelmet testGoldenHelmet : helmets) {

			NBTItem nbtHelmet = new NBTItem(helmet);
			NBTItem storedHelmet = new NBTItem(testGoldenHelmet.item);

			if(!nbtHelmet.hasKey(NBTTag.IS_GHELMET.getRef())) return null;

			if(!storedHelmet.getString(NBTTag.GHELMET_UUID.getRef()).equals(nbtHelmet.getString(NBTTag.GHELMET_UUID.getRef()))) continue;
			goldenHelmet = testGoldenHelmet;
			break;
		}
		if(goldenHelmet == null) {

			goldenHelmet = new GoldenHelmet(helmet, player);
			goldenHelmet.owner = player;
			helmets.add(goldenHelmet);
			goldenHelmet.setLore();



		}

		goldenHelmet.owner = player;
		return goldenHelmet;
	}


	public void setLore() {
		if(getInventorySlot(owner) == -1) return;
		ALoreBuilder loreBuilder = new ALoreBuilder();
		loreBuilder.addLore("");
		if(ability != null) {
			loreBuilder.addLore("&7Ability: &9" + ability.name);
			loreBuilder.addLore(ability.getDescription());
		}
		else loreBuilder.addLore("&7Ability: &cNONE");
		loreBuilder.addLore("", "&7Passives:");
		int passives = 0;
		for(HelmetSystem.Passive passive : HelmetSystem.Passive.values()) {
			int level = HelmetSystem.getLevel(gold);
			int passiveLevel = HelmetSystem.getTotalStacks(passive,level - 1);

			if(passiveLevel == 0) continue;
			passives++;

			if(passive.name().equals("DAMAGE_REDUCTION")) {
				loreBuilder.addLore(passive.color + "-" + passiveLevel * passive.baseUnit + "% " + passive.refName);
				continue;
			}
			if(passive.name().equals("SHARD_CHANCE"))  {
				loreBuilder.addLore(passive.color + "+" + passiveLevel * (passive.baseUnit / 10) + "% " + passive.refName);
				continue;
			}
			loreBuilder.addLore(passive.color + "+" + passiveLevel * passive.baseUnit + "% " + passive.refName);
		}
		if(passives == 0) loreBuilder.addLore("&cNONE");
		loreBuilder.addLore("", "&7Gold: &6" + formatter.format(gold) + "g", "", "&eHold and right-click to modify!");

		ItemMeta meta = item.getItemMeta();
		meta.setLore(loreBuilder.getLore());
		item.setItemMeta(meta);

		NBTItem nbtItem = new NBTItem(item);
		nbtItem.setInteger(NBTTag.GHELMET_GOLD.getRef(), gold);
		if(ability != null) nbtItem.setString(NBTTag.GHELMET_ABILITY.getRef(), ability.refName);

		if(getInventorySlot(owner) == -2) {
			item.setAmount(1);
			owner.getInventory().setHelmet(nbtItem.getItem());
			return;
		}


		item.setAmount(1);
		owner.getInventory().setItem(getInventorySlot(owner), nbtItem.getItem());

	}

	public static List<GoldenHelmet> getHelmetsFromPlayer(Player player) {
		List<GoldenHelmet> playerHelmets = new ArrayList<>();
		for(GoldenHelmet helmet : helmets) {
			if(helmet.owner == player) playerHelmets.add(helmet);
		}
		return playerHelmets;
	}



	public int getInventorySlot(Player owner) {
		for(int i = 0; i < owner.getInventory().getSize(); i++) {
			if(Misc.isAirOrNull(owner.getInventory().getItem(i))) continue;
			if(owner.getInventory().getItem(i).getType() == Material.GOLD_HELMET) {
				NBTItem helmetItem = new NBTItem(item);
				NBTItem playerItem = new NBTItem(owner.getInventory().getItem(i));

				if(!(helmetItem.getString(NBTTag.GHELMET_UUID.getRef()).equals(playerItem.getString(NBTTag.GHELMET_UUID.getRef())))) continue;

				return i;
			}
		}
		if(Misc.isAirOrNull(owner.getInventory().getHelmet())) return -1;
		if(owner.getInventory().getHelmet().getType() == Material.GOLD_HELMET) {

			NBTItem helmetItem = new NBTItem(item);
			NBTItem playerItem = new NBTItem(owner.getInventory().getHelmet());

			if(helmetItem.getString(NBTTag.GHELMET_UUID.getRef()).equals(playerItem.getString(NBTTag.GHELMET_UUID.getRef()))) return -2;
		}
		return -1;
	}

	public void setAbility(HelmetAbility ability) {
		if(ability == null) this.ability = null;
		else this.ability = generateInstance(owner, ability.refName);
		setLore();
	}

	public HelmetAbility generateInstance(Player player, String refName) {
		if(refName.equals("leap")) return new LeapAbility(player);
		if(refName.equals("pitblob")) return new BlobAbility(player);
		if(refName.equals("goldrush")) return new GoldRushAbility(player);
		if(refName.equals("hermit")) return new HermitAbility(player);
		if(refName.equals("judgement")) return new JudgementAbility(player);
		if(refName.equals("phoenix")) return new PhoenixAbility(player);
		return null;
	}


	public void depositGold(int gold) {
		this.gold += gold;
		setLore();
	}

	public boolean withdrawGold(int gold) {
		if(this.gold < gold) return false;
		else {
			if(HelmetSystem.getLevel(this.gold - gold) < HelmetSystem.getLevel(this.gold)) {
				AOutput.send(owner, "&6&lGOLDEN HELMET! &7Helmet level reduced to &f" +
						HelmetSystem.getLevel(this.gold - gold) + "&7. (&6" + formatter.format(this.gold - gold) + "g&7)");
				Sounds.HELMET_DOWNGRADE.play(ability.player);
			}
			this.gold -= gold;
			setLore();

		}
		return true;
	}

	public void deactivate() {
		HelmetAbility.toggledHelmets.remove(uuid);
		this.ability.isActive = false;
		this.ability.onDeactivate();
	}

}
