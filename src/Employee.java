import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/***************************************************************
 * Model class used to hold MySQL employee data.
 **************************************************************/
public class Employee {
  private Integer employeeId;
  private String lastName;
  private Integer jobCode;
  private Integer payCode;
  private String salary;
  private String email;
  private String telephone;
  // firstName made StringProperty to support tableview cell editing.
  private final StringProperty firstName = new SimpleStringProperty( this, "firstName", null );

  // Constructor.
  public Employee( 
    Integer id,
    String fName, 
    String lName,
    Integer jCode,
    Integer pCode,
    Double pay,
    String mail,
    String tele
  ) {
    employeeId = id;
    // Capitalize names.
    fName = EmployeeTableViewUtility.toTitleCase( fName );
    lName = EmployeeTableViewUtility.toTitleCase( lName );
    // Note, fName field is a string property.
    firstName.set( fName ); 
    lastName = lName;
    jobCode = jCode;
    payCode = pCode;
    email = mail;
    telephone = tele;
    salary = String.format( "$%.2f", pay ); // Format as $USD.
  }

  // Getters.
  public Integer getEmployeeId() { return employeeId; }
  public String getLastName() { return lastName; }
  public Integer getJobCode() { return jobCode; }
  public Integer getPayCode() { return payCode; }
  public String getSalary() { return salary; }
  public String getTelephone() { return telephone; }
  public String getEmail() { return email.isEmpty() ? "No email address provided." : email;  }

  // Setters.
  public void setEmployeeId( Integer id ) { employeeId = id; }
  public void setLastName( String name ) { lastName = name; }
  public void setJobCode( Integer code ) { jobCode = code;  }
  public void setPayCode( Integer code ) { payCode = code; }
  public void setTelephone( String tele ) { telephone = tele; }
  public void setEmail( String mail ) { email = mail; }

  // firstName Property 
  public final String getFirstName() {
    return firstName.get();
  }
  public final void setFirstName( String fName ) {
    firstNameProperty().set( fName );
  }
  public final StringProperty firstNameProperty() {
    return firstName;
  }

  // Non-class member getters.
  public final String getPayFrequencyDescription() { 
    return EmployeeTableViewUtility.getPayFrequency( payCode ); 
  }
  public final String getJobDescription() {
    return EmployeeTableViewUtility.toTitleCase( EmployeeTableViewUtility.getJobDescription( jobCode ) );
  }

}
