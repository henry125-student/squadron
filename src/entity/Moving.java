package entity;

/**
 * Important note:
 * Either getVelocityX() and getVelocityY(), 
 * or getDirection() and getSpeed() MUST be overwritten.
 * They are designed so that one pair of methods will cover the other if those are redefined.
 * 
 */
public interface Moving {
	
	public default double getVelocityX() {
		return getSpeed()*Math.cos(getDirection());
	};
	
	public default double getVelocityY() {
		return getSpeed()*Math.sin(getDirection());
	};
	
	/**
	 * (in radians)
	 */
	public default double getDirection() {
		if (getVelocityY() < 0 ||
				(getVelocityY() == 0 && getVelocityX() < 0)) {
			return Math.atan(getVelocityY()/getVelocityX()) + Math.PI;
		} else if (getVelocityY() == 0 && getVelocityX() == 0){
			return 0;
		} else {
			return Math.atan(getVelocityY()/getVelocityX());
		}
	}
	
	public default double toDirection(double xVelocity, double yVelocity) {
		if (xVelocity < 0 && yVelocity < 0){
			return Math.atan(yVelocity/xVelocity) - Math.PI;
		} else if (xVelocity < 0 /*||
				(xVelocity == 0 && yVelocity < 0)*/) {
			return Math.atan(yVelocity/xVelocity) + Math.PI;
		} else if (xVelocity == 0 && yVelocity == 0){
			return getDirection();
		} else {
			return Math.atan(yVelocity/xVelocity);
		}
	}
	
	public default double getSpeed() {
		return Math.sqrt(Math.pow(getVelocityX(), 2) + Math.pow(getVelocityY(), 2));
	}
	
	public void shiftX(double newVal);
	public void shiftY(double newVal);
	
	public default void move() {
		shiftX(getVelocityX());
		shiftY(getVelocityY());
	}
	
}
