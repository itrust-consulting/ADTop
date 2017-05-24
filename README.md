# ADTop (Attack-Defence Tree optimizer)

<!--- TODO: Add description as introduction, papers --->

## Dependencies

* Java JDK 8u92 or later
* Maven
* JavaFX/OpenJFX
* Scene Builder
* (Optionally) Any IDE, e.g. [eclipse](https://eclipse.org/ide/)

## Installation and execution

ADTop is built as a Maven project which makes it easier to install the tool.
Depending on users' preferences (command line or IDE), two paths can be taken to build the tool.

### Command-line based

After cloning or downloading the repository, execute the following in the terminal ```mvn package -Dmaven.test.skip=true```
to build the project. We use this argument to skip the tests that are currently in progress.

Once all the tests have been implemented, using simple `mvn package` would successfully build it. The `package` goal of maven
also executes all the tests within the project.

### IDE based

Import the project in your favorite IDE and do a clean build. Following this, just do a direct `run` to execute it.

### Execution

If the build is successful, then the compiled `.jar` file can be found within the
`target` folder. Just execute the .jar file and ADTop is up and running.

For Linux-based distribution, run the .jar file using the following command:

```
java -jar adtop-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

## Screenshots

![Main view of the ADTop](/img/main.jpg)

![Example in ADTop](/img/example.jpg)

## Development information

ADTop uses JavaFX which is required to create future screen in FXML.

Class `GUI` does the indexing of FXML file that is created via Scene builder.
This FXML file is connected to Index controller which contains other controllers.
Java FX, FXML structure is nested application. The attributes of the FXML file
can be accessed using the `@FXML` tags.

The `Index` controller is the principal controller within the project with a principle attribute
called `Pack`. It contains all the models used in the ADTop, measure container, attack-defense trees, etc.

All the models used within the tool are in the `model class`.

`Jackson` package is used to import/export `Measure` container(s).

`Apache POI` is used to import/export Association Matrix and `Apache XML` is used for parsing the XML.
_Note_: ADT is a bidirectional object which is the reason for a tree to be parsed manually.

## License

[![License](https://img.shields.io/badge/License-BSD%203--Clause-blue.svg)](https://opensource.org/licenses/BSD-3-Clause)

## Further information

Additionally, interested parties can contact [itrust consulting](https://www.itrust.lu)
or call `0035226176212` for further information information.
