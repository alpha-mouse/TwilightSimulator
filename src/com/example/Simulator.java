package com.example;

import static com.example.UnitType.Carrier;
import static com.example.UnitType.Cruiser;
import static com.example.UnitType.Destroyer;
import static com.example.UnitType.Dreadnought;
import static com.example.UnitType.Fighter;
import static com.example.UnitType.WarSun;

import java.util.Comparator;
import java.util.Random;

public class Simulator {
	private static final UnitType[] damagePriority = { Dreadnought, WarSun };
	private static final UnitType[] diePriority = { Fighter, Carrier, Destroyer, Cruiser, Dreadnought, WarSun };

	private Simulator() {
	}

	public static Simulator instance = new Simulator();

	public SimulateOutput simulateSpaceBattle(Fleet attacker, Fleet defender, int triesCount) {
		SimulateOutput result = new SimulateOutput();
		result.attackerFleet = attacker;
		result.defenderFleet = defender;
		int attackerWon = 0;
		int defenderWon = 0;
        int tied = 0;
		for (int currentTry = triesCount; currentTry > 0; --currentTry) {
			attacker = result.attackerFleet;
			defender = result.defenderFleet;
			boolean firstRound = true;
			while (attacker.getHitPoints() > 0 && defender.getHitPoints() > 0) {
				if (firstRound) {
					attacker = applyDamage(attacker,
							roll(defender.getTotalCount(UnitType.Defense), UnitType.Defense.fireCount, defender.getPowerModifier(UnitType.Defense), UnitType.Defense.firePower));
					defender = applyDamage(defender,
							roll(attacker.getTotalCount(UnitType.Defense), UnitType.Defense.fireCount, attacker.getPowerModifier(UnitType.Defense), UnitType.Defense.firePower));
					if (attacker.getTotalCount(Fighter) > 0) {
						int antiFighter = roll(defender.getTotalCount(UnitType.Destroyer), 2, defender.getPowerModifier(UnitType.Destroyer), UnitType.Destroyer.firePower);
						if (antiFighter > 0) {
							attacker = new Fleet.Builder(attacker).add(Fighter, -Math.min(antiFighter, attacker.getTotalCount(Fighter))).done();
						}
					}
					if (defender.getTotalCount(Fighter) > 0) {
						int antiFighter = roll(attacker.getTotalCount(UnitType.Destroyer), 2, attacker.getPowerModifier(UnitType.Destroyer), UnitType.Destroyer.firePower);
						if (antiFighter > 0) {
							defender = new Fleet.Builder(defender).add(Fighter, -Math.min(antiFighter, defender.getTotalCount(Fighter))).done();
						}
					}
				}
				int attackerShots = attacker.rollShots();
				int defenderShots = defender.rollShots();
				defender = applyDamage(defender, attackerShots);
				attacker = applyDamage(attacker, defenderShots);
				firstRound = false;
			}
			if (attacker.getHitPoints() > 0) {
				++attackerWon;
				if (fleetValueComparator.compare(attacker, result.attackerBest) > 0)
					result.attackerBest = attacker;
				if (fleetValueComparator.compare(attacker, result.attackerWorst) < 0)
					result.attackerWorst = attacker;
			} else if (defender.getHitPoints() > 0) {
				++defenderWon;
				if (fleetValueComparator.compare(defender, result.defenderBest) > 0)
					result.defenderBest = defender;
				if (fleetValueComparator.compare(defender, result.defenderWorst) < 0)
					result.defenderWorst = defender;
			}
            else{
                ++tied;
            }
		}
		result.attackerChance = (float) attackerWon / triesCount;
		result.defenderChance = (float) defenderWon / triesCount;
        result.tieChance = (float) tied / triesCount;
		return result;
	}

	private Fleet applyDamage(Fleet target, int shots) {
		if (shots == 0)
			return target;
		Fleet.Builder b = new Fleet.Builder(target);
		if (shots >= target.getHitPoints()) {
			for (UnitType type : UnitType.values()) {
				b.add(type, -target.getTotalCount(type));
				b.damage(type, -target.getDamagedCount(type));
			}
		} else {
			for (UnitType type : damagePriority) {
				if (shots <= 0)
					break;
				int damage = Math.min(target.getTotalCount(type) - target.getDamagedCount(type), shots);
				b.damage(type, damage);
				shots -= damage;
			}
			for (UnitType type : diePriority) {
				if (shots <= 0)
					break;
				int damage = Math.min(target.getTotalCount(type), shots);
				b.kill(type, damage);
				shots -= damage;
			}
		}
		return b.done();
	}

	private static Comparator<Fleet> fleetValueComparator = new Comparator<Fleet>() {
		@Override
		public int compare(Fleet o1, Fleet o2) {
			if (o1 == null)
				return -1;
			if (o2 == null)
				return 1;
			return o1.getHitPoints() - o2.getHitPoints();
		}
	};

	public static int roll(int unitCount, int fireCount, int modifier, int firePower) {
		Random rnd = new Random();
		int result = 0;
		for (int unitsToShot = unitCount; unitsToShot > 0; --unitsToShot) {
			for (int multiShotRound = fireCount; multiShotRound > 0; --multiShotRound) {
				int dice = rnd.nextInt(10) + 1 + modifier;
				if (dice >= firePower)
					++result;
			}
		}
		return result;
	}

}
