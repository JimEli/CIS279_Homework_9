# Pima CC CIS279 Homework Assignment #9

This JavaFX class provides functions for viewing a table of employee data held in a MySQL database using a MVC/MVP type architecture. 

The MySQL DB department table is missing item #5. This created difficulties for the combobox and array alignment. Additionally, the MySQL stored procedure returns the department fields in alphabetical order. These issues necessitated my use of the List<Pair> and sorting inside the getDepartments() method. 
 
Better crafted MySQL queries could have eliminated the need for the funky getJobTypes() and getPayFrequencies() methods. However, adding this functionality was good practice.
 
For additional programming experience I made the first name column editable, and provided a rudimentary database update method. 
 
MySQL user id and password can be hard-coded into the program below. However, for obvious security reasons this should not be done. 

For further information, see the files: 
* EmployeeTableViewUtility.java
* EmployeeTableView.java
* Employee.java
 
Notes: 
* Requires MySQL Employeedb database.
* Ensure the database URL, userid and password are entered appropriately.
* Compiled with java:
* (a) SE JDK 8, Update 131 (JDK 8u131)
* (b) JavaFX version 8.0.121-b13
* (c) Java-MySQL connector version 5.1.40
   
Submitted in partial fulfillment of the requirements of PCC CIS-279.
