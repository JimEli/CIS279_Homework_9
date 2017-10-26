/*************************************************************************
 * Title: Employee Table View 
 * File: EmployeeTableView.java
 * Author: James Eli
 * Date: 2/23/2017
 *
 * This JavaFX class provides functions for viewing a table of
 * employee data held in a MySQL database using a MVC/MVP type
 * architecture. 
 * 
 * The MySQL DB department table is missing item #5. This created 
 * difficulties for the combobox and array alignment. Additionally, 
 * the MySQL stored procedure returns the department fields in 
 * alphabetical order. These issues necessitated my use of the
 * List<Pair> and sorting inside the getDepartments() method. 
 * 
 * Better crafted MySQL queries could have eliminated the need for the 
 * funky getJobTypes() and getPayFrequencies() methods. However, adding
 * this functionality was good practice.
 * 
 * For additional programming experience I made the first name column
 * editable, and provided a rudimentary database update method. 
 * 
 * MySQL user id and password can be hard-coded into the program below.
 * However, for obvious security reasons this should not be done. 
 *
 * For further information, see the files: 
 *   EmployeeTableViewUtility.java
 *   Employee.java
 * 
 * Notes: 
 *   (1) Requires MySQL Employeedb database.
 *   (2) Ensure the database URL, userid and password are entered 
 *   appropriately.
 *   (3) Compiled with java:
 *      (a) SE JDK 8, Update 131 (JDK 8u131)
 *      (b) JavaFX version 8.0.121-b13
 *      (c) Java-MySQL connector version 5.1.40
 *   
 * Submitted in partial fulfillment of the requirements of PCC CIS-279.
 *************************************************************************
 * Change Log:
 *   02/23/2017: Initial release. JME
 *   03/05/2017: Made first name column editable. JME
 *   04/28/2017: Changed login functionality to allow 3 attempts. JME
 *************************************************************************/
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

@SuppressWarnings( "unchecked" )
public class EmployeeTableView extends Application {
  // Default url, user id and password. (during development append to database: "?verifyServerCertificate=false&useSSL=true")
  private final String url = new String( "jdbc:mysql://localhost:3306/employeedb" );
  private String userId = new String( "" ); // MySQL database user id.
  private String password = new String( "" );   // MySQL database user password.
  private static List<Pair<Integer, String>> departments = new ArrayList<>(); // List of departments.
  private static int MAXIMUM_LOGIN_ATTEMPTS = 3; // Maximum number of failed MySQL database login attempts before termination.
  
  /***************************************************************
   * JavaFX application start method. 
   **************************************************************/
  @Override
  public void start( final Stage stage ) {
    // Create a TableView with a list of Employees.
    final TableView<Employee> table = new TableView<>();

    Optional<Pair<String, String>> login;
    if ( !EmployeeTableViewUtility.openDB( url, userId, password ) ) {
      int loginAttempts=1;
      while( true ) {
        // Ask user for id/password, attempt access database.
        login = loginDialog( userId, password );
        if ( !login.isPresent() ) {
          // User cancelled/closed login dialog.
  	      alertDialog( "Program Termination", "User requested to cancel." );
          System.exit( 0 );
        }
        // Fetch id & password.
        login.ifPresent( loginPair -> {
          userId = loginPair.getKey();
  	      password = loginPair.getValue();
        });
        // Attempt to open db.
        if ( !EmployeeTableViewUtility.openDB( url, userId, password ) ) {
          // Login failed, clear password.
          password = ""; 
          if ( ++loginAttempts > MAXIMUM_LOGIN_ATTEMPTS ) {
            // Exceeded maximum number of incorrect login attempts.
  	  	    alertDialog( "Database Access Error", "Cannot open mySQL database!" );
            System.exit( 0 );
          }
        } else
          break; // MySQL login was successful.
      }
    }

    // Get all departments, job type descriptions and pay frequency descriptions from the DB.
    departments = EmployeeTableViewUtility.getDepartments();
    if ( !EmployeeTableViewUtility.getJobTypes() ) {
      // Failure loading job types.
	  alertDialog( "Database Access Error", "Cannot locate job types inside mySQL database!" );
      System.exit( 0 );
    }
    if ( !EmployeeTableViewUtility.getPayFrequencies() ) {
      // Failure loading pay frequencies.
  	  alertDialog( "Database Access Error", "Cannot locate pay frequencies inside mySQL database!" );
      System.exit( 0 );
    }

    // Customize TableView.
    table.setPlaceholder( new Label( "Select a department above to view data." ) );
    table.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
    // Make the table editable.
	table.setEditable( true );
    // Add columns to the TableView, in correct order, with First Name Column as editable.
    table.getColumns().addAll( 
      EmployeeTableViewUtility.getIdColumn(), 
      EmployeeTableViewUtility.getLastNameColumn()
    );
	EmployeeTableViewUtility.addFirstNameColumn( table ); // Editable column.
    table.getColumns().addAll( 
      EmployeeTableViewUtility.getJobTypeColumn(),
      EmployeeTableViewUtility.getPayColumn(),
      EmployeeTableViewUtility.getPayFrequencyColumn(),
      EmployeeTableViewUtility.getEmailColumn(),
      EmployeeTableViewUtility.getTelephoneColumn()
    );

    // Set combobox label.
    Label lblDepartments = new Label( "Department:" );
    // Setup combobox.
    final ComboBox<String> cbDeptSelector = new ComboBox<>(); 
    for ( Pair<Integer, String> p : departments ) {
      try {
        cbDeptSelector.getItems().add( p.getValue().toString() );
      } catch ( Exception ex ) {
        // Throw up an error dialog and terminate program.
        alertDialog( "Data Error", "Department list ComboBox data corrupted!" );
        Platform.exit();
      }
    }
   	// Listener fires when combobox is changed.
    cbDeptSelector.setOnAction( e -> { 
      table.getItems().clear();
      int selection = departments.get( cbDeptSelector.getSelectionModel().selectedIndexProperty().getValue() ).getKey();
      // Retrieve appropriate list of employees.
      ObservableList<Employee> employeeList = EmployeeTableViewUtility.EmployeeGetList( selection );
      // Fill table with list.
      table.getItems().addAll( employeeList );
    } );

    // Cancel button.
    Button btCancel = new Button( "_Cancel" );
    // Register cancel button event.
    btCancel.setOnAction( event -> Platform.exit() );

    // Add nodes to first HBox pane.
    HBox hBox1 = new HBox( lblDepartments, cbDeptSelector );
    hBox1.setSpacing( 10 );
    hBox1.setAlignment( Pos.CENTER );
    // Add nodes to second HBox pane.
    HBox hBox2 = new HBox( btCancel );
    hBox2.setAlignment( Pos.BOTTOM_RIGHT );
    // Add nodes to VBox pane.
    VBox vBox = new VBox( 10 );
    vBox.getChildren().addAll( hBox1, table, hBox2 );
    vBox.setAlignment( Pos.CENTER );

    // Add bling.
    vBox.setStyle( 
      "-fx-padding: 10;" + 
      "-fx-border-style: solid inside;" + 
      "-fx-border-width: 2;" +
      "-fx-border-insets: 5;" + 
      "-fx-border-radius: 5;" + 
      "-fx-border-color: blue;"
    );

    Scene scene = new Scene( vBox );
    stage.setScene( scene );
    stage.setTitle( "Employee TableView" );
    stage.show();
  }

