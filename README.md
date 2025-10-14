# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

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

[Server Sequence Diagram](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGVIE4DdF0gRwK6QHYGMYAREAQwHMESBbAKBpL2AHsFoBhcEXYGgBxISg8IfjmBpkiPgKEiSY6AAl5AEygJpgkMNHj4ybZE2zd0QiWAkAgngIBnOzRUWSAIxJ2YK13W4IAnnb8wjhk0AAMAHQAnDQUTJi8AMQALADMABwATNF40EmcZAAWwK7g2NAASpBkIHbAlKBMODQcXAoAtAB8EigIAFwA2gAKAPKwACoAutAA9JieCAA6OADeAEQLiDjUkOv969DrADRH-A4A7iwq+4cn65BUJCDgt0cAvjT6fdDdSqrqfrQDZbBA7Kh7A5HU7rc52K4IG5Q+6PZ6vZGfZQ4NSoP7fQxAhA1OrARAACmqtXqiGqWEg9QAlF9EEhDL8euZLDZ7HYgWRIMAAKqLMmg8GQJmc6y2el2dnQbxAgBiIGx0GFiCl0Fc-mgYt2NFcRJIAGt1YtoBcwEU9YtxdASOBjSpdZAAB4kxxS7my354lkE82alzMgwEP09b6IIFWJ2QEguiam3AAUTdBF4TRaUdYHT+bW4QJS4TSKw2EIc5EhRxTCAQLCB+ohDrjCd1lhNuHWn1wKho3plDgjvUDOEw4HAodZ4b+A55QLwxtJGoQosWUslLh9Q7+iugVhUKiDCClU7Zs63g950EX8dJVkwwCKZJIj6KG-7l558r3B6PD6fU98RnP4sRxAYqmJepGhAZpqjscceDA9RhxzIEQTtXZDiBO4YVfJ8JiYTscGwo4PinXF804QtoCycJwjLTZMIhUjcKOfCikI4jWPI3s6HiRIkhYeR+XyJU2BTQg2CsaAABkmFqFoC06SMWWjaARnGaY5k8BxYJwMsm0gU44QRFRPhzeVkPU1YjJMjx4WuTEAVxVSw0gIFwAU1UyXkxTaWwRkzxnDkv1lPkBRXNdtl2TcuSvH9XGVVUjxXLUdVtGKIUNY0zRXS1rUysFdgVJh6WgHAmHEd0SU-eLvzzNzpw8irx0nYCYEa3p1JXAA5Kq0xJNMMyzCjcyo9pgCLcIAEZGIrOwq32Gs6wbIr7RUMq5Uq6qPXqbsaD4udfS6jqgTSkMOvlM6byKSA8BNYYHLMslTOuU4LssJkjXjM0AEkcCQR0QCPN7EWCzqmsDAAzR1PAh1C1IggGgc4FQnsua5hsgTN9LG4dlKm6BizmtZ1gWpacNresINVVGQegMGbh7bEEf9dygQabA2dC+rwpvJdIAA58OI3aA6ulBqel-Q99zfICAxAnprIgvzVXgxCaBV66kfQpisr2aAcOhdi3y4rsjbIg7LK6wmgTohiyaMniTlNgiiIt42Dr4uJ60EihIFwfJChKAOg78hIeEJnXJAgwZCBTWSUwmFMZlmXS7H0lYOPN7MkaslyIJzj2Wm19nms8hTI98qvHwC+lgCZK6Lz5hwIuAYWXzNku4slk7paS6AVTVYX0t1YviIl7c5VOxWWtHy65-lY624VSAoHvN8u-d4je+nxLkrVVQ143mAF8sbVx+7yeV5n8vRzatnQMLoEI7r+lNbL8ubIs-PbeosQ9t6JlhZn2X2CRkhEiPEkXgqozSyRJNAAA4rsRw0dv5xyQcnNOZBdjZ2vrgfGz9sSAgdAQ0uhcY4V2gJweoKCKy+RJPQ+k9cgrN15n3Ve-IO5bwnrgPeCVdyD2Hv+eWLhtQeEgEeZoZCd6EJ+qaaA-U5aAXEU8YAeAiiqjCHwlot9EYc1ahOJ+TUeo7DfCwEAAAvKR2NcbNCIRNGiJN5qykpitGmjZzFPksTY5mh1Wb6NnoY8+JAeZmDClwgUzC7BkgEVLBUg8EH1BbOAaAuCKxT0EffAgQJmHJJ4M3Yh4E8m7AKVrShwS+joSOBk+ky1BgbDqX9Qgy0ZpZDSCkGEFwtHLmYpCdYrt1hlAYCaFc4pbhDLqb1XYBxuzQCmL-WOBMAFEwdmWWpqCGlNN2C0tpHSulHB6WASA4ysJzJhCMh6ZyWIXM2RCGZEI5nvAWaA-iftkgqAAOzRHCJAcI+QUxpDYBJAAbOwQWyCSqtFWVQ9SmlJg4Lwd4ziJcyzTN2Esn4xTSG6PRbsR5kBnIkNciOXJAs7yQGYWSNggtmGsMbuE2+7dO66Pif3RJh9RGqIvu4Tw0icCyNRZPBRZplGhOgOozR2ihW5yyQ1HJLUxzGKKaYiCgoUW+NsemHGo0bZOMAcTWarjKz8mWusama1MCaoQNYqR3tAmRLvlDcloSmVOoXHS3YZIMUQnZTuAeMZZbMNPN4YczL0m7MICY-4JKIK0spfSj+4AkKVIwTU9YzTWlAnaZ06AWLKI9DtrRYBax7mQD2Tmg5+aAlgIEskfw68vIXGDiAYowBG0TiYC2gAUkwVUULmxJCuSaGFk04Vx2GIKbSsw6n4LkQZNYvBwAkEbQgNgTAvIIFOFmgtuZlYv1lWipdK610bq3TuqNxLwJUMDAAK37TgalfbVRJrpGwpeLdOHXm4ay8h-qZ6BqHilFR75xF8qkdAGRuicq-SUUwUDWopVaNCEem+TqDHUOVe1T96b1Q2rtSoOxeq-4GqJi4smFMzVU1WhBa1HEtX+KOhhqpgY3XsIia3H90TvVZoAwfYDaoQ3gckQKyNEIWmwcUcoupSGLDStQ1m+VJ1FVAmwzGtCg7ID9WAINeoxG8b6qLas6ajtyxuOox4ta7gjxEnfcAB1fYgmqa0wrdyt7yWaPuiac9LArBAxeG4KAr1T2IF89u1zLhvq5XYJulg0AkyTw4zdWG4B4aqu6vGuLCBEuph1fYvOyz-6TWmqWSjFnqwWto0CR0zp2zJhIm85Lc9OYIG5hxiNCZ0ahdXHZRmPXwuXok4QfjQiuUOjVIkZwpIbzZfWiVCDYms0TdBgN7LMaVZAhfU+3YGsU0VLjZhn+jjjMlZLWZt54D-aUF1AUNtocbvsCgAIB0vBl3aAsPpcW6C1VAnjonZOqc5jeCIQeuN-QDs3pcyodeAohYTjie6rjQIYenxmojoJQHCCw5m46NJqpoYsHUV96G9YqAKhcBB6AM0kffpRzjyAWQMcsaxwz1J0ACdE8+zI0nTByfTbcJI2itPp709PmkZnXGBPY9PuzznCBic87JxTywVO0gbcPUgftfYv6-eBHulZZ31lrFAUAA)