# SBR SQL database connector #

## Purpose ##

* Demonstrate ability to query SBR data from a SQL database.
* Provide this functionality via a separate module to be integrated into the SBR Controller.

## Running the application separately ##

`sbt run`

* There is a simple dummy application [DevDummyApp](./src/main/scala/uk/gov/ons/sbr/data/DevDummyApp.scala).
* This is configured to load sample data from SQL INSERT scripts (held separately from this repository) into the target database tables and print a count of the records in each table.

## Testing the application ##

`sbt test`

* There are tests for all the main database functions.  
* These are configured to use an [H2 in-memory database](http://www.h2database.com/).
* Each test will create/update records in the test database as necessary. 

## Configuration ##

### Configuration file ###

* See [application.conf](./src/main/resources/application.conf).
* Default configuration is used for the dummy application (see above) e.g. it calls for the sample data to be loaded into the database.
* Test configuration does not load sample data.
* Both configurations are set to create the target tables, as we assume we are running in a fresh database e.g. [H2](http://www.h2database.com/).
* There is a sample configuration for using a local PostgreSQL RDBMS, which has been tested with the dummy application and the test suites.
* The SQL Server configuration should be similar.

### Using the database configuration ###

* The main DB service is provided via the [SbrDbService](./src/main/scala/uk/gov/ons/sbr/data/service/SbrDbService.scala) class.
* This expects to receive a [TypeSafe Config](https://github.com/typesafehub/config) object containing the configuration for the required database i.e. either `default` or `test` from our `application.conf` file.
* If the service is being called from another module, then this configuration should be provided.
* If no database configuration is provided, the application will use the one from the `application.conf` file.

## SQL-specific features ##

### Data model and tables ###

* The data model is a simple hierarchy:

> * An `Enterprise` can have zero or more `Legal units`.
> * A `Legal Unit` can have a `Companies House` record (a company).
> * A `Legal Unit` can have zero or more `PAYE` records.
> * A `Legal Unit` can have zero or more `VAT` records.

* The tables are created via the [SbrDbTables](src/main/scala/uk/gov/ons/sbr/data/db/SbrDbTables.scala) object when the service is started (see above for config).
* For this Alpha exercise, we kept the basic 3rd Normal Form model, but this would probably need to be modified for a large scale production application.

### Data ###

* We have a set of around 2500 sample "large enterprises" that were identified by the SBR data scientists.
* However, the original sample records were not all complete e.g. there were "orphan" CH/PAYE/VAT records that were not related to a Legal Unit.
* This breaks foreign key relationships in the SQL data model, so we had to pre-process the data to remove incomplete records etc.
* Once a "clean" set of data had been created, we generated the SQL INSERT statements to create these records in the target tables.
* These SQL scripts are held in a separate repository (in the ONS GitLab).

### Queries and data formats ###

* The SQL database allows us to model the hierarchical relationships and data structures explicitly, unlike the alternative HBase storage platform.
* For this reason, we have stored the actual Company/PAYE/VAT data with the linked CH/PAYE/VAT entry for each Legal Unit.
* This means we do not have to query a separate "Admin Data" store to get the Company details etc for a given Legal Unit.
* Also, we can fetch fully structured data because we can model all the relevant fields in the data model.
* However, because this connector is supposed to be compatible with the HBase Connector, the query functions ultimately return generic (weakly structured) data objects instead.
* For example, the non-key properties of each object (CH, VAT, PAYE) are presented as simple key-value pairs of Strings, because this is what the client application is expecting from HBase.
* The application creates these by exploiting the standard Play JSON library's support for JSON mapping via implicit `Reads`/`Writes` objects.
* When we need to convert from a database object to the simpler Map representation, we convert the object to JSON via Play JSON functions, then from JSON to the required Map.
* This works well, provided the class attributes match the keys in the Map.

### StatUnits and StatUnitLinks ###

* These two classes are how data is provided to an external application from the SQL Connector, in order to match the basic data structures returned from the HBase Connector.
* [StatUnitLinks](./src/main/scala/uk/gov/ons/sbr/data/model/StatUnitLinks.scala) contain just the parent/child links and IDs for an object.
* The corresponding table `unit_links_2500` is queried when the user wants to search for an object by ID.
* [StatUnit](src/main/scala/uk/gov/ons/sbr/data/model/StatUnit.scala) contains the data for the given object (as key-value pairs), plus a nested hierarchy of all the object's children as StatUnit objects.
* StatUnits are not stored as such in the database, but are constructed at runtime by the query which reads the relevant records for the Enterprise, Legal Unit etc.

### Updates ###

* We did not have time to integrate the "edit Enterprise" functionality with this module.
* However, the current DAO objects and service methods will allow existing fields on an Enterprise StatUnit object to be updated via the key-value Map.
* See the test case "insert new Enterprise and update it correctly via StatUnit" in the [SbrDbServiceTest](src/test/scala/uk/gov/ons/sbr/data/service/SbrDbServiceTest.scala) suite for an example of how this works.
* The SQL table structure is pre-defined, so we cannot currently add new fields to an object (unlike HBase where the stored data structure is arbitrary), because we would need the corresponding column to exist in the table.

### Alternative data model for changing attributes ###

* As indicated above, the relative rigidity of the relational table model makes it hard to add arbitrary fields to an object at runtime, at least in our current 3NF model.
* However, many RDBMS platforms now allow you to store collections (maps, arrays, sets) within a table record.
* This might provide a way to store a changing set of attributes
 without having to change the data model.
* A more flexible and scalable alternative would be to use [MongoDB](https://www.mongodb.com/), which would allow us to represent the internal structure of these hierarchical obects explicitly, but still allow us to add extra fields at runtime as necessary.
 
### SQL database choice ###

* We used [H2 in-memory database](http://www.h2database.com/) as our primary DB here because it was readily available and avoided dependencies on any additional infrastructure.
* We also tested the SQL Connector with PostgreSQL to ensrue that ti was compatible with other SQL databases.
* We did not test it with the proposed SQL Server database, as this would have introduced extra infrastructure dependencies and prevented us working off-network.
* As indicated above, there are reasons to favour [MongoDB](https://www.mongodb.com/) as a more suitable database platform for the SBR application.


## SQL access library: ScalikeJDBC ##

* Scala has several commonly used RDBMS interface libraries.
* The Play Framework now uses the "functional-relational" [Slick](http://slick.lightbend.com/) library by default.
* However, in this case we did not need all the sophisticated functionality provided by Slick.
* Instead, we chose the simpler [ScalikeJDBC](http://scalikejdbc.org/) library.
* This provides a basic DSL for SQL functionality and allows you to convert between database records and Scala objects relatively easily.
* Many of our DB functions rely on implicit database session objects, which in our case are created when the DB service is initialised.
* There are various configuration options for ScalikeJDBC which allow more sophisticated session management, connection pooling etc, but this was not necessary here.