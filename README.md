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

## Conclusion

This README outlines the essentials for developing, testing, and understanding the Java Raft implementation. It includes the architecture, testing instructions using a CLI tool, and guidelines for unit testing, providing a foundation for effective project development and debugging.
