package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class TriageController  implements Initializable  {
	
	@FXML
	private TextField name;
	
	@FXML
	private ListView listView;
	
	@FXML
	private Button addButton, ALERTButton;
	
	private final ObservableList currentList = FXCollections.observableArrayList(); 

	@Override
	public void initialize(URL fxmlFilelocation, ResourceBundle resources) {
		
		addButton.setOnAction(e -> {currentList.add(name.getText()); name.clear();});
		listView.setItems(currentList);
		
		ALERTButton.setOnAction(e -> AlertBox.display("Hospital Full!", "Alert Other Hospitals!!!"));
		
	}

}
