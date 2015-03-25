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

public class RevController implements Initializable {
	
	@FXML
	private ListView queue, trooms;
	
	@FXML
	private Button RunLists;
	
	private final ObservableList QList = FXCollections.observableArrayList();
	private final ObservableList trList = FXCollections.observableArrayList();
	
	private String[] array = new String[10];
	private String[] array1 = new String[4];
	
	@Override
	public void initialize(URL fxmlFilelocation, ResourceBundle resources) {
		array[0] = "Jim";
		array[1] = "Mary";
		array[2] = "Illy";
		array[3] = "OMM";
		array[4] = "B3B4";
		array[5] = "A";
		array[6] = "JHSjhd";
		array[7] = "0987";
		array[8] = "bn67BNhgg";
		array[9] = "Kim";
		array1[0] = "JimJonJoe";
		array1[1] = "JonJoe";
		array1[2] = "Joe";
		array1[3] = "JJJoe";
		
		QList.addAll(array);
		trList.addAll(array1);
		queue.setItems(QList);
		
		// DEMO Purposes only
		// Select a name
		// Click 'Tick' Button
		// See the selected name replace another in the trList
		RunLists.setOnAction(e -> {String potential = (String) queue.getSelectionModel().getSelectedItem();
	      if (potential != null) {
	        queue.getSelectionModel().clearSelection();
	        QList.remove(potential);
	        trList.remove(0);
	        trList.add(trList.size()-1, potential);}});
		
		trooms.setItems(trList);
		
	}
	
	public void tickDown(){
		
		for (int i = 0; i < 1; i++) {
			queue.setItems(QList);
			QList.remove(0);
			Thread t = new Thread();
			try {
				t.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
