# Fontys Module Management System backend
This is the backend of the FMMS project. It is written in Java and uses Jersey to create a REST API run on a Grizzly server. 
This project uses Maven to offer some handy functionality when contributing.

## Scripts
`mvn checkstyle:check` Checks your code according to the standards defined in the `checkstyle.xml` file.

`mvn test` Runs the tests and also generates a code coverage report in a handy website in the `target` folder.

## Contributing
If you're here to add new endpoints or functionality to the FMMS REST API then keep reading!

!!!

First of all: Read the [Jersey JAX-RS documentation](https://jersey.github.io/)!

!!!

### Endpoints and services
New endpoints can be added by subclassing the `Endpoint` class and providing a new Path. As a rule of thumb, every group of endpoints that are related make use of their own `Service`.

Not every class has to have their own `Service`, since some functionality can be split up into multiple classes, but still need access to one `Service` class.

For instance the `EditableModuleEndpoint` and `ReadableModuleEndpoint` both use the `ModuleService` class. But this is all up to you of course :)

There is an issue where the Path of a method cannot overlap the path of a class. 
Meaning that this
```
@Path("/hello/world
Class A {}

@Path("/"
Class B {
  @Path("hello/world/again")
  public Response doStuff(){}
}
```

Will not work, and one of the two endpoints will not be registered properly.

### Services
`Service` classes provide database and query access for `Endpoint`s. When creating a new service, subclass the `Service` class and create any methods the Endpoint needs. The methods for getting a connection already exist in the abstract class.

Be sure to register any new services in the AppBinder class!

### Filters
A filter can be used to preprocess any incoming requests and outgoing responses. This is used by implementing the `ContainerRequestFilter` and `ContainerResponseFilter` interface. One method will need to be implemented that provides the `Context` of both situations.

### Tests
The main part of tests will test the `Endpoint` and `Services` separately.

The use of the `Endpoint` tests is to ensure the right status codes are given, and any input is processed correctly.

The use of the `Service` tests is to test any output from the database is processed correctly and given to the Endpoint.

### Database connection
The `Connection` class ensures a connection to the PostgreSQL database. 

### Style
Code style is enforced by [Checkstyle](http://checkstyle.sourceforge.net/) using the `checkstyle.xml` file. Be warned any build or test run WILL FAIL if code style is not followed!
