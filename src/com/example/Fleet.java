package com.example;

import java.util.EnumMap;
import java.util.Map;

public class Fleet {
	private Map<UnitType, Integer> units;
	private Map<UnitType, Integer> damagedUnits;
	private Map<UnitType, Integer> powerModifiers;
	private Integer hitPoints;
	private Integer armorPoints;

	Fleet(Map<UnitType, Integer> units, Map<UnitType, Integer> damagedUnits, Map<UnitType, Integer> powerModifiers) {
		this.units = units;
		this.damagedUnits = damagedUnits;
		this.powerModifiers = powerModifiers;
	}

	public int getTotalCount(UnitType type) {
		return units.get(type);
	}

	public int getDamagedCount(UnitType type) {
		return damagedUnits.get(type);
	}

	public int getPowerModifier(UnitType type) {
		return powerModifiers.get(type);
	}

	public int getHitPoints() {
		if (hitPoints == null) {
			int hp = 0;
			for (UnitType type : UnitType.values()) {
				if (type == UnitType.Defense)
					continue;
				hp += (units.get(type) << 1) - damagedUnits.get(type);
			}
			hitPoints = hp;
		}
		return hitPoints;
	}

	public int getArmorPoints() {
		if (armorPoints == null) {
			int ap = 0;
			for (UnitType type : UnitType.values()) {
				if (!type.armored)
					continue;
				ap += units.get(type) - damagedUnits.get(type);
			}
			armorPoints = ap;
		}
		return armorPoints;
	}

	public int rollShots() {
		int totalShots = 0;
		for (UnitType type : UnitType.values()) {
			if (type == UnitType.Defense)
				continue;
			totalShots += Simulator.roll(units.get(type), type.fireCount, powerModifiers.get(type), type.firePower);
		}
		return totalShots;
	}

	public static final class Builder {
		private Map<UnitType, Integer> units;
		private Map<UnitType, Integer> damagedUnits;
		private Map<UnitType, Integer> powerModifiers;

		public Builder() {
			this.units = new EnumMap<UnitType, Integer>(UnitType.class);
			this.damagedUnits = new EnumMap<UnitType, Integer>(UnitType.class);
			this.powerModifiers = new EnumMap<UnitType, Integer>(UnitType.class);
			for (UnitType type : UnitType.values()) {
				units.put(type, 0);
				damagedUnits.put(type, 0);
				powerModifiers.put(type, 0);
			}
		}

		public Builder(Fleet fleet) {
			this.units = new EnumMap<UnitType, Integer>(fleet.units);
			this.damagedUnits = new EnumMap<UnitType, Integer>(fleet.damagedUnits);
			this.powerModifiers = new EnumMap<UnitType, Integer>(fleet.powerModifiers);
		}

		public Builder add(UnitType type, int count) {
			if (units == null)
				throw new IllegalStateException();
			units.put(type, units.get(type) + count);
			return this;
		}

		public Builder damage(UnitType type, int count) {
			if (damagedUnits == null)
				throw new IllegalStateException();
			damagedUnits.put(type, damagedUnits.get(type) + count);
			return this;
		}

		public Builder kill(UnitType type, int count) {
			if (units == null)
				throw new IllegalStateException();
			if (type.armored && units.get(type) > damagedUnits.get(type))
				throw new IllegalStateException();
			units.put(type, units.get(type) - count);
			if (type.armored)
				damagedUnits.put(type, units.get(type));
			return this;
		}

		public Builder modifier(UnitType type, int diceBonus) {
			if (powerModifiers == null)
				throw new IllegalStateException();
			powerModifiers.put(type, diceBonus);
			return this;
		}

		public Fleet done() throws IllegalStateException {
			for (UnitType type : UnitType.values()) {
				int count = units.get(type);
				int damaged = damagedUnits.get(type);
				int modifier = powerModifiers.get(type);
				if (count < 0 || damaged < 0)
					throw new IllegalStateException();
				if (!type.armored && damaged > 0)
					throw new IllegalStateException();
				if (damaged > count)
					throw new IllegalStateException();
				if (modifier < -3 || modifier > 5)
					throw new IllegalStateException();
			}
			Fleet result = new Fleet(units, damagedUnits, powerModifiers);
			units = damagedUnits = powerModifiers = null;
			return result;
		}
	}
}
