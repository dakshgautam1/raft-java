# Java Raft Implementation

This Java project implements the Raft consensus algorithm, aimed at achieving consensus efficiently in distributed systems. Raft is known for its simplicity and understandability compared to older consensus algorithms like Paxos.

## Project Architecture

TODO: Need to add the architecture diagram. 

The project is modular, focusing on clarity and simplicity:

- **Server Node**: Manages connections from clients and other nodes within a Raft cluster.
- **Socket Layer**: Facilitates communication across cluster nodes.
- **Log Module**: Handles log operations such as appending and compaction.
- **Consensus Module**: Implements Raft's core functionalities including log replication, leader election, and state management.


Components interact via well-defined interfaces for ease of testing and development.

## Testing

### CLI Testing

A CLI tool is provided for manual simulation and testing:

- **Build the package**: `mvn package`
- **Start a Node**: `java -jar target/raft-java-1.0-SNAPSHOT.jar <server_name>`. The `server_name` can be from **1, 2, 3, 4, 5**
- **Help**: `help` will show all the options available in the CLI. 
```
Available commands:
log                - Show the state of the Raft log on this server
state              - Show other Raft state on this server
command <cmd>      - Add a new command on the Raft leader
heartbeat          - Manually issue a heartbeat to update followers
leader             - Become a leader
follower           - Become a follower
help               - Show this list of commands
exit               - Exit the CLI
```

### Unit Tests (WIP)

Unit tests, crucial for verifying component behavior, are conducted using JUnit:

- **Run Tests**: Execute `./gradlew test` from the project root to run all unit tests.
- **Coverage**: Focus on leader election, log replication, and fault tolerance to ensure robustness and correctness.

## Project Milestones & Roadmap

### Upcoming Tasks
- [ ] Implement `handleAppendEntryResponse`. This function is responsible for handling the `handleAppendEntry` response from the followers. 


### Completed Tasks
- [x] Basic setup with maven. The aim is to make sure that the jar is created successfully with all the third party dependencies. Not just build but also be able to run it successfully.
- [x] Unit testing setup with JUnit
- [x] Implement both **inbound socket processor** & **outbound socket processor** .
- [x] Implement both **inbound queue** & **outbound queue**.
- [x] Implement the core logic of `Log`
- [x] Thread which listens to the UDP packets from the internet. This thread holds **inbound socket processor**. This thread grabs the message from outside, converts the message into events and puts it into the **inbound queue**
- [x] Thread which polls the **inbound queue** and forwards the request to `RaftServerLogic`
- [x] Thread which sends the UDP package to the internet.  This thread polls the **outbound queue**
- [x] Main thread which runs the CLI application.
- [x] Decide on serialization & de-serialization strategy. Initially, I felt that both **inbound queue** & **outbound queue** should hold the `Event` type, but this strategy make the deserialization more complicated. Hence decided to create both the queues with different types - **inbound queue** with `string` type & **outbound queue** with `event` type.


This section provides a clear view of what has been achieved and what is still on the horizon, keeping both the project team and external contributors informed about the project's status and direction.


## Conclusion

This README outlines the essentials for developing, testing, and understanding the Java Raft implementation. It includes the architecture, testing instructions using a CLI tool, and guidelines for unit testing, providing a foundation for effective project development and debugging.
