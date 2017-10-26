/*************************************************************************
 * Title: Employee Table View Utility
 * File: EmployeeTableViewUtility.java
 * Author: James Eli
 * Date: 2/23/2017
 *
 * This JavaFX class provides utility functions for viewing a table of
 * employee data held in a MySQL database. For further information, see: 
 *   EmployeeTableView.java
 *   Employee.java
 *
 * Notes: 
 *   (1) Compiled with java SE JDK 8, Update 121 (JDK 8u121) and JavaFX
 *   version 8.0.121-b13.
 *   
 * Submitted in partial fulfillment of the requirements of PCC CIS-279.
 *************************************************************************
 * Change Log:
 *   02/23/2017: Initial release. JME
 *   03/05/2017: Made first name column editable. JME
 *   04/28/2017: Added title case utility method. JME
 *************************************************************************/
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Pair;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EmployeeTableViewUtility {
  // ArrayLists hold job and pay frequency descriptions.
  private final static List<String> jobDescriptions = new ArrayList<String>();
  private final static List<String> payFrequencies = new ArrayList<String>();
  private static Connection connection; // MySQL database connection object.

  /***************************************************************
   * Open MySQL database. 
   **************************************************************/
  public static boolean openDB( final String url, final String userId, final String password ) {
    // Open mysql database connection.
    try {
      //Class.forName( "com.mysql.jdbc.Driver" ); // Not required.
      connection = DriverManager.getConnection( url, userId, password );
      return true;
    } catch ( Exception ex ) {
      return false;
    }
  }

  /***************************************************************
   * Retrieve a list of MySQL Employee database department fields. 
   **************************************************************/
  public final static List<Pair<Integer, String>> getDepartments() {
    final List<Pair<Integer, String>> departments = new ArrayList<>();

    try ( CallableStatement statement = connection.prepareCall( "{ call sp_all_departments() }" ) ) {
      ResultSet resultSet = statement.executeQuery();
      while ( resultSet.next() ) {
      	int index = resultSet.getInt( "department_code" );
      	String name = resultSet.getString( "department_name" );
    	// Using pair because MySQL returns data in alphabetical order.
        departments.add( new Pair<Integer, String>( index, name ) );
      }
    } catch ( SQLException ex ) {
      System.err.println( "Department exception " + ex.getMessage() );
	  EmployeeTableView.alertDialog( "Database Access Error", "Cannot locate departments inside mySQL database!" );
      System.exit( 0 );
    }
    // Put into ascending order using a lambda comparator.
    Collections.sort( departments, ( a, b ) -> a.getKey().compareTo( b.getKey() ) );
    return departments;
  }

  /***************************************************************
   * Retrieve MySQL Employee database job type fields. 
   **************************************************************/
  public final static boolean getJobTypes() {
    try ( Statement statement = connection.createStatement() ) {
      // execute our query, and get a java resultset
        ResultSet resultSet = statement.executeQuery( "SELECT * FROM job_type" );
        while ( resultSet.next() ) {
          String description = resultSet.getString( "Job_type_description" );
          jobDescriptions.add( description );
        }
    } catch ( SQLException ex ) {
      System.err.println( "Job type exception " + ex.getMessage() );
      return false;
    }
    return true;
  }

  public final static String getJobDescription( final int jCode ) {
    return jobDescriptions.get( jCode - 1 );  // Array index is 0-based.
  }

  /***************************************************************
   * Retrieve MySQL Employee database pay frequency fields. 
   **************************************************************/
  public final static boolean getPayFrequencies() {
    try ( Statement statement = connection.createStatement() ) {
      // execute our query, and get a java resultset
        ResultSet resultSet = statement.executeQuery( "SELECT * FROM pay_frequency" );
        while ( resultSet.next() ) {
          String description =  resultSet.getString( "pay_freq_description" );
          payFrequencies.add( description );
        }
    } catch ( SQLException ex ) {
      System.err.println( "Pay frequency exception " + ex.getMessage() );
      return false;
    }
    return true;
  }

  public final static String getPayFrequency( final int pCode ) {
    return payFrequencies.get( pCode - 1 ); // Array index is 0-based.
  }

  /***************************************************************
   * Retrieve MySQL Employee fields and insert into collection 
   * of (observableList) of the Employee class. 
   **************************************************************/
  public final static ObservableList<Employee> EmployeeGetList( final int departmentNumber ) {
    final ObservableList<Employee> employeeList = FXCollections.<Employee>observableArrayList();

    try ( CallableStatement statement = connection.prepareCall( "{ call sp_employees_in_dept( ? ) }" ) ) {
      statement.setString( 1, String.valueOf( departmentNumber ) ); // Insert department number to fetch.
      ResultSet resultSet = statement.executeQuery();
      while ( resultSet.next() ) {
        // Extract the following data from the MySQL DB table.
        int id = resultSet.getInt( "employee_id" );
        String lName = resultSet.getString( "last_name" );
        String fName = resultSet.getString( "first_name" );
        int jCode = resultSet.getInt( "job_type_code" );
        String email = resultSet.getString( "email_address" );
        String tele = resultSet.getString( "telephone" );
        double pay = resultSet.getInt( "pay" );
        int pCode = resultSet.getInt( "pay_freq_code" );
        employeeList.add( new Employee( id, fName, lName, jCode, pCode, pay, email, tele ) );
      }
    } catch ( SQLException ex ) {
      System.err.println( "SQL exception " + ex.getMessage() );
    }
    return employeeList;
  }

  /***************************************************************
   * Update MySQL Employee first name field. 
   **************************************************************/
  public final static void updateFirstName( final int id, final String fName ) {
  	try {
      PreparedStatement statement = connection.prepareStatement( "UPDATE employee SET first_name = ? WHERE employee_id = ?" );
      statement.setString( 1, fName.toLowerCase() ); // Insert appropriate name and id for updating.
      statement.setInt( 2, id );
      statement.executeUpdate();
	} catch ( SQLException ex ) {
      System.err.println( "SQL UPDATE exception "  + ex.getMessage() );
	}
  }
  
  /***************************************************************
   * Callbacks for the tableview columns.
   **************************************************************/
  public final static TableColumn<Employee, Integer> getIdColumn() {
    TableColumn<Employee, Integer> idCol = new TableColumn<>( "Id" );
    idCol.setPrefWidth( 50 );
    idCol.setStyle( "-fx-alignment: CENTER;" );
    idCol.setCellValueFactory( new PropertyValueFactory<>( "EmployeeId" ) );
    return idCol;
  }

  public final static TableColumn<Employee, String> getFirstNameColumn() {
    TableColumn<Employee, String> fNameCol = new TableColumn<>();
    Label fNameLabel = new Label( "First Name" );
    fNameLabel.setTooltip( new Tooltip( "This column is editable." ) );
    fNameCol.setGraphic( fNameLabel );
    fNameCol.setPrefWidth( 100 );
    fNameCol.setSortable( false );
    fNameCol.setCellValueFactory( new PropertyValueFactory<>( "firstName" ) );
    return fNameCol;
  }

  // Add edit capability to column.  
  public final static void addFirstNameColumn( final TableView<Employee> table ) {
	TableColumn<Employee, String> fNameCol = EmployeeTableViewUtility.getFirstNameColumn();
	// Use TextFieldTableCell to produce editable column.
	fNameCol.setCellFactory( TextFieldTableCell.<Employee>forTableColumn() );
	table.getColumns().add( fNameCol );
    // Following handler fires when cell edit is committed.
    fNameCol.setOnEditCommit( new EventHandler<CellEditEvent<Employee, String>>() {
      @Override
      public void handle( CellEditEvent<Employee, String> t ) {
        int id = t.getTableView().getItems().get( t.getTablePosition().getRow() ).getEmployeeId();
        // Post a confirmation alert.
        Alert alert = new Alert( AlertType.CONFIRMATION );
        alert.setTitle( "Confirmation Dialog" );
        alert.setHeaderText( null );
        alert.setContentText( "Do you want to update the MySQL database with your edit?" );
        Optional<ButtonType> result = alert.showAndWait();
        // Make change permanent to database?
        if ( result.get() == ButtonType.OK ) {
          ((Employee) t.getTableView().getItems().get( t.getTablePosition().getRow() ) ).setFirstName( t.getNewValue() );
          EmployeeTableViewUtility.updateFirstName( id, t.getNewValue() );
        } else {
          table.refresh(); // Discard update and refresh previous data.
        }
      }
    });
  }
  
  public final static TableColumn<Employee, String> getLastNameColumn() {
    TableColumn<Employee, String> lNameCol = new TableColumn<>( "Last Name" );
    lNameCol.setPrefWidth( 100 );
    lNameCol.setCellValueFactory( new PropertyValueFactory<>("lastName" ) );
    return lNameCol;
  }

  // Job Type is a nested column.
  public final static TableColumn<Employee, ?> getJobTypeColumn() {
    TableColumn<Employee, ?> jobTypeCol = new TableColumn<>( "Job Type" );
    TableColumn<Employee, Integer> jobTypeCodeCol = new TableColumn<>( "Code" );
    TableColumn<Employee, String> jobTypeCodeDescriptionCol = new TableColumn<>( "Description" );
    //@SuppressWarnings( "unchecked" ) jobTypeCol.getColumns().addAll( jobTypeCodeCol, jobTypeCodeDescriptionCol );
    jobTypeCol.getColumns().add( jobTypeCodeCol );
    jobTypeCol.getColumns().add( jobTypeCodeDescriptionCol );
    jobTypeCodeCol.setPrefWidth( 50 );
    jobTypeCodeCol.setSortable( false );
    jobTypeCodeCol.setStyle( "-fx-alignment: CENTER;" );
    jobTypeCodeCol.setCellValueFactory( new PropertyValueFactory<>( "jobCode" ) );
    jobTypeCodeDescriptionCol.setPrefWidth( 125 );
    jobTypeCodeDescriptionCol.setSortable( false );
    jobTypeCodeDescriptionCol.setCellValueFactory( new PropertyValueFactory<>( "jobDescription" ) );
    return jobTypeCol;
  }

  // Pay Frequency is a nested column.
  public final static TableColumn<Employee, ?> getPayFrequencyColumn() {
    TableColumn<Employee, ?> payFrequencyCol = new TableColumn<>( "Pay Frequency" );
    TableColumn<Employee, Integer> payFrequencyCodeCol = new TableColumn<>( "Code" );
    TableColumn<Employee, String> payFrequencyDescriptionCol = new TableColumn<>( "Description" );
    //@SuppressWarnings( "unchecked" ) payFrequencyCol.getColumns().addAll( payFrequencyCodeCol, payFrequencyDescriptionCol );
    payFrequencyCol.getColumns().add( payFrequencyCodeCol );
    payFrequencyCol.getColumns().add( payFrequencyDescriptionCol );
    payFrequencyCodeCol.setPrefWidth( 50 );
    payFrequencyCodeCol.setStyle( "-fx-alignment: CENTER;" );
    payFrequencyCodeCol.setSortable( false );
    payFrequencyCodeCol.setCellValueFactory( new PropertyValueFactory<>( "payCode" ) );
    payFrequencyDescriptionCol.setPrefWidth( 200 );
    payFrequencyDescriptionCol.setSortable( false );
    payFrequencyDescriptionCol.setCellValueFactory( new PropertyValueFactory<>( "payFrequencyDescription" ) );
    return payFrequencyCol;
  }

  public final static TableColumn<Employee, String> getPayColumn() {
    TableColumn<Employee, String> payCol = new TableColumn<>( "Pay" );
    payCol.setPrefWidth( 100 );
    payCol.setStyle( "-fx-alignment: CENTER-RIGHT;" );
    payCol.setCellValueFactory( new PropertyValueFactory<>( "salary" ) );
    return payCol;
  }

  public final static TableColumn<Employee, String> getEmailColumn() {
    TableColumn<Employee, String> emailCol = new TableColumn<>( "Email" );
    emailCol.setPrefWidth( 200 );
    emailCol.setSortable( false );
    emailCol.setCellValueFactory( new PropertyValueFactory<>( "email" ) );
    return emailCol;
  }

  public final static TableColumn<Employee, String> getTelephoneColumn() {
    TableColumn<Employee, String> telephoneCol = new TableColumn<>( "Telephone" );
    telephoneCol.setPrefWidth( 100 );
    telephoneCol.setSortable( false );
    telephoneCol.setCellValueFactory( new PropertyValueFactory<>( "telephone" ) );
    return telephoneCol;
  }

  /***************************************************************
   * Utility method that changes string to title case.
   **************************************************************/
  public final static String toTitleCase( final String s ) {
    final String delimiters = " '-/"; // The character following these gets capitalized.
    boolean nextChar = true;          // Capitalize first character.
    StringBuilder sb = new StringBuilder();

    // Walk string char by char.
    for ( char c : s.toCharArray() ) {
      c = ( nextChar ? Character.toUpperCase( c ) : Character.toLowerCase( c ) );
      sb.append( c );
      if ( delimiters.indexOf( c ) >= 0 ) //nextChar = delimiters.indexOf( c ) >= 0;
        nextChar = true;
      else
        nextChar = false;
    }
    return sb.toString();
  }

}