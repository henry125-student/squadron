package game;

import entity.*;
import javafx.geometry.VPos;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import ship.*;
import java.util.*;

public class Overlay {
	private World world;
	private Group display;
	
	private Polygon storageIndicator = new Polygon();
	private Text materialText = new Text();
	private Text energyText = new Text();
	private Polygon storageRuleIndicator = new Polygon();
	
	
	private ArrayList<String> vBarValues = new ArrayList<String>();
	private Group vBar = new Group();
	private double vBarFillNum = 0;
	private Rectangle vBarFill;
	private final double VBAR_LENGTH = 300;
	
	public Overlay(World world) {
		this.world = world;
		this.display = world.getOverDisplay();
		
		
		for (int i = 0; i < 6; i++) {
			storageIndicator.getPoints().addAll(
					60 + 50 * Math.cos(i * Math.PI / 3 + Math.PI/2), 
					60 + 50 * Math.sin(i * Math.PI / 3 + Math.PI/2) );
		}
		
		storageIndicator.setStrokeWidth(2);
		storageIndicator.setStrokeLineJoin(StrokeLineJoin.ROUND);
		storageIndicator.setVisible(false);
		display.getChildren().add(storageIndicator);
		
		for (int i = 0; i < 6; i++) {
			storageRuleIndicator.getPoints().addAll(
					95 + 15 * Math.cos(i * Math.PI / 3 + Math.PI/2), 
					95 + 15 * Math.sin(i * Math.PI / 3 + Math.PI/2) );
		}
		
		materialText.setLayoutX(10);
		materialText.setWrappingWidth(100);
		materialText.setLayoutY(60);
		materialText.setText("test");
		materialText.setFill(Color.rgb(0,0,192));
		materialText.setTextAlignment(TextAlignment.CENTER);
		materialText.setFont(Font.font(20));
		materialText.setTextOrigin(VPos.BOTTOM);
		materialText.setVisible(false);
		display.getChildren().add(materialText);
		
		energyText.setLayoutX(10);
		energyText.setWrappingWidth(100);
		energyText.setLayoutY(60);
		energyText.setText("test");
		energyText.setFill(Color.rgb(192,0,0));
		energyText.setTextAlignment(TextAlignment.CENTER);
		energyText.setFont(Font.font(20));
		energyText.setTextOrigin(VPos.TOP);
		energyText.setVisible(false);
		display.getChildren().add(energyText);
		
		storageRuleIndicator.setStrokeWidth(2);
		storageRuleIndicator.setStrokeLineJoin(StrokeLineJoin.ROUND);
		storageRuleIndicator.setVisible(false);
		display.getChildren().add(storageRuleIndicator);
		
		Rectangle vBarOutline = new Rectangle(0, 0, VBAR_LENGTH, 25);
		vBarOutline.setStroke(Color.WHITE);
		vBarOutline.setStrokeWidth(2);
		vBarOutline.setStrokeLineJoin(StrokeLineJoin.ROUND);
		vBar.getChildren().add(vBarOutline);
		
		vBarFill = new Rectangle(0, 0, 1, 25);
		vBarFill.setStroke(Color.GREY.darker());
		vBarFill.setFill(Color.GREY);
		vBarFill.setStrokeWidth(2);
		vBarFill.setStrokeLineJoin(StrokeLineJoin.ROUND);
		vBar.getChildren().add(vBarFill);
		
		display.getChildren().add(vBar);
	}
	
	public void updateStorageIndicator() {
		Entity subject = world.getFocus();
		if (subject instanceof Squad && !subject.getIsDead()) {
			storageIndicator.setVisible(true);
			
			Squad subject2 = (Squad)subject;
			Color storageColor = Color.rgb(
					Math.max(127, Math.min(255, 127 + (int)(128 * subject2.getEnergy()/subject2.getCapacity()))),
					127, 
					Math.max(127, Math.min(255, 127 + (int)(128 * subject2.getMaterial()/subject2.getCapacity())))
					);
			
			storageIndicator.setFill(storageColor); 
			storageIndicator.setStroke(storageColor.darker());
			
			materialText.setVisible(true);
			energyText.setVisible(true);
			materialText.setText((int)subject2.getMaterial()+"");
			energyText.setText((int)subject2.getEnergy()+"");
			
			storageRuleIndicator.setVisible(true);
			
			Color storageRuleColor;
			switch(subject2.getStorageRule()) {
			case 0:
			default:
				storageRuleColor = Color.rgb(127, 127, 127);
				break;
			case 1:
				storageRuleColor = Color.rgb(127, 127, 255);
				break;
			case 2:
				storageRuleColor = Color.rgb(191, 127, 191);
				break;
			case 3:
				storageRuleColor = Color.rgb(255, 127, 127);
				break;
			}
			
			storageRuleIndicator.setFill(storageRuleColor); 
			storageRuleIndicator.setStroke(storageRuleColor.darker());
		} else {
			storageIndicator.setVisible(false);
			storageRuleIndicator.setVisible(false);
			materialText.setVisible(false);
			energyText.setVisible(false);
		}
	}
	
	private boolean vBarFillUp = true;
	public void incrementVBar() {
		if (vBarFillUp) {
			vBarFillNum += 0.05;
			if (vBarFillNum >= 1) {
				vBarFillNum = 1;
				vBarFillUp = false;
			}
		} else {
			vBarFillNum -= 0.05;
			if (vBarFillNum <= 0) {
				vBarFillNum = 0;
				vBarFillUp = true;
			}
		}
		vBarFill.setWidth(vBarFillNum * VBAR_LENGTH); 
	}
	
	public void addVBarString(String string) {
		if (!vBarValues.contains(string)) {
			vBarValues.add(string);
		}
	}
	public double getVBarValue(String string) {
		if (vBarValues.contains(string)) {
			vBarValues.remove(string);
			return vBarFillNum;
		}
		return 0;
	}
	
	public void update() {
		updateStorageIndicator();
		
		if (vBarValues.size() > 0) {
			vBar.setVisible(true);
			incrementVBar();
			vBar.setLayoutX(world.getScene().getWidth() / 2 - VBAR_LENGTH/2);
			vBar.setLayoutY(world.getScene().getHeight() * 4 / 5);
		} else {
			vBar.setVisible(false);
			vBarFillNum = 0;
		}
	}
}
