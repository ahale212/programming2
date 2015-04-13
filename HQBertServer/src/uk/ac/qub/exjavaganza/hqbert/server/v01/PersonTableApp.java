package uk.ac.qub.exjavaganza.hqbert.server.v01;
 

import java.sql.Connection;
import java.sql.DriverManager;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Person;
import javafx.application.Application ;
import javafx.scene.control.TableView ;
import javafx.scene.control.TableColumn ;
import javafx.scene.control.cell.PropertyValueFactory ;
import javafx.scene.layout.BorderPane ;
import javafx.scene.Scene ;
import javafx.stage.Stage ;

public class PersonTableApp extends Application {
	
	
    private PersonDataAccessor dataAccessor ;
 // establish connection to mySQl
	String url = "jdbc:mysql://web2.eeecs.qub.ac.uk/40058483";
	Connection con;
    @SuppressWarnings("unchecked")
	@Override
    public void start(Stage primaryStage) throws Exception {
    	dataAccessor = new PersonDataAccessor(url, "40058483", "VPK7789");
    	
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

        TableView<Person> patientsTable = new TableView<>();
        TableColumn<Person, String> Title = new TableColumn<>("Title");
        Title.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Person, String> NHSNum = new TableColumn<>("NHSNum");
        NHSNum.setCellValueFactory(new PropertyValueFactory<>("NHSNum"));
        TableColumn<Person, String> firstName = new TableColumn<>("First Name");
        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        TableColumn<Person, String> lastName = new TableColumn<>("Last Name");
        lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        TableColumn<Person, String> DOB = new TableColumn<>("DOB");
        DOB.setCellValueFactory(new PropertyValueFactory<>("DOB"));
        TableColumn<Person, String> Address = new TableColumn<>("Address");
        Address.setCellValueFactory(new PropertyValueFactory<>("address"));
        TableColumn<Person, String> City = new TableColumn<>("City");
        City.setCellValueFactory(new PropertyValueFactory<>("city"));
        TableColumn<Person, String> Country = new TableColumn<>("Country");
        Country.setCellValueFactory(new PropertyValueFactory<>("country"));
        TableColumn<Person, String> Telephone = new TableColumn<>("Telephone");
        Telephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        TableColumn<Person, String> Allergies = new TableColumn<>("Allergies");
        Allergies.setCellValueFactory(new PropertyValueFactory<>("allergies"));
        TableColumn<Person, String> BloodGroup = new TableColumn<>("BloodGroup");
        BloodGroup.setCellValueFactory(new PropertyValueFactory<>("bloodGroup"));
        
        // adds all info to the table, wont work now changed the query
        patientsTable.getColumns().addAll(NHSNum,Title,firstName, lastName, DOB, Address, City, Country, Allergies, BloodGroup);

      //used to get the items for demo purposes
       patientsTable.getItems().addAll(dataAccessor.personList());
       //fx set up
        BorderPane root = new BorderPane();
        root.setCenter(patientsTable);
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


