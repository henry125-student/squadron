package entity;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineJoin;
import game.World;

public class Resource extends Entity implements Damagable, Moving, HasValue{
	static final double INTOPIX = 2500; //value of 1 unit of "size"
	
	private double rotateSpeed = (Math.random() * 2 - 1) * Math.PI / 180;
	
	private int material, energy, junk;
	private double hp;
	
	private double direction = Math.random()*2*Math.PI, speed = 7 * Math.random();
	private boolean isMoving = true;
	
	private int expireTimer = (int)(500 * Math.random()) + 500;
	
	public Resource(World world, double x, double y, int m, int e, int j) {
		super(world, x, y, Math.sqrt((m+e+j) * INTOPIX / Math.PI));
		material = m;
		energy = e;
		junk = j;
		getSize();
		
		makeDisplay();
		setUpTangable();
	}
	
	public Resource(World world, double x, double y, int size) {
		super(world, x, y, Math.sqrt(size * INTOPIX / Math.PI));
		
		double[] rnpa = {Math.random(),Math.random(),
				Math.random()+0.1}; //random number processing array
		double sum = rnpa[0]+rnpa[1]+rnpa[2];
		for (int i = 0; i < 3; i++) {
			rnpa[i] = (int)(rnpa[i]/sum * size);
		}
		while (rnpa[0]+rnpa[1]+rnpa[2] < size) {
			rnpa[(int)(Math.random()*3)]++;
		}
		material = (int)rnpa[0]; energy = (int)rnpa[1]; junk = (int)rnpa[2];
		
		makeDisplay();
		setUpTangable();
		
	}
	
	private void setUpTangable() {
		if (getSize() == 1 && junk == 0) {
			setIsTangable(false);
			
			/*Arc outline = new Arc(0, 0, getRadius()*2, getRadius()*2, 0, 360);
			outline.setStroke(Color.WHITE);
			outline.setFill(null);
			outline.setStrokeWidth(3);
			getDisplay().getChildren().add(outline);*/
		}
	}
	
	public Resource(World world, double x, double y, int size, boolean isMoving) {
		this(world, x, y, size);
		this.isMoving = isMoving;
	}
	
	public void makeDisplay() {
		hp = getSize()*25.0;
		
		Color color = Color.rgb(127 + (int)(128 * (double)energy/getSize()),
				127, 127 + (int)(128 * (double)material/getSize()));
		
		int sides;
		if (getSize() == 1) { 
			sides = 5;
			if (material == 1) {
				sides = 4;
			} else if (energy == 1) {
				sides = 3;
			}
		} else {
			sides = 3 + (int)( (getRadius() * 2*Math.PI)/ 500
					* (Math.random() + 1.0));
		} 
		double radius = Math.sqrt(
				(2 * Math.PI * Math.pow(getRadius(), 2))/
				(sides * Math.sin(2* Math.PI / sides)));
		Polygon p = new Polygon();
		p.setFill(color); p.setStroke(color.darker());
		p.setStrokeWidth(5);
		p.setStrokeLineJoin(StrokeLineJoin.ROUND);
		for (int i = 0; i < sides; i++) {
			p.getPoints().addAll(radius * Math.cos(Math.PI*2 * (double)i/sides),
					radius * Math.sin(Math.PI*2 * (double)i/sides));
		}
		display.getChildren().add(p);
		
	}
	
	public int getSize() {
		return material+energy+junk;	
	}
	public double getHp() {
		return hp;
	}
	public void setHp(double newVal) {
		hp = newVal;
	}
	public Color getTeam() {
		return Color.rgb(127, 127, 127);
	}
	
	public void beDestroyed() {
		//memes lol
		/*if (Math.random() < 0.01) {
			double value = getSize()*100;
			while (value >= 75) {
				value -= 75;
				new KamikazeActive(world, getX(), getY(), 50, Math.random()*Math.PI*2,
						50, null, 75, Color.WHITE);
			}
			die();
			return;
		}*/
		
		if (getSize() > 1) {
			int parts = (int)(Math.random() * 3 + 3);
			double[][] rnpa = new double[3][parts]; //random number processing array
			for (int i = 0; i < 3; i++) {
				int total;
				switch(i) {
				case 0: total = material; break;
				case 1: total = energy; break;
				default: total = junk;
				}
				if (total == 0) {
					for (int j = 0; j < parts; j++) {
						rnpa[i][j] = 0;
					}
				} else {
					for (int j = 0; j < parts; j++) {
						rnpa[i][j] = Math.random()+0.2;
					}
					double sum = 0;
					for (int j = 0; j < parts; j++) {
						sum += rnpa[i][j];
					}
					if (sum == 0) {
						rnpa[i][0] = 1; sum = 1;
					}
					for (int j = 0; j < parts; j++) {
						rnpa[i][j] = (int)(rnpa[i][j]/sum * total);
					}
					sum = 0;
					for (int j = 0; j < parts; j++) {
						sum += rnpa[i][j];
					}
					int cycle = (int)(Math.random()*parts);
					while (sum < total) {
						rnpa[i][cycle]++;
						cycle = (cycle+1)%parts;
						sum++;
					}
				}
			}
			for (int i = 0; i < parts; i++) {
				if (rnpa[0][i] + rnpa[1][i] + rnpa[2][i] != 0) {
					Moving e = new Resource(world, getX(), getY(), 
							(int)rnpa[0][i], (int)rnpa[1][i], (int)rnpa[2][i]);
					e.move();
				}
			}
		}
		die();
	}
	
	/**
	 * returns 1 for single material, 2 for single energy, 0 otherwise
	 */
	public int getType() {
		if (getSize() == 1) {
			if (material == 1) {
				return 1;
			} else if (energy == 1) {
				return 2;
			}
		}
		return 0;
	} 
	
	public double getValue() {
		return getSize() * 100 * Math.ceil(Math.log(getSize()) / Math.log(4));
	}
	
	public double getMaterial() {
		return material;
	}
	public double getEnergy() {
		return energy;
	}
	
	public double getDirection() {
		return direction;
	}
	public double getSpeed() {
		return speed;
	}
	
	public void hastenExpire(int amount) {
		expireTimer -= amount;
	}
	
	public void run() {
		shiftRotate(rotateSpeed);
		
		if (isMoving) {
			move();
			speed *= 0.99;
		}
		
		if (getSize() == 1) {
			expireTimer--;
			if (expireTimer <= 0) {
				die();
			}
		}
		
		//takeDamage(1);
	}
}
