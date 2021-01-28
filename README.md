# P2Ps Protocol: P2P Streaming

- Java: 1.8
- Build System: Gradle 6.7

### Module

- streaming: the P2Ps protocol module;
- sample: a sample using the P2Ps protocol via console.

### Build

Build a jar file including all modules

> ./gradlew shadowJar

### Run

> java -jar sample/build/libs/sample-0.0.1-all.jar

You can follow these steps for testing:

1. Run the jar file, then the app main server will start listening on port 9876;
2. Connect to another peer in your network by choosing option 1 and typing host address (can be 127.0.0.1);
3. To establish the connection between the pairs, do the same connect operation in the other peer, typing this peer IP as the host (step not needed for localhost);
4. Confirm the connection with the other peer by choosing the option 3;
5. Do a streaming by choosing option 2 and typing some characters/bytes;
