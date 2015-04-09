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
	private ListView queue, trooms, treatment_room_list, on_call_list;
	
	@FXML
	private Button UPGRADE;
	
	private final ObservableList QList = FXCollections.observableArrayList();
	private final ObservableList trList = FXCollections.observableArrayList();
	private final ObservableList trno = FXCollections.observableArrayList();
	private final ObservableList ocu = FXCollections.observableArrayList();
	
	private String[] array = new String[10];
	private String[] array1 = new String[4];
	private String[] onCall = new String[1];
	private String[] treatmentRoomNum = new String[4];

	
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
		onCall[0] = "On Call Unit";
		treatmentRoomNum[0] = "Treatment Room 1";
		treatmentRoomNum[1] = "Treatment Room 2";
		treatmentRoomNum[2] = "Treatment Room 3";
		treatmentRoomNum[3] = "Treatment Room 4";
		
		trno.addAll(treatmentRoomNum);
		ocu.addAll(onCall);
		QList.addAll(array);
		trList.addAll(array1);
		queue.setItems(QList);
		treatment_room_list.setItems(trno);
		on_call_list.setItems(ocu);
	
		// DEMO Purposes only
		// Select a name
		// Click 'Tick' Button
		// See the selected name replace another in the trList
		UPGRADE.setOnAction(e -> {String potential = (String) queue.getSelectionModel().getSelectedItem();
	      if (potential != null) {
	        queue.getSelectionModel().clearSelection();
	        QList.remove(potential);
	        trList.remove(0);
	        trList.add(3, "");
	        trList.add(3, potential);}});
		
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
