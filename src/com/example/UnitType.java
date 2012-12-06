package com.example;

public enum UnitType {
	WarSun(3, 3, true), Dreadnought(5, 1, true), Cruiser(7, 1, false), Destroyer(9, 1, false), Carrier(9, 1, false), Fighter(9, 1, false), Defense(6, 1, false), GroundForce(9, 1, false);
	public final byte firePower;
	public final byte fireCount;
	public final boolean armored;

	private UnitType(int firePower, int fireCount, boolean armored) {
		this.firePower = (byte) firePower;
		this.fireCount = (byte) fireCount;
		this.armored = armored;
	}

}
