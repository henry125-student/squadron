package entity;

import javafx.scene.paint.Color;

public interface HasTeam {
	
	public Color getTeam();
	
	public default boolean isOnTeam(Color team) {
		if (getTeam().equals(team)) {
			return true;
		}
		return false;
	}
	
}
