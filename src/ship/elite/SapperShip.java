package ship.elite;

import java.util.ArrayList;
import java.util.List;

import entity.*;
import game.World;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import ship.*;

public class SapperShip extends Elite {
	private List<Polygon> fans;
	private double rotate = 0;
	private double rotateSpeed = 0;
	private static final double maxRotateSpeed = 12; 
	
	public SapperShip(Squad squad, double x, double y) {
		super(squad, x, y, 150);
	}
	
	public SapperShip(World world, double x, double y) {
		super(world, x, y, 150);
	}
	
	public void setStats() {
		setMaxSpeed(12);
		setMaxHp(3600);
		setCapacity(7200);
		setReload(4);
		setActCost(0);
	}
	
	public static double getCost(Squad squad) {
		double cost = 3600;
		if (squad != null)
		return cost * squad.getCostMultiplier();
		return cost;
	}
	
	private Polygon makeFan(double x, double y, double radius, int prongCount, double innerRad) {
		Polygon body = new Polygon();
		body.setFill(getTeam()); 
		body.setStroke(getTeam().darker());
		body.setStrokeWidth(5);
		body.setStrokeLineJoin(StrokeLineJoin.ROUND);
		for (int i = 0; i < prongCount; i++) {
			body.getPoints().addAll(x + innerRad*radius * Math.cos(Math.PI*2 * (double)(i-0.5)/prongCount),
					y + innerRad*radius * Math.sin(Math.PI*2 * (double)(i-0.5)/prongCount),
					x + radius * Math.cos(Math.PI*2 * (double)(i-0.15)/prongCount),
					y + radius * Math.sin(Math.PI*2 * (double)(i-0.15)/prongCount),
					x + radius * Math.cos(Math.PI*2 * (double)(i+0.15)/prongCount),
					y + radius * Math.sin(Math.PI*2 * (double)(i+0.15)/prongCount));
		}
		return body;
	}
	
	private double turbineDist;
	
	public void draw() {
		
		double radius = Math.sqrt((6.28318530718 * Math.pow(getRadius(), 2)/(4.75528258148)));
		
		//double apothem = 0.8 * radius;
		
		Polygon body = makeFan(0, 0, radius, 5, 0.4);
		body.setFill(getTeam()); 
		body.setStroke(getTeam().darker());
		display.getChildren().add(body);
		
		
		Polygon guide = new Polygon();
		guide.setFill(null); 
		guide.setStroke(getTeam().darker());
		guide.setStrokeWidth(15);
		guide.setStrokeLineJoin(StrokeLineJoin.ROUND);
		for (int i = 0; i < 5; i++) {
			guide.getPoints().addAll(-0.9 * radius * Math.cos(Math.PI*2 * (double)i/5),
					0.9 * radius * Math.sin(Math.PI*2 * (double)i/5));
		}
		display.getChildren().add(guide);
		
		Circle circleDecor = new Circle(0, 0, getRadius()*0.5);
		circleDecor.setFill(null);
		circleDecor.setStroke(getTeam().darker());
		circleDecor.setStrokeWidth(15);
		display.getChildren().add(circleDecor);
		
		turbineDist = 0.8*radius;
		
		fans = new ArrayList<Polygon>();
		for (int i = 0; i < 5; i++) {
			Polygon fan = makeFan(turbineDist * Math.cos(Math.PI*2 * (double)(i)/5),
					turbineDist * Math.sin(Math.PI*2 * (double)(i)/5), radius*0.5, 6, 0.2);
			fan.setRotate(rotate);
			fans.add(fan);
			display.getChildren().add(fan);
		}
		
		
		Circle centering = new Circle(0, 0, getRadius()*1.6);
		centering.setOpacity(0);
		display.getChildren().add(centering);
	}
	
	private static final double range = 1200;
	
	public boolean getInActRange() {
		return getWorld().getAllEntities().stream()
				.filter((e)->e instanceof Ship)
				.filter((e)->getDistanceSq(e) < Math.pow(getRadius() + range, 2))
				.map((e)->(Ship)e)
				.anyMatch((e)->!isOnTeam(e.getTeam()) && e.getSquad().getEnergy() >= 1);
	}
	
	private Squad prevTarget;
	
