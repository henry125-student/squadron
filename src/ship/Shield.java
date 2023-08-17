package ship;

import entity.*;
import game.World;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

public class Shield extends Entity implements Damagable{

	Color team;
	double hp;
	double maxHp;
	double maxRadius;
	Polygon body = new Polygon();
	private double timer = 0;
	private Entity parent;
	private boolean doesDecay = true;
	
	public Shield(World world, double x, double y, double maxRadius, double hp, Color team, Entity parent) {
		super(world, x, y, 1, Math.PI/2);
		
		this.team = team;
		this.hp = hp;
		this.maxHp = hp;
		this.maxRadius = maxRadius;
		this.parent = parent;
		
		redefineBody();
		body.setFill(getTeam().deriveColor(0, 1, 1, 0.5)); 
		body.setStroke(getTeam().darker());
		body.setStrokeWidth(5);
		body.setStrokeLineJoin(StrokeLineJoin.ROUND);
		display.getChildren().add(body);
	}
	public Shield(World world, double x, double y, double maxRadius, double hp, Color team) {
		this(world, x, y, maxRadius, hp, team, null);
	}
	
	public static Shield getPermaShield(World world, Entity parent, double hp, Color team) {
		Shield out = new Shield(world, parent.getX(), parent.getY(),
				parent.getRadius()*1.7, hp, team, parent);
		out.doesDecay = false;
		return out;
	}
	
	private void redefineBody() {
		double radius = Math.sqrt((6.28318530718 * Math.pow(getRadius(), 2)/(5.19615242271)));
		body.getPoints().clear();
		for (int i = 0; i < 6; i++) {
			body.getPoints().addAll(radius * Math.cos(Math.PI*2 * (double)i/6),
					radius * Math.sin(Math.PI*2 * (double)i/6));
		}
		
	}
	
	public void setRadius(double newVal) {
		super.setRadius(newVal);
		
		if (team != null) {
			redefineBody();
		}
		
	}
	
	public void updateRadius() {
		
	}
	
	public Color getTeam() {
		return team;
	}
	
	public double getHp() {
		return hp;
	}
	public void setHp(double newVal) {
		hp = newVal;
	}
	public Entity getParent() {
		return parent;
	}
	
	public void beDestroyed() {
		die(250, 0);
	}
	
	public void run() {
		/*shiftRadius(61);
		
		if (getRadius() > maxRadius) {
			shiftRadius(-209);
		}*/
		shiftRadius((maxRadius * (Math.sin(timer*0.4)*0.1+1) - getRadius()) * 0.2);
		timer += 1;
		
		if (timer < 10) {
			hp = maxHp;
		}
		if (parent != null) {
			setX(parent.getX());
			setY(parent.getY());
			if (parent.getIsDead()) {
				beDestroyed();
			}
		} 
		
		body.setOpacity(hp/maxHp*0.5 + 0.5);
		if (doesDecay) {
			takeDamage(maxHp/200);
		}
	}

}
