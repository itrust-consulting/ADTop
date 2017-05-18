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

Finally, the generated
java -cp target/my-app-1.0-SNAPSHOT.jar com.mycompany.app.App

### IDE based

Import the project in your favorite IDE and do a clean build. Following this one can do a direct `run` to execute it.


### Generating Executable

To make a executable `Jar` file for the project, execute a `mvn clean install` or generate Jar using the `maven clean install` option.


### Execution

##

ADTop user Java FX that's why you need a software to create future screen in
FXML. You can user Scene Builder.

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



Execution: You can simply pull the project download dependencies via maven download
dependencies menu (right click on project).

Generate JAR : you can generate a Jar via maven clean install menu.

BRANCHES

Master and development (merged) final delivered version
addCost (with some additional features like adding and editing costs.)

## Further information

Additionally, interested parties can contact [itrust consulting](https://www.itrust.lu)
or call `0035226176212` for further information information.