	public void doAction() {
		this.getSquad().addEnergy(101);
		
		rotateSpeed = /*Math.min(maxRotateSpeed,*/ rotateSpeed+Math.max(rotateSpeed/30, 0.5)/*)*/;
		
		if (rotateSpeed == 0) {
			return;
		}
		
		Squad target = getWorld().getAllEntities().stream()
			.filter((e)->(e instanceof Ship))
			.filter((e)->(getDistanceSq(e) < Math.pow(getRadius() + range, 2)))
			.map((e)->(Ship)e)
			.filter((e)->!isOnTeam(e.getTeam()))
			.sorted((e1, e2)->(int)Math.signum(e1.getSquad().getMaterial()-e2.getSquad().getMaterial()))
			.map(Ship::getSquad)
			.distinct()
			.filter((e)->e.getEnergy() >= 1)
			.findFirst().orElse(null);
		
		if (prevTarget != target && rotateSpeed > maxRotateSpeed) {
			rotateSpeed = Math.max(maxRotateSpeed, rotateSpeed*0.4);
		}
		prevTarget = target;
		
		if (target == null) {
			return;
		}
		
		double ratio = rotateSpeed/maxRotateSpeed;
		
		if (Math.random() < ratio && target.getAllShips().size() > 0) {
			if (beams == null) {
				beams = new ArrayList<Beam>();
			}
			Ship shipTarget = target.getAllShips()
					.get((int)(Math.random()*target.getAllShips().size()));
			beams.add(new Beam(shipTarget, (int)(Math.random()*5)));
		}
		
		double amount = 20 * ratio;
		if (!target.removeEnergy(amount)) {
			amount = target.getEnergy();
			target.removeEnergy(target.getEnergy());
		};
		double penalty = 1/8;
		if (this.getPowerBoostTime() > 0) {
			penalty /= 2;
		}
		getSquad().addEnergy(amount * (1-penalty));
		double capac = getSquad().getCapacity();
		double inventory = getSquad().getEnergy()+getSquad().getMaterial();
		if (capac < inventory) {
			getSquad().removeEnergy(inventory - capac);
			//takeDamage(inventory - capac);
			if (Math.random() < 0.2) {
				double dir = Math.random()*Math.PI*2;
				double len = Math.random()*getRadius();
				double xShift = Math.cos(dir)*len;
				double yShift = Math.sin(dir)*len;
				new KamikazeExplosion(getWorld(), getX()+xShift*2, getY()+yShift*2, getRadius()/3,
						0, Color.rgb(255, 127, 127));
			}
		}
		
	}
	
	public void die() {
		super.die();
		if (beams != null) {
			for (Beam b : beams) {
				b.remove();
			}
		}
	}
	
	public void run() {
		super.run();
		
		rotateSpeed = Math.max(0, rotateSpeed-Math.max(0.1, rotateSpeed/120));
		rotate += Math.min(rotateSpeed, maxRotateSpeed);
		if (fans != null) {
			for (Polygon f : fans) {
				f.setRotate(rotate/*-this.getRotate()/Math.PI*180*/);
			}
		}
		
		if (beams != null) {
			for (Beam b : beams) {
				b.run();
			}
			beams.removeIf((b)->b.progress >= 1.2);
		}
	}
	
	private List<Beam> beams;
	
	private double[] getTurbinePos(int num) {
		double[] out = {0, 0};
		double totalRotate = Math.PI*2 * (double)(num)/5 +
				rotate * Math.PI/180 + getRotate();
		out[0] = getX() + turbineDist * Math.cos(totalRotate);
		out[1] = getY() + turbineDist * Math.sin(totalRotate);
		
		return out;
	}
	
	private class Beam {
		Entity from;
		int to;
		double progress = 0;
		Line display;
		
		Beam (Entity from, int to){
			this.from = from;
			this.to = to;
			
			this.display = new Line();
			display.setStroke(Color.rgb(255, 127, 127));
			display.setStrokeWidth(10);
			display.setStrokeLineCap(StrokeLineCap.ROUND);
			display.setOpacity(0.5);
			updateLine();
			List<Node> nodeList = getWorld().getDisplay().getChildren();
			nodeList.add(0, display);
		}
		
		double[] progressToPoint(double p) {
			p = Math.min(Math.max(p, 0), 1);
			
			double[] out = {0,0};
			
			double startX = from.getX();
			double startY = from.getY();
			double[] end = getTurbinePos(to);
			
			out[0] = startX * (1-p) + end[0]*p;
			out[1] = startY * (1-p) + end[1]*p;
			return out;
		}
		
		void updateLine() {
			double[] startcoords = progressToPoint(progress-0.2);
			display.setStartX(startcoords[0]);
			display.setStartY(startcoords[1]);
			double[] endcoords = progressToPoint(progress);
			display.setEndX(endcoords[0]);
			display.setEndY(endcoords[1]);
		}
		
		void remove() {
			getWorld().getDisplay().getChildren().remove(display);
		}
		
		void run (){
			progress += 0.1;
			updateLine();
			
			if (progress >= 1.2) {
				remove();
			}
		}
	}
	
}
