package ship.elite;

import entity.Damagable;
import entity.Entity;
import game.World;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import ship.KamikazeExplosion;
import ship.Projectile;

public class LaserProjectile extends Projectile {

	private Line beam;
	
	public LaserProjectile(World world, double x, double y, double radius, double direction, double speed,
			double damage, Color team) {
		super(world, x, y, radius, direction, speed, damage, team);
		this.setTimeLeft(3200/speed);
	}
	
	private void updatebeam() {
		beam.setStartX(getX());
		beam.setStartY(getY());
		beam.setEndX(getX()-this.getVelocityX());
		beam.setEndY(getY()-this.getVelocityY());
	}
	
	public void collideWith(Damagable subject) {
		
		//subject.takeDamage(getDamage());
		if (getDamage() > 0) {
			Entity e = new KamikazeExplosion(getWorld(), getX(), getY(), getRadius()*3,
					getDamage(), getTeam(), false);
			e.getDisplay().setOpacity(beam.getOpacity()/0.75);
			
			setDamage(getDamage()*0.9);
		}
		beam.setOpacity(beam.getOpacity()*0.9);
	}
	
	public void draw() {
		beam = new Line();
		if (getDamage() > 0) {
			beam.setStroke(getTeam());
		} else {
			beam.setStroke(Color.rgb(127,127,127));
		}
		beam.setStrokeLineCap(StrokeLineCap.ROUND);
		beam.setStrokeWidth(getRadius()*2);
		beam.setOpacity(0.75);
		if (getDamage() <= 0) {
			beam.setOpacity(0.3);
		}
		getWorld().getDisplay().getChildren().add(beam);
		updatebeam();
	}
	
	public void die() {
		super.die();
		if (beam != null) {
			getWorld().getDisplay().getChildren().remove(beam);
		}
	}
	
	public void run() {
		super.run();
		if (beam != null) {
			updatebeam();
		}
		
	}

}
