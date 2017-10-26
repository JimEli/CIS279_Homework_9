create database EmployeeDB;

use EmployeeDB ;

drop table if exists employee;

drop table if exists dept;

drop table if exists job;

create table dept
(
    dept_code TINYINT PRIMARY KEY, 
    dept_name VARCHAR(30)
);


create table job
(
    job_code    TINYINT PRIMARY KEY, 
    job_desc    VARCHAR(20)
);

create table employee
(
    employee_id     INT AUTO_INCREMENT PRIMARY KEY, 
    last_name       VARCHAR(20),
    first_name      VARCHAR(12),
    job_code   TINYINT ,
    dept_code TINYINT ,
    pay             NUMERIC(12,2),
    CONSTRAINT emp_job_FK FOREIGN KEY(job_code) REFERENCES job(job_code),
    CONSTRAINT dept_FK FOREIGN KEY (dept_code) REFERENCES dept(dept_code)
);


insert into dept
(dept_code, dept_name)
values
(1, 'Human Resources'),
(2, 'Accounting'),
(3, 'Engineering'),
(4, 'Software Development'),
(6, 'Manufacturing'),
(7, 'Shipping & Receiving'),
(8, 'Executive'),
(9, 'Finishing');

insert into job
(job_code, job_desc)
values
( 1, 'clerical assistant'),
( 2, 'software developer'),
( 3, 'engineer'),
( 4, 'manager'),
( 5, 'vice president'),
( 6, 'president'),
( 7, 'machinist'),
( 8, 'assembler'),
( 9, 'receiver'),
( 10,'shipper'),
( 11, 'finisher'),
( 12, 'accountant'),
( 13, 'analyst');


insert into employee
(last_name, first_name, job_code, dept_code, pay)
values
('washington', 'geo.', 6, 8, 4000.00),
('adams', 'john', 5, 8, 3250.00),
('cady-stanton', 'elizabeth', 3, 3, 3250.00),
('allen', 'paul', 2, 4, 3000.00),
('gates', 'bill', 2, 4, 5800.00),
('simonyi', 'charles', 2, 4, 4700.00),
('wozniak', 'steve', 3, 3, 3900.00),
('edison', 'thomas', 3, 3, 4100.00),
('bardeen', 'john', 3, 3, 2900.00),
('shannon', 'claude', 3, 3, 2500.00),
('virgo', 'norman', 4, 3, 1200.00),
('gillis', 'john', 4, 2, 1150.00),
('dunbar', 'ainsley', 7, 6, 2200.00),
('redding', 'noel', 7, 6, 1700.00),
('mitchell', 'mitch', 7, 6, 1800.00),
('anderson', 'ian', 7, 6, 1900.00),
('nash', 'graham', 8, 6, 2100.00),
('redding', 'noel', 8, 6, 2300.00),
('harrison', 'george', 9, 7, 2400.00),
('lennon', 'john', 9, 7, 2000.00),
('mccartney', 'paul', 10, 7, 2100.00),
('starkey', 'richard',10, 7, 2200.00),
('barre', 'martin', 11, 9, 2300.00),
('pense', 'lydia', 11, 9, 2400.00);

--- Create and test simple stored procedures:

drop procedure if exists sp_get_depts;

-- highlight (select) and execute the code from the following line:
delimiter //
create procedure sp_get_depts()
begin
	select concat(dept_code, ' - ', dept_name) from dept;
end//
-- through the previous line.

-- highlight (select) and execute the code in the following line:
delimiter ;

drop procedure if exists sp_get_jobs;

-- highlight (select) and execute the code from the following line:
delimiter //
create procedure sp_get_jobs()
begin
	select concat(job_code, ' - ', job_desc) from job;
end//
-- through the previous line.

-- highlight (select) and execute the code in the following line:
delimiter ;

drop procedure if exists sp_get_all_employees;

-- highlight (select) and execute the code from the following line:
delimiter //
create procedure sp_get_all_employees()
begin
	select employee_id, last_name, first_name, employee.job_code, job_desc, employee.dept_code, dept_name, pay from employee 
    inner join job on employee.job_code = job.job_code
    inner join dept on employee.dept_code = dept.dept_code;
end//
-- through the previous line.

-- highlight (select) and execute the code in the following line:
delimiter ;

drop procedure if exists sp_get_employees_in_dept;

-- highlight (select) and execute the code from the following line:
delimiter //
create procedure sp_get_employees_in_dept(in deptCode TINYINT)
begin
	select employee_id, last_name, first_name, employee.job_code, job_desc, employee.dept_code, dept_name, pay from employee 
    inner join job on employee.job_code = job.job_code
    inner join dept on employee.dept_code = dept.dept_code
    where employee.dept_code = deptCode;
end//
-- through the previous line.

-- highlight (select) and execute the code in the following line:
delimiter ;

drop procedure if exists sp_get_employee;

-- highlight (select) and execute the code from the following line:
delimiter //
create procedure sp_get_employee(in empID TINYINT)
begin
	select employee_id, last_name, first_name, employee.job_code, job_desc, employee.dept_code, dept_name, pay from employee 
    inner join job on employee.job_code = job.job_code
    inner join dept on employee.dept_code = dept.dept_code
    where employee_id = empID;
end//
-- through the previous line.

-- highlight (select) and execute the code in the following line:
delimiter ;

drop procedure if exists sp_update_employee;

-- highlight (select) and execute the code from the following line:
delimiter //
create procedure sp_update_employee(in empID TINYINT, in lastName VARCHAR(20), in firstName VARCHAR(12), 
								in jobTypeCode TINYINT, in deptCode  TINYINT, in periodicPay NUMERIC(12,2))
begin
	update employee
	set last_name = lastName,
		first_name = firstName,
		job_code = jobTypeCode,
		dept_code = deptCode,
		pay = periodicPay  
		where employee_id = empID;
end//
-- through the previous line.

-- highlight (select) and execute the code in the following line:
delimiter ;

drop procedure if exists sp_add_employee;

-- highlight (select) and execute the code from the following line:
delimiter //
create procedure sp_add_employee(in lastName VARCHAR(20), in firstName VARCHAR(12), 
								in jobTypeCode TINYINT, in deptCode  TINYINT, in periodicPay NUMERIC(12,2))
begin
	insert into employee
		(last_name, first_name, job_code, dept_code , pay)
	values (lastName, firstName, jobTypeCode, deptCode , periodicPay); 
end//
-- through the previous line.

-- highlight (select) and execute the code in the following line:
delimiter ;

-- run the tests individually.

-- test the first procedue by highlighting (selecting) and 
-- executing the code in the following line:

call sp_get_jobs();

-- test the first procedue by highlighting (selecting) and 
-- executing the code in the following line:

call sp_get_depts();

-- test the first procedue by highlighting (selecting) and 
-- executing the code in the following line:
call sp_get_all_employees();

-- test the second procedue by highlighting (selecting) and 
-- executing the code in the following line:
call sp_get_employees_in_dept( 4 );

-- test the first procedue by highlighting (selecting) and 
-- executing the code in the following line:
call sp_get_employee(2);

-- test the second procedue by highlighting (selecting) and 
-- executing the code in the following line:
call sp_update_employee(1, 'washington', 'george', 6, 8, 4075.00);

-- test the second procedue by highlighting (selecting) and 
-- executing the code in the following line:
call sp_add_employee('kearns', 'doris', 3, 3, 4150.00);