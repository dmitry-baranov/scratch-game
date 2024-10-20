# scratch-game

## Building and Running the Project

### Building the JAR File

To build the JAR file, execute the following command in the root directory of the project:

```bash
chmod +x mvnw
./mvnw clean package
```

### Running the Application

After successfully building the project, you can run the application using the following command:

```bash
java -jar target/scratch-game-1.0-SNAPSHOT.jar --config src/test/resources/config.json --betting-amount 100

```