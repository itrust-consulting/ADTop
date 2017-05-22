# ADTop (Attack-Defence Tree optimizer)

<!--- TODO: Add description as introduction, papers --->

## Dependencies

* Java JDK 8u92 or later
* Maven
* Scene Builder
* Any IDE like for e.g. [eclipse](https://eclipse.org/ide/)

## Installation and Execution

ADTop is build as Maven project which makes it easier to install the project.
Depending on the choice, it following two paths can be taken to build the project.

### Command-line based

After cloning or downloading the repository, in the terminal, execute ```mvn package```
to build the project

This should give a build successful message. Furthermore, the `package` goal of maven
also executes all the tests within the project. Implementing of tests is in-progress.


### IDE based

Import the project in your favorite IDE and do a clean build. Following this one can do a direct `run` to execute it.


### Generating Executable

To make a executable `Jar` file for the project, execute a `mvn clean install` or generate Jar using the `maven clean install` option.

### Execution

If the build is successful, then the compiled .jar file can be found within the
`target\` folder. Just execute the .jar file and ADTop is up and running.

## Screenshoots

TODO: Add couple of screenshots with tutorials/examples

## Development information

ADTop user Java FX which is required to create future screen in FXML. This is the reason Scene Builder is one of the dependency of the project.

Class gui charge index FXML file created via Scene builder. This FXML file is
connected to Index controller. Index controller contains other controllers.
Java FX, FXML structure is nested application. You can access to attributs of
FXML file ex: button thanks to @FXML tag. Index controller is the principal
controller. It has a principal Attribute called Pack. This object contains all
models used in ADTop. Measure container, Attack-defence tree etc..

All models are in the model class.

Jackson is used to import export Measure container.
Apache POI is used to import export Association Matrix.
Apache XML is used to parse XML. (ADT is bidirectionnal object, that's why Tree
is parsed manually)

java -cp target/my-app-1.0-SNAPSHOT.jar com.mycompany.app.App

## License


## Further information

Additionally, interested parties can contact [itrust consulting](https://www.itrust.lu)
or call `0035226176212` for further information information.
