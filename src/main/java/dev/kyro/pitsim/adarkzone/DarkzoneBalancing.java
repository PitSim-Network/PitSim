package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.StaticPitItem;
import dev.kyro.pitsim.aitems.diamond.DiamondLeggings;
import dev.kyro.pitsim.aitems.mystics.*;
import dev.kyro.pitsim.controllers.ItemFactory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class DarkzoneBalancing {
	public static final double SCYTHE_DAMAGE = 7.5;

	public static final int TIER_1_ENCHANT_COST = 5;
	public static final int TIER_2_ENCHANT_COST = 10;
	public static final int TIER_3_ENCHANT_COST = 25;
	public static final int TIER_4_ENCHANT_COST = 1_000;

	public static final int MAIN_PROGRESSION_COST_PER = 10;
	public static final double MAIN_PROGRESSION_MAJOR_MULTIPLIER = 1.5;
	public static final double BRANCH_DIFFICULTY_MULTIPLIER_INCREASE = 0.2;

	public static final double MOB_ITEM_DROP_PERCENT = 10;

	public static final double AVERAGE_XP_PER_100_SOULS = 10.0;
	public static final double AVERAGE_RENOWN_PER_100_SOULS = 1.2;
	public static final double AVERAGE_VOUCHERS_PER_100_SOULS = 3.0;
	public static final double NONE_REWARD_MULTIPLIER = 0.0;
	public static final double LOW_REWARD_MULTIPLIER = 1.0;
	public static final double HIGH_REWARD_MULTIPLIER = 1.5;

	public static final int PEDESTAL_NONE_THRESHOLD = 30;
	public static final int PEDESTAL_LOW_THRESHOLD = 80;
	public static final int PEDESTAL_INCREASE_PERCENT = 30;
	public static final double PEDESTAL_WEALTH_MULTIPLIER = 1.2;

	public static int getTravelCost(SubLevel subLevel) {
		return subLevel.getIndex() + 1;
	}

	public static int getAttributeAsInt(SubLevelType type, Attribute attribute) {
		return (int) Math.floor(getAttribute(type, attribute));
	}

	public static double getAttribute(SubLevelType subLevelType, Attribute attribute) {
		return attribute.getBaseValue() * Math.pow(attribute.getScalar(), subLevelType.getIndex());
	}

	public enum Attribute {
		BOSS_DAMAGE(5, 1.5),
		BOSS_HEALTH(40, 1.5),
		BOSS_SOULS(15, 1.4),
		MOB_DAMAGE(8.5, 1.5),
		MOB_HEALTH(22.5, 1.5),
		MOB_SOULS(5, 1.3),
		;

		private final double baseValue;
		private final double scalar;

		Attribute(double baseValue, double scalar) {
			this.baseValue = baseValue;
			this.scalar = scalar;
		}

		public double getBaseValue() {
			return baseValue;
		}

		public double getScalar() {
			return scalar;
		}
	}

	public enum ShredValue {
		JEWEL_PANTS(50, MysticPants.class),
		JEWEL_SWORD(50, MysticSword.class),
		JEWEL_BOW(50, MysticBow.class),
		TAINTED_SCYTHE(10, TaintedScythe.class),
		TAINTED_CHESTPLATE(10, TaintedChestplate.class),
		;

		private final int souls;
		private final Class<? extends PitItem> item;

		ShredValue(int souls, Class<? extends PitItem> item) {
			this.souls = souls;
			this.item = item;
		}

		public static ShredValue getShredValue(PitItem item) {
			for(ShredValue shredValue : values()) {
				if(shredValue.getItem().getClass().equals(item.getClass())) return shredValue;
			}
			return null;
		}

		public int getLowSouls() {
			return (int) (souls * 0.5);
		}

		public int getHighSouls() {
			return (int) (souls * 1.5);
		}

		public int getRandomSouls() {
			return (int) (souls * 0.5 + new Random().nextInt(souls + 1));
		}

		public PitItem getItem() {
			return ItemFactory.getItem(item);
		}
	}

	public enum ShopItem {
		DIAMOND_LEGGINGS(DiamondLeggings.class, 10),
		;

		private Class<? extends StaticPitItem> item;
		private int soulCost;

		ShopItem(Class<? extends StaticPitItem> item, int soulCost) {
			this.item = item;
			this.soulCost = soulCost;
		}

		public ItemStack getItem() {
			return ItemFactory.getItem(item).getItem();
		}

		public int getSoulCost() {
			return soulCost;
		}
	}

	public enum SkillUnlockCost {
		FIRST(100),
		PATH_1(500),
		PATH_2(1_000),
		PATH_3(1_500),
		MIDDLE(3_000),
		PATH_4(5_000),
		PATH_5(10_000),
		PATH_6(20_000),
		LAST(50_000),
		;

		private int cost;

		SkillUnlockCost(int cost) {
			this.cost = cost;
		}

		public int getCost() {
			return cost;
		}
	}
}
