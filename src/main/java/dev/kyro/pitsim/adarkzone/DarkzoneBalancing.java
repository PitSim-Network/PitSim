package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.mystics.*;
import dev.kyro.pitsim.controllers.ItemFactory;

import java.util.Random;

public class DarkzoneBalancing {
	public static final double SCYTHE_DAMAGE = 7.5;

	public static final int TIER_1_ENCHANT_COST = 5;
	public static final int TIER_2_ENCHANT_COST = 10;
	public static final int TIER_3_ENCHANT_COST = 25;
	public static final int TIER_4_ENCHANT_COST = 1_000;

	public static int getTravelCost(SubLevel subLevel) {
		return subLevel.getIndex() + 1;
	}

	public static int getAttributeAsInt(SubLevelType type, Attribute attribute) {
		return (int) getAttribute(type, attribute);
	}

	public static double getAttribute(SubLevelType subLevelType, Attribute attribute) {
		return Math.floor(attribute.getBaseValue() * Math.pow(subLevelType.getIndex() + 1, attribute.getScalar()));
	}

	public enum Attribute {
		BOSS_DAMAGE(4, 1.3),
		BOSS_HEALTH(80, 1.3),
		BOSS_SOULS(10, 1.4),
		MOB_DAMAGE(8, 1.3),
		MOB_HEALTH(40, 1.3),
		MOB_SOULS(5, 1.4),
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
}
