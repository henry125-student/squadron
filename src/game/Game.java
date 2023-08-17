package game;

import java.awt.Taskbar;
import java.awt.Taskbar.Feature;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.util.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Group; 
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import javafx.event.*;

class Runner extends TimerTask{
	World world;
	public Runner(World world) {this.world = world;}
	
	public void run() {
		Platform.runLater(new Runnable() {
            public void run() {
                world.run();
            }
        });
	}
}
public class Game extends Application{
	Timer timer; World world;
	
	public void start(Stage primaryStage) throws Exception {
		
		Group root = new Group(); 
	    Scene scene = new Scene(root ,600, 600);
	    scene.setFill(Color.BLACK);
	    
	    primaryStage.setTitle("Squadron"); 
	    primaryStage.setScene(scene); 
	    
	    Image ico16 = new Image(new FileInputStream("img/icon16.png"));
	    Image ico32 = new Image(new FileInputStream("img/icon32.png"));
	    Image ico64 = new Image(new FileInputStream("img/icon64.png"));
	    primaryStage.getIcons().addAll(ico32, ico64, ico16);
	    
	    primaryStage.show(); 
	    
	    world = new World(scene, this);
	    
	    timer = new Timer();
	    timer.schedule(new Runner(world), 0, 20);
	    
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				timer.cancel();
	     	}
	 	});
		
	}
	
	double rate = 1;
	
	public void setGameSpeed(double rate) {
		if (this.rate == rate) return;
		
		if (this.rate != 0) {
			timer.cancel();
			timer = new Timer();
		}
		if (rate != 0) {
			long period = (long)(20/rate);
			timer.schedule(new Runner(world), 0, period);
		}
		
		this.rate = rate;
	}
	
	public static void main(String[] args) {
		if (Taskbar.isTaskbarSupported()) {
            var taskbar = Taskbar.getTaskbar();

            if (taskbar.isSupported(Feature.ICON_IMAGE)) {
                final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                var dockIcon = defaultToolkit.getImage("img/icon128.png");
                taskbar.setIconImage(dockIcon);
            }

        }
		
		launch(args);
	}

}
