# Fontys Module Management System backend
This is the backend of the FMMS project. It is written in Java and uses Jersey to create a REST API run on a Grizzly server. 
This project uses Maven to offer some handy functionality when contributing.

## Scripts
`mvn checkstyle:check` Checks your code according to the standards defined in the `checkstyle.xml` file.

`mvn checkstyle:report` Generates a report based on the standards. Any faults, warnings or errors can be viewed in a nice generated website in the `target` folder.

`mvn test` Runs the tests and also generates a code coverage report once again in a handy website in the `target` folder.
