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

[Server Sequence Diagram](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGVIE4DdF0gRwK6QHYGMYAREAQwHMESBbAKBpL2AHsFoBhcEXYGgBxISg8IfjmBpkiPgKEiSY6AAl5AEygJpgkMNHj4ybZE2zd0QiWAkAgngIBnOzRUWSAIxJ2YK13W4IAnnb8wjhk0AAMAHQAnDQUTJi8AMQALADMABwATNF40EmcZAAWwK7g2NAASpBkIHbAlKBMODQcXAoAtAB8EigIAFwA2gAKAPKwACoAutAA9JieCAA6OADeAEQLiDjUkOv969DrADRH-A4A7iwq+4cn65BUJCDgt0cAvjT6fdDdSqrqfrQDZbBA7Kh7A5HU7rc52K4IG5Q+6PZ6vZGfZQ4NSoP7fQxAhA1OrARAACmqtXqiGqWEg9QAlF9EEhDL8euZLDZ7HYgWRIMAAKqLMmg8GQJmc6y2el2dnQbxAgBiIGx0GFiCl0Fc-mgYt2NFcRJIAGt1YtoBcwEU9YtxdASOBjSpdZAAB4kxxS7my354lkE82alzMgwEP09b6IIFWJ2QEguiam3AAUTdBF4TRaUdYHT+bW4QJS4TSKw2EIc5EhRxTCAQLCB+ohDrjCd1lhNuHWn1wKho3plDgjvUDOEw4HAodZ4b+A55QLwxtJGoQosWUslLh9Q7+iugVhUKiDCClU7Zs63g950EX8dJVkwwCKZJIj6KG-7l558r3B6PD6fU98RnP4sRxAYqmJepGhAZpqjscceDA9RhxzIEQTtXZDiBO4YVfJ8JiYTscGwo4PinXF804QtoCycJwjLTZMIhUjcKOfCikI4jWPI3s6HiRIkhYeR+XyJU2BTQg2CsaAABkmFqFoC06SMWWjaARnGaY5k8BxYJwMsm0gU44QRFRPhzeVkPU1YjJMjx4WuTEAVxVSw0gIFwAU1UyXkxTaWwRkzxnDkv1lPkBRXNdtl2TcuSvH9XGVVUjxXLUdVtGKIUNY0zRXS1rUysFdgVJh6WgHAmHEd0SU-eLvzzNzpw8irx0nYCYEa3p1JXAA5Kq0xJNMMyzCjcyo9pgCLcIAEZGIrOwq32Gs6wbIr7RUMq5Uq6qPXqbsaD4udfS6jqgTSkMOvlM6byKSA8BNYYHLMslTOuU4LssJkjXjM0AEkcCQR0QCPN7EWCzqmsDAAzR1PAh1C1IggGgc4FQnsua5hsgTN9LG4dlKm6BizmtZ1gWpacNresINVVGQegMGbh7bEEf9dygQabA2dC+rwpvJdIAA58OI3aA6ulBqel-Q99zfICAxAnprIgvzVXgxCaBV66kfQpisr2aAcOhdi3y4rsjbIg7LK6wmgTohiyaMniTlNgiiIt42Dr4uJ60EihIFwfJChKAOg78hIeEJnXJAgwZCBTWSUwmFMZlmXS7H0lYOPN7MkaslyIJzj2Wm19nms8hTI98qvHwC+lgCZK6Lz5hwgRUSAoHvN9ouKiE4slk7paS6AVTVVQFU7gUYGF9LdSMiXtzlU7FZascJzZ0DC6BCO6-pTWy-LmyLPz23qLEe36LLFm+19hJkiJI8kl4VUzVkkloAAcV2Rxo6PuPP7JzTmQXY2czYl3xlvbEgIHTgOIlrQuMcK7QE4PUb+FZfIknQfSeuQVm680Hm3aA-JgDCxfHA3AA8l6JWSmqWeLhtQeEgEeZosD3bwJ+qaaA-U5aAQYU8YAeAiiqjCMXeBx0dzl1HG1TeTUeo7DfCwEAAAvZh2NcbNEgRNGiJN5qykpitGmjYFFPiUao5mh1WYSOXlIggMZ5aXVXvKaxEVgDYLsOQ9hlDF4JV3CPd+9QWzgGIT-HxDVbEtWwQEngzcoHgSBFE2qh85EQVWEcEBFZlqDA2BkyAf1CDLRmlkNIKQYQXGEcuZikJ1iu3WGUBgJoVziluLU3JvVdgHG7NAKYJ9Y4E3PkTB2ZZ0k-yyTk3Y+TCnFNKUccpYBIBNKwp0mE9SHqLJYsskZEJ2kQk6e8bpN9+J+2SCoAA7NEcIkBwj5BTGkNgEkABs7BBZfxKq0AZSD1KaUmMA0BJjOIlzLG03YvSfhxJgWI3AQLdg7MgM5aBrkRx2IFneSA2CyRsEFtg3BjceZmDCkQkhZDIU4Cob44etD-wOMsIwzwLCcBsIBRw3K3CmC8PfPwiwQiRGMtzmEk6ESgTr3ak4lesdzr-LMWo9MONRo220RfYms09GVn5MtdY1M1qYElQgFRzDvZWIJTYqGyL6GWDxS4lFFg0W7DJMC-u-KdwUv3LLbBp5vDDktbk-Jsj-gIogpi1F2L97gCQogsVfR0JbLyQUoERSSnQFBZRHodtaJXzWNGyZcbpmJssbfASyR-Cdy8hcYOIBijACLROJgpaABSTBVSvObEkVZJp3mTU+XHYYgptKzFyWArxBk1i8HACQItCA2BMC8ggU43rCBJtzMrbevLAXDtHeOyd07Z0TPnQg-1SDAwACsG04HRfW1Uwa6R4NFQQperjiUUNJY65ezqx5Ur4TS9wdLoCsJJTlX6rL2VagEdy0IK7xFGsRhzVqG9YkpIlRxKVKh1FytPgqomuiyYUzVVTVaEFtWId1eYg1fZrFQeQWakgFqjWuPRXOslUsFQjzfU2yA6UmH0pCRCH1nCzQ8NycBrlwiwNzufeR6RsHRX-wSTCgae1gAobxvKlNAzpqO3LPonDhi1ruCPESK9wASNiYjYGN1jj3IHuRUI+6JpN0sCsEDF4bgoCvXXYgOzM7WMfl4+wKdLBoBJngfgpFLVYbgHhnB7qAa-MIEC6mGVGi859LPpNaapYsOaerBqvDQJHTOnbMmEihzgs3S5kYYLlqEzozc6uOyjMasee3dxwgDGh5McpQ6NUiRnCkhvDF9aJUv3MJ-QyudnXQYNZi76lWQJz2nt2BrUNe7wLkePlolTqW03qcOXff2lBdQFHLaHfb7AoACAdLwEd2gLD6XFn-eDGkE5JxTmnbwkCl3+v6MtlCgrJ5dyFhOMkTcb34tbteDu-3YzgCB8Z51hAp69cdME1U0MWACNu9DesVBoBPi8C4Ib03l1IAbX2ZJUX0ILv6ZtoZawb5AA)
