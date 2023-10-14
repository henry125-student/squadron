package ship;

import game.World;

public class MainSquad extends Squad {
	
	public static int squadCount = 0;
	public static final int squadCap = 20;

	public MainSquad(World world, double x, double y, double value) {
		super(world, x, y, value);
		squadCount++;
		// TODO Auto-generated constructor stub
	}

	public MainSquad(World world, double x, double y, Ship... ships) {
		super(world, x, y, ships);
		squadCount++;
		// TODO Auto-generated constructor stub
	}

	public MainSquad(World world, double x, double y, double material, double energy, Ship... ships) {
		super(world, x, y, material, energy, ships);
		squadCount++;
		// TODO Auto-generated constructor stub
	}
	
	public void die() {
		super.die();
		squadCount--;
	}

}
