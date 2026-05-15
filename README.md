# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

Link to a Server Design Sequence Diagram:

Edit:
https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNMY7gjySp6lKoDyySIVPbKjdnjAFKaUMBze11egAKKWlTYAgFd23Ur3YrmeqBJzBYbjObqYCMhbLMNQbx1A1TJXGgNB+XyNXoKFmTiaHReqoelD1NA+BAIOMU+482n82ogVXol3sgY87naAfqQXGWoKDgcTXS7Q9z196d8+nDwPohQ+PUY4CH+KTzD97cC+7zxfLg96t2Ijf3GFPYvYtF4tSdrBvuHNj6xavIaSpLPU+wgieertBANZoOByyXHGlDNkmGD1OEThOBmEygZ8MAQcCyzQfEsHwYh+xXOgHCmF4vgBNA7CMjEIpwGG0hwAoMAADIQFkhTocwDrUL6zRtF0vQGOo+RoLhiqfJCIK-P8gJUSh8KVP+vogaWimQes+h-DsXzQo8AEiVQSIwAg-HihifECQSRJgKSz6GJuNJXgyTJjgpXIXludLXpU84wGKEoujKcolu8SqYCqgYai6RocBAahoAA5MwVpoo2gGOm2MAdl264eZZvougAcp2CARlGMaFEBaHIMmMCpjhozLFmqg5vMEEFkW9TilQJpIMu-lLNRDZ2gm5Tuc6E5rol6owE0RmqTA2RbWgIDQCi4BlQVonFsy0yntASAAF4oBw9UoNGskaS1JRgCmTgAIwZt1-J9XmYyDdAw1oKNyDLipJkwNNtHuYKl7BfUu5yCgD7xMep7nvD-JzsKfpLn6GNrrDc0PLCvqOeKGSqL+mDafCFXAXhenzIRSHfKR5G1qz6nNSTQmYdh8n4SzRHs6enMIdzyE0XR3h+P4XgoOgMRxIkivK45vhYEJgpAfUDTSGGPFhu0YbdD00mqLJwwc3B6DPa+5m+rb8G007mktlZTo2fxWsOb7h7OWorlHRUWP0jAjJgKj6MwXbaBTl5wU4-UEX3oT8iyvKLv2ytGqozKaUZdlMC5Tk+UkwtxW1aHDPOudeqXTdd2Rg9jUO4mrUYe1n3fWMPV-QNhZAztoPjcaF1QNdeQFFN9a0bNlnWQXROFXDQWDkYKDcPup4Yiv8iJ7yyc3rj0jb0yhgH8Atdae79Sa4HP4IH+7vHTULxQx3lT8zAWGdSML+MtPBywCCiZc-hsDig1DxNEMAADiSoNA6zro0eBJtzb2CVDbcW8cHZ3zJsWHOhQ6a60KvUZAOQHJoiDiSUO5Rw6qB8tHPexCj4zhCkKVO4p06Pm0FnCecdXZ5wJnwzORdVBZRytacui9vTkOrqVYmqCzqkSbrde6j1Yy807q9d6X0ur91+rmIeQ1R5jWXGoqeV0Z6S2hhXJe3tr70JkBvCOlCwCIKzBidhV4U7hR4QgpBgUk7Y0rgorBcw4AQC7CgcAT1lEEPfA-Ghz9X6EPfjpZYkS1CIQaDAboVwdE-y7m9P+gtDE5NUHkgpQCGwgIYv4DgAB2NwTgUBOBiGGYIcB2IADZ4AjkMF4wwRRSlkJOvrVoHRMHYIbmReO31slKiqmBKWc9eZJLhPUYhSyxg5NWQRUWX9SGOKKkjdEIyMRwCGbQkOxMqRuKYZHJkMc2EhOPmE0KuM06iLPPwmKxCEqqnzhnYARh0qSJLmXLAcjPbWRKt2RJntKrzPUS3BqCTqioT5qU-RfcB4mPzMPYsI0LGCPiE3Wxc8ZZwqrs4h5rjQkRwuSgK5IzMZPP8WQSAKIglzCfGvR2hD6g3L3CgKmNNTnyMmTAQB+ylQAElpB5g+uEYIgQv6bPgHi8pADllzGVaq9Vmr7ENPlpYbetlNgqyQAkMAlquwQBtQAKQgOKflhh-DJFAGqMZr0Jkf0aE0Zkkkeg5JwUI9AezsAIGAJaqA0TbJQEogAdRYIq023wclGtZmqjVWrsUezpjs3B8EY1xoTUm6AaaM1ZpBDmlVeaTUnLfmc+oAArd1aArluspmktyQrHnMueVHN5Zb0C+JPt87hEpr4CKBSI+dEipGlxkbCps4TWzthrsivWko0XWI0a3LRTUi0vTaqmAx8rCX9WJWYslYMKVUsajSmam721-MFa2deI7mFXMbVOr5XCAkShGQIxtwKkp+jAOiGY0YYCQBgH2tAxpioFFKDC8wnqHEyq9kVRFt8UXFhGTVLsmj27FJ1Xonu+qjHZiJQDEl9RCSUFQMuEZwwIXF2kWiAAhG+heH68PWQ5ctEFMG4MIaQyhtD7RAwzEQ-EOQmUBQmjNFWUMaAejge2iKWqfGiP7vk7MfT5GT2UfPbimjqZ0yjHo71RjgNizolmNqfQ5YNOIa0wJ+xjLGH1D8FoS5SoMTDEbWsYYaAUA2rE4fD5HD-EZFjaAQwMSONKhlMMGKkHkUluQ92yVL83YZK3UGwBRSrO6Lav-b6Zr6Lyy8PG219qmvykQIGWAwBsCxsILYmA-rsOoINkbE2ZtejGHwaTZJA3pXwqdC4gLMAQDcDwD4hLfjT71HPjvQwJoEArigBoRh-iduX3LAd5xJ2tsyAvuiC7nq3TXc-UR-LHW8BFfSe+TJwFC0nQvd3WrXV7FAA

