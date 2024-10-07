# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](240System.png)](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdADZTme1AgBXbDAAxGjAVJYwAEoo9kiqFnJIEGhBAO4AFkhgYoiopAC0AHzklDRQAFwwANoACgDyZAAqALowAPS+BlAAOmgA3gBEHZQhALYo-WX9MP0ANNO46inQHBNTs9MoI8BICKvTAL6YwqUwhazsXJQVA0NQo+OT03P9C6pLUCuP6-2b27tfhzYnG4sDOx1EFSg0ViWSgAAoojE4pQogBHXxqMAASiOJVEpyKsnkShU6gq9hQYAAqp04bd7jiiYplGpVASjDoygAxJCcGA0yhMmA6cL04BjTBMkms05gvEqCpoXwIBC4kQqdlSllkkBQuQoAXw25MxnaaXqdnGMoKDgcfmdJlqqj4s5a0mqMq6lD6hS+MBpOHAP1pE2Ss3atlnK02u2+-2O8EavJnIGXcqRaHIqBRVTKrCpkGyorHK4wG6de5TCprZ5B-0NCAAa3QVemBydlCL8GQ5gqACYnE4euXhuLxjBq09pnW0g3m2hW2tDugOKZvH4AoFoOwKTAADIQGLJQLpTLZHv5YslUs1erNNoGdSJNDDsVjOavd4cQ4l0FFAuliOdxjns3yfssoGAhcIKFImKAVAgh68nCB5HmiGJxFiMBOi6hLhu65KUoadIVmOprEhGlqcjAPJ8oaQoijAb5iG6MrJle6rwfagraDhKiwfKXGlEgABmljVPobzLCRPHyHMEmLMsOJwZq+Gsp6epZHGAYzqGrEWlGnIxjA2kJoJXYAeUiFHjAUK5gg+bQZQ7HFNQgGDKRYygbWwZzi2AIdlALk5L2MADkOfQeaOXlfD59ZNv5awwMunCeF4Pj+EE3goOg+6Hn4zAnhkWSYCFl6uaUFSVNIACie41Q0NUtK0j6qM+PQzn5aCBeylnXP0nUJQuk79FBwLOQUcEIflfooTNYDoZiWF8YYrpqWSUQjBANCmdocLaUN5HMu6VHWsG0gKLUGZbTQJnBlo8jCuE2mQPOmCBDI62RnKnFnfGvErRZTnptZBUZvZjnjUFP2VWWP7XtDRRlWA-aDsOKWrml6UbkEUJ2nuMIwAA4mObJFWepUXswMM3kTDXNfYY4db5Q09SmwP9YN84TG2o2YJZ7JTTAFJgCTYyqHCjPi4tmGA2tFEEcLRG0tpXVHeakZFFad3xpdxOUkxnksV9XZC1EYC+HchuUAAcmOct4Qr6kwMgcRi2oMnAWM6uUYZFTu6oF1XSLxOk093FQHL7NQ5ClKW8kIsB7Zah5vzwOCwj1zTFLag85UAw5wAktIPMAIx9gAzAALM8p5ZIalaxdMOgIKAjYNyBTf9DndtjACMBNPDbmI92uQo1U4VNMO2ek3nBdjsXZeVzX0x1waRvec3rcgO3G9dz3Y794PZipVj2OZYE2CW9g3DwJphju6kxXnmPGfD1VdSNAzTMhCz87DgfMYQ9SiFD6mWAaf9-K8zmIAriS405QwEr9GAwkxLaUDJAtAylBKFH0h6FBlBRKWAoE+JIGD4rzh9u6Qo2tTJ6xDlzXKEAdAACsUDgGwgLGGEIYBen1O7SWY5e4oGwZxXBX0Kh8KyO7JkgixjCKoayGh1EZF62ALaUOYxHR4KQc6BUycLZWxkbxFS0c0yx0MckKRKBH52VTpZXRgEZ5jEXhUcu1dkqmF-OyZGqMnAwGnt3BeJc3HL08SfTGaV1wX0sCgFUEAUgwAAFIQF5JowwgQW5t0pq-Gm6YahUnvK0HOzMKHoGHDfYAsSoBwAgIhKAMDgnAImn1AYlTqm1PqTzGswtgkjTmIwtAzx2wOMmoJCoqDLDoMGaIvRKBxFOzJAQqARCSFtTITMsMizVDKIqHQ4OBtBkwGYWwjhCC0xvzmWUfwD0bFjjkSgUMH04ILOOs7SZAic6hheQUPBFQbn8PuV87Qij1C7N6Vo7QQclbMGBY9WoAAhJJ4ci7SDZkjKmfj0ZR3-BzCJZ9ombm8FU7s3pYDAGwDfQgCQkhPwpsjS5N5ar1Uas1Yw6Lzgx16GNC53D9EgG4HgaQCK4SzNwp9bZkjBVQHopdUVWy3kGS1tRKEN1DAKD3HuCOGhTGO0VfggVZLVG1HlXg06yc1UmU1eknV5l5b6qlWS-ZpqTZ+wtdtdV1rVZDVtZxIGMdEBkuFeDexwMXK-muM00EZxfFhTRn0DGnggA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