  /***************************************************************
   * Display alert dialog with supplied text.
   **************************************************************/
  public static void alertDialog( final String title, final String alertMessage ) {
    // Throw up an error dialog.
    Alert alert = new Alert( AlertType.ERROR );
    alert.setTitle( title );
    alert.setHeaderText( null );
    alert.setContentText( alertMessage );
    alert.showAndWait();
  }

  /***************************************************************
   * Login dialog. Will show passed id & masked password
   **************************************************************/
  private final Optional<Pair<String, String>> loginDialog( final String id, final String pwd) {
    // Create a custom dialog.
    final Dialog<Pair<String, String>> dialog = new Dialog<>();
    dialog.setTitle( "MySQL Login" );
    dialog.setHeaderText( null );

    // Set the button types.
    ButtonType btnOk = new ButtonType( "Login", ButtonData.OK_DONE );
    dialog.getDialogPane().getButtonTypes().addAll( btnOk, ButtonType.CANCEL );

    // Create the username and password labels and fields.
    GridPane grid = new GridPane();
    grid.setHgap( 10 );
    grid.setVgap( 10 );
    TextField tfUserName = new TextField();
    if ( id.length() != 0 )
      tfUserName.setText( id );
    else
      tfUserName.setPromptText( "root" );
    PasswordField tfPassword = new PasswordField();
    if ( pwd.length() != 0 )
      tfPassword.setText( pwd );
    grid.add( new Label( "Username:" ), 0, 0 );
    grid.add( tfUserName, 1, 0 );
    grid.add( new Label( "Password:" ), 0, 1 );
    grid.add( tfPassword, 1, 1 );

    // Disable login button until username/password entered.
    Node btLogin = dialog.getDialogPane().lookupButton( btnOk );
    btLogin.setDisable( true );
    // Wait for data to be entered.
    tfUserName.textProperty().addListener( ( obs, oldValue, newValue ) -> {
      btLogin.setDisable( newValue.trim().isEmpty() );
    });
    tfPassword.textProperty().addListener( ( obs, oldValue, newValue ) -> {
      btLogin.setDisable( newValue.trim().isEmpty() );
    });
    dialog.getDialogPane().setContent( grid );

    // Set focus to the username textfield.
    tfUserName.requestFocus();

    // Convert result to a username/password-pair when login button is clicked.
    dialog.setResultConverter( dialogButton -> {
      if ( dialogButton == btnOk ) 
        return new Pair<>( tfUserName.getText(), tfPassword.getText() );
      return null;
    });

    // Use of optional eliminates NullPointerException.
    return (Optional<Pair<String, String>>)dialog.showAndWait();
  }

  public static void main( String[] args ) { Application.launch( args ); }
}

