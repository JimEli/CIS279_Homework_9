create database EmployeeDB;

use EmployeeDB ;

drop table if exists department;

create table department
(
    department_code TINYINT PRIMARY KEY, 
    department_name VARCHAR(30)
);

drop table if exists job_type;

create table job_type
(
    job_type_code           TINYINT PRIMARY KEY, 
    job_type_description    VARCHAR(20)
);

drop table if exists work_location;

create table work_location
(
    work_loc_code   TINYINT PRIMARY KEY,
    office          VARCHAR(5),
    address         VARCHAR(30),
    address2        VARCHAR(30),
    city            VARCHAR(30),
    state           CHAR(2),
    zipcode         CHAR(5)
);

drop table if exists pay_frequency;

create table pay_frequency
(
    pay_freq_code           TINYINT PRIMARY KEY,
    pay_freq_description    VARCHAR(36)
);

drop table if exists employee;

create table employee
(
    employee_id     INT AUTO_INCREMENT PRIMARY KEY, 
    last_name       VARCHAR(20),
    first_name      VARCHAR(12),
    job_type_code   TINYINT ,
    work_loc_code   TINYINT ,
    department_code TINYINT ,
    email_address 	VARCHAR(32),
    telephone 		VARCHAR(15),
    pay             NUMERIC(12,2),
    pay_freq_code   TINYINT ,
    CONSTRAINT emp_job_type_FK FOREIGN KEY(job_type_code) REFERENCES job_type(job_type_code),
    CONSTRAINT emp_work_loc_FK FOREIGN KEY(work_loc_code) REFERENCES work_location(work_loc_code),
    CONSTRAINT department_FK FOREIGN KEY (department_code) REFERENCES department(department_code),
    CONSTRAINT pay_freq_FK FOREIGN KEY (pay_freq_code)     REFERENCES pay_frequency(pay_freq_code)
);


insert into department
(department_code, department_name)
values
(1, 'Human Resources'),
(2, 'Accounting'),
(3, 'Engineering'),
(4, 'Software Development'),
(6, 'Manufacturing'),
(7, 'Shipping & Receiving'),
(8, 'Executive'),
(9, 'Finishing');

insert into job_type
(job_type_code, job_type_description)
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
( 11, 'finisher');


insert into pay_frequency
(pay_freq_code, pay_freq_description)
values
( 1, 'Hourly paid every 2 weeks'),
( 2, 'Salary paid every 2 weeks'),
( 3, 'Salary + comm. pd every 2 weeks');

insert into employee
(last_name, first_name, job_type_code, department_code, pay, pay_freq_code, telephone, email_address)
values
('washington', 'george', 6, 8, 400000.00, 2, '520-206-0001', 'gw@Thecompany.com'),
('adams', 'john', 5, 8, 325000.00, 2, '520-206-0002', 'jadams@Thecompany.com'),
('cady-stanton', 'elizabeth', 3, 3, 325000.00, 2, '520-206-0003', 'ecstanton@Thecompany.com'),
('allen', 'paul', 2, 4, 90000.00, 2, '520-206-0010', 'pallen@Thecompany.com'),
('gates', 'bill', 2, 4, 85000.00, 2, '520-206-0011', 'wgates@Thecompany.com'),
('simonyi', 'charles', 2, 4, 85000.00, 2, '520-206-0012', 'csimyoni@Thecompany.com'),
('wozniak', 'steve', 3, 3, 95000.00, 2, '520-206-0013', 'swozniak@Thecompany.com'),
('edison', 'thomas', 3, 3, 75000.00, 2, '520-206-0014', 'tedison@Thecompany.com'),
('bardeen', 'john', 3, 3, 90000.00, 2, '520-206-0015', 'jbardeen@Thecompany.com'),
('shannon', 'claude', 3, 3, 92500.00, 2, '520-206-0016', 'cshannon@Thecompany.com'),
('virgo', 'norman', 4, 3, 105000.00, 2, '520-206-0017', 'nvirgo@Thecompany.com'),
('gillis', 'john', 4, 2, 115000.00, 2, '520-206-0018', 'jgillis@Thecompany.com'),
('dunbar', 'ainsley', 7, 6, 45000.00, 1, '520-206-0020', ''),
('redding', 'noel', 7, 6, 45000.00, 1, '520-206-0020', ''),
('mitchell', 'mitch', 7, 6, 45000.00, 1, '520-206-0020', ''),
('anderson', 'ian', 7, 6, 45000.00, 1, '520-206-0020', ''),
('nash', 'graham', 8, 6, 45000.00, 1, '520-206-0020', ''),
('redding', 'noel', 8, 6, 45000.00, 1, '520-206-0020', ''),
('harrison', 'george', 9, 7, 32000.00, 1, '520-206-0030', ''),
('lennon', 'john', 9, 7, 32000.00, 1, '520-206-0030', ''),
('mccartney', 'paul', 10, 7, 32000.00, 1, '520-206-0030', ''),
('starkey', 'richard',10, 7, 32000.00, 1, '520-206-0030', ''),
('barre', 'martin', 11, 9, 45000.00, 1, '520-206-0020', ''),
('pense', 'lydia', 11, 9, 45000.00, 1, '520-206-0020', '');
