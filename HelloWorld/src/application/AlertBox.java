package application;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox {
	
	static Stage window;
	static Scene scene;
	static Label lb1;
	static Button bt1;
	static VBox v1;

	public static void display(String title, String message) {
		window = new Stage();
		
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Alert");
		window.setMinWidth(250);
		
		lb1 = new Label();
		lb1.setText("Close");
		
		bt1 = new Button("Close");
		bt1.setOnAction(e -> window.close());
		
		v1 = new VBox(10);
		v1.getChildren().addAll(lb1,bt1);
		v1.setAlignment(Pos.CENTER);
		
		scene = new Scene(v1);
		window.setScene(scene);
		window.showAndWait();
	}

}
