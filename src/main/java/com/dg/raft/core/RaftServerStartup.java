package com.dg.raft.core;

import com.dg.raft.core.eventhandler.RaftInboxEventHandler;
import com.dg.raft.core.queue.RaftServerInboxUtil;
import com.dg.raft.core.queue.RaftServerOutboxUtil;
import com.dg.raft.core.queue.RaftServerQueue;
import com.dg.raft.core.socket.RaftServerOutboxSocketProcessor;
import com.dg.raft.core.socket.RaftServerSocketProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.dg.raft.core.ServerConfig.SERVER_PORT_MAPPING;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class RaftServerStartup {
    public static void main(String[] args) {

        log.info("Starting raft server..");

        final String serverName = args[0];
        final int serverPort = SERVER_PORT_MAPPING.get(serverName);


        // Outbox Queue
        final RaftServerQueue outboxQueue = new RaftServerQueue();


        // Raft Server Outbox
        final RaftServerOutboxUtil raftServerOutboxUtil = new RaftServerOutboxUtil(
                outboxQueue
        );

        // Raft Server Logic
        final RaftServerLogic raftServerLogic = new RaftServerLogic(
                Integer.parseInt(serverName),
                serverName,
                serverPort,
                true,
                raftServerOutboxUtil
        );

        final Thread raftServerOutboxSocketProcessorThread = new Thread(
                new RaftServerOutboxSocketProcessor(serverPort, outboxQueue)
        );

        raftServerOutboxSocketProcessorThread.start();

        // Inbox Queue
        final RaftServerQueue inboxQueue = new RaftServerQueue();

        // Starting the server that will listen to incoming messages.
        final Thread raftInboxEventHandlerThread = new Thread(
                new RaftInboxEventHandler(raftServerLogic, inboxQueue
                )
        );

        raftInboxEventHandlerThread.start();

        // Starting the socket for listening to messages.
        final Thread raftServerSocketProcessorThread = new Thread(new RaftServerSocketProcessor(
                serverPort,
                inboxQueue
            )
        );

        raftServerSocketProcessorThread.start();

        RaftServerInboxUtil raftServerInboxUtil = new RaftServerInboxUtil(
                inboxQueue, serverName
        );

        startCli(raftServerInboxUtil);

    }
    private static void startCli(RaftServerInboxUtil inboxUtil) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        log.info("Raft Server CLI started. Type 'help' for a list of commands, or 'exit' to stop.");

        try {
            String input;
            while (true) {
                System.out.print("> ");
                input = reader.readLine();
                if ("exit".equalsIgnoreCase(input.trim())) {
                    break;
                } else if ("help".equalsIgnoreCase(input.trim())) {
                    showHelp();
                } else {
                    handleCommand(input, inboxUtil);
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading input: " + e.getMessage());
        }
        log.info("CLI stopped.");
    }

    private static void handleCommand(String input, RaftServerInboxUtil inboxUtil) {
        String[] parts = input.split(" ", 2);
        String command = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1] : null;

        switch (command) {
            case "log":
                inboxUtil.showLog();
                break;
            case "state":
                inboxUtil.showState();
                break;
            case "command":
                if (argument != null) {
                    inboxUtil.sendCommand(argument);
                } else {
                    log.info("Command requires an argument.");
                }
                break;
            case "heartbeat":
                inboxUtil.sendHeartBeat();
                break;
            case "leader":
                inboxUtil.becomeLeader();
                break;
            case "follower":
                inboxUtil.becomeFollower();
                break;
            case "":
                break;
            default:
                log.info("Invalid command. Please try 'help' for a list of valid commands.");
                break;
        }
    }

    private static void showHelp() {
        log.info("Available commands:");
        log.info("  log                - Show the state of the Raft log on this server");
        log.info("  state              - Show other Raft state on this server");
        log.info("  command <cmd>      - Add a new command on the Raft leader");
        log.info("  heartbeat          - Manually issue a heartbeat to update followers");
        log.info("  leader             - Become a leader");
        log.info("  follower           - Become a follower");
        log.info("  help               - Show this list of commands");
        log.info("  exit               - Exit the CLI");
    }
}
