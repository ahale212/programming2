package uk.ac.qub.exjavaganza.hqbert.server.v01;
 

import java.sql.Connection;
import java.sql.DriverManager;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Staff;
import javafx.application.Application ;
import javafx.scene.control.TableView ;
import javafx.scene.control.TableColumn ;
import javafx.scene.control.cell.PropertyValueFactory ;
import javafx.scene.layout.BorderPane ;
import javafx.scene.Scene ;
import javafx.stage.Stage ;

public class PersonTableApp extends Application {
	
	
    private StaffDataAccessor dataAccessor ;
 // establish connection to mySQl
	String url = "jdbc:mysql://web2.eeecs.qub.ac.uk/40058483";
	Connection con;
    @SuppressWarnings("unchecked")
	@Override
    public void start(Stage primaryStage) throws Exception {
    	dataAccessor = new StaffDataAccessor(url, "40058483", "VPK7789");
    	
    	// Loading the driver
    			try {
    				Class.forName("com.mysql.jdbc.Driver");
    				// catch exceptions
    			} catch (java.lang.ClassNotFoundException e) {
    				System.err.print("ClassNotFoundException: ");
    				// get error message
    				System.err.println(e.getMessage());
    			}
    			
    			con = DriverManager.getConnection(url, "40058483", "VPK7789");
        
    			// all demo test to make sure it ran, makes its own fx

        TableView<Staff> staffTable = new TableView<>();
        TableColumn<Staff, String> employeeNumber = new TableColumn<>("EmployeeNum");
        employeeNumber.setCellValueFactory(new PropertyValueFactory<>("employeeNum"));
        TableColumn<Staff, String> firstName = new TableColumn<>("First Name");
        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        TableColumn<Staff, String> lastName = new TableColumn<>("Last Name");
        lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        TableColumn<Staff, String> username = new TableColumn<>("Username");
        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<Staff, String> password = new TableColumn<>("Password");
        password.setCellValueFactory(new PropertyValueFactory<>("password"));
        TableColumn<Staff, String> email = new TableColumn<>("Email");
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
       
        
        // adds all info to the table, wont work now changed the query
        staffTable.getColumns().addAll(employeeNumber,firstName, lastName, username, password, email);
        //staffTable.getItems().addAll(dataAccessor.staffList(employeeNumber, firstName, lastName, username, password, email));
      //used to get the items for demo purposes
       //staffTable.getItems().addAll(dataAccessor.staffList(url, url, url, url, url, url));
       //fx set up
        BorderPane root = new BorderPane();
        root.setCenter(staffTable);
        Scene scene = new Scene(root, 1000, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
    
    @Override
    public void stop() throws Exception {
        if (dataAccessor != null) {
            dataAccessor.shutdown();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}


