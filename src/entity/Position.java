package entity;

import game.World;

public class Position extends Entity{
	public Position(World world, double x, double y, double radius) {
		super(world, x, y, radius);
		world.remove(this);
		world.getDisplay().getChildren().remove(display);
	}
	
	public boolean getIsDead() {
		return true;
	}
	
	public Position(World world, double x, double y) {
		this(world, x, y, 10);
	}
}