Presentation Mode:

https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNMY7gjySp6lKoDyySIVPbKjdnjAFKaUMBze11egAKKWlTYAgFd23Ur3YrmeqBJzBYbjObqYCMhbLMNQbx1A1TJXGgNB+XyNXoKFmTiaHReqoelD1NA+BAIOMU+482n82ogVXol3sgY87naAfqQXGWoKDgcTXS7Q9z196d8+nDwPohQ+PUY4CH+KTzD97cC+7zxfLg96t2Ijf3GFPYvYtF4tSdrBvuHNj6xavIaSpLPU+wgieertBANZoOByyXHGlDNkmGD1OEThOBmEygZ8MAQcCyzQfEsHwYh+xXOgHCmF4vgBNA7CMjEIpwGG0hwAoMAADIQFkhTocwDrUL6zRtF0vQGOo+RoLhiqfJCIK-P8gJUSh8KVP+vogaWimQes+h-DsXzQo8AEiVQSIwAg-HihifECQSRJgKSz6GJuNJXgyTJjgpXIXludLXpU84wGKEoujKcolu8SqYCqgYai6RocBAahoAA5MwVpoo2gGOm2MAdl264eZZvougAcp2CARlGMaFEBaHIMmMCpjhozLFmqg5vMEEFkW9TilQJpIMu-lLNRDZ2gm5Tuc6E5rol6owE0RmqTA2RbWgIDQCi4BlQVonFsy0yntASAAF4oBw9UoNGskaS1JRgCmTgAIwZt1-J9XmYyDdAw1oKNyDLipJkwNNtHuYKl7BfUu5yCgD7xMep7nvD-JzsKfpLn6GNrrDc0PLCvqOeKGSqL+mDafCFXAXhenzIRSHfKR5G1qz6nNSTQmYdh8n4SzRHs6enMIdzyE0XR3h+P4XgoOgMRxIkivK45vhYEJgpAfUDTSGGPFhu0YbdD00mqLJwwc3B6DPa+5m+rb8G007mktlZTo2fxWsOb7h7OWorlHRUWP0jAjJgKj6MwXbaBTl5wU4-UEX3oT8iyvKLv2ytGqozKaUZdlMC5Tk+UkwtxW1aHDPOudeqXTdd2Rg9jUO4mrUYe1n3fWMPV-QNhZAztoPjcaF1QNdeQFFN9a0bNlnWQXROFXDQWDkYKDcPup4Yiv8iJ7yyc3rj0jb0yhgH8Atdae79Sa4HP4IH+7vHTULxQx3lT8zAWGdSML+MtPBywCCiZc-hsDig1DxNEMAADiSoNA6zro0eBJtzb2CVDbcW8cHZ3zJsWHOhQ6a60KvUZAOQHJoiDiSUO5Rw6qB8tHPexCj4zhCkKVO4p06Pm0FnCecdXZ5wJnwzORdVBZRytacui9vTkOrqVYmqCzqkSbrde6j1Yy807q9d6X0ur91+rmIeQ1R5jWXGoqeV0Z6S2hhXJe3tr70JkBvCOlCwCIKzBidhV4U7hR4QgpBgUk7Y0rgorBcw4AQC7CgcAT1lEEPfA-Ghz9X6EPfjpZYkS1CIQaDAboVwdE-y7m9P+gtDE5NUHkgpQCGwgIYv4DgAB2NwTgUBOBiGGYIcB2IADZ4AjkMF4wwRRSlkJOvrVoHRMHYIbmReO31slKiqmBKWc9eZJLhPUYhSyxg5NWQRUWX9SGOKKkjdEIyMRwCGbQkOxMqRuKYZHJkMc2EhOPmE0KuM06iLPPwmKxCEqqnzhnYARh0qSJLmXLAcjPbWRKt2RJntKrzPUS3BqCTqioT5qU-RfcB4mPzMPYsI0LGCPiE3Wxc8ZZwqrs4h5rjQkRwuSgK5IzMZPP8WQSAKIglzCfGvR2hD6g3L3CgKmNNTnyMmTAQB+ylQAElpB5g+uEYIgQv6bPgHi8pADllzGVaq9Vmr7ENPlpYbetlNgqyQAkMAlquwQBtQAKQgOKflhh-DJFAGqMZr0Jkf0aE0Zkkkeg5JwUI9AezsAIGAJaqA0TbJQEogAdRYIq023wclGtZmqjVWrsUezpjs3B8EY1xoTUm6AaaM1ZpBDmlVeaTUnLfmc+oAArd1aArluspmktyQrHnMueVHN5Zb0C+JPt87hEpr4CKBSI+dEipGlxkbCps4TWzthrsivWko0XWI0a3LRTUi0vTaqmAx8rCX9WJWYslYMKVUsajSmam721-MFa2deI7mFXMbVOr5XCAkShGQIxtwKkp+jAOiGY0YYCQBgH2tAxpioFFKDC8wnqHEyq9kVRFt8UXFhGTVLsmj27FJ1Xonu+qjHZiJQDEl9RCSUFQMuEZwwIXF2kWiAAhG+heH68PWQ5ctEFMG4MIaQyhtD7RAwzEQ-EOQmUBQmjNFWUMaAejge2iKWqfGiP7vk7MfT5GT2UfPbimjqZ0yjHo71RjgNizolmNqfQ5YNOIa0wJ+xjLGH1D8FoS5SoMTDEbWsYYaAUA2rE4fD5HD-EZFjaAQwMSONKhlMMGKkHkUluQ92yVL83YZK3UGwBRSrO6Lav-b6Zr6Lyy8PG219qmvykQIGWAwBsCxsILYmA-rsOoINkbE2ZtejGHwaTZJA3pXwqdC4gLMAQDcDwD4hLfjT71HPjvQwJoEArigBoRh-iduX3LAd5xJ2tsyAvuiC7nq3TXc-UR-LHW8BFfSe+TJwFC0nQvd3WrXV7FAA

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
