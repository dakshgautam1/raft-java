package com.dg.raft.core;


import com.dg.raft.core.models.RaftLogData;
import com.dg.raft.core.models.ServerMetadata;
import com.dg.raft.core.models.events.AddNewCommandEvent;
import com.dg.raft.core.models.events.AppendEntryEvent;
import com.dg.raft.core.models.events.AppendEntryEventResponse;
import com.dg.raft.core.models.events.HeartBeatEvent;
import com.dg.raft.core.queue.RaftServerOutboxUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dg.raft.core.ServerConfig.createServerMetadata;
import static com.dg.raft.core.ServerConfig.getAllServers;

@Log4j2
public class RaftServerLogic {

    @Setter
    @Getter
    boolean isLeader;
    private final ServerMetadata currentServerMetaData;
    private int currentTerm;
    private final RaftLog raftLog;
    private final RaftServerOutboxUtil raftServerOutboxUtil;
    /**
     * for each server, index of the next log entry
     * to send to that server (initialized to leader
     * last log index + 1)
     */
    private final Map<ServerMetadata, Integer> followerNextIndex;

    public RaftServerLogic(
            int currentTerm,
            String serverName,
            int serverPort,
            boolean isLeader,
            RaftServerOutboxUtil raftServerOutboxUtil
    ) {
        this.currentTerm = currentTerm;
        this.raftLog = new RaftLog(currentTerm);
        this.currentServerMetaData = new ServerMetadata(serverName, serverPort);
        this.isLeader = isLeader;
        this.raftServerOutboxUtil = raftServerOutboxUtil;
        this.followerNextIndex = new HashMap<>();
    }


    @Override
    public String toString() {
        return String.format(
                "RaftServerLogic{isLeader=%s, currentServerMetaData=%s, currentTerm=%d, raftLog=%s, raftServerOutboxUtil=%s, followerNextIndex=%s}",
                isLeader,
                currentServerMetaData, // Assuming ServerMetadata has a meaningful toString method
                currentTerm,
                raftLog, // Assuming RaftLog has a meaningful toString method
                raftServerOutboxUtil.getClass().getSimpleName(), // Just the class name of raftServerOutboxUtil
                followerNextIndex
        );
    }

    public void applicationSubmitCommand(AddNewCommandEvent event) {
        try {
            log.info("Client Log appended");
            raftLog.appendNewEntry(this.currentTerm, event.getCmd());
        } catch (Exception e) {
            log.error("Error while submitting command: " + e.getMessage(), e);
        }
    }

    public void updateFollowers() {
        try {
            assert isLeader: "This node is not a leader, hence cannot trigger append entries to the follower.";

            final List<ServerMetadata> allServers = getAllServers(currentServerMetaData.getServerName());

            allServers.forEach(server -> {
                    try {
                        if (!server.equals(currentServerMetaData)) {
                            int prevIndex = followerNextIndex.get(server) - 1;
                            int prevTerm = raftLog.getLogDataList().get(prevIndex).getTerm();
                            final List<RaftLogData> entries = raftLog.getLogDataList().subList(
                                    prevIndex + 1,
                                    raftLog.getLogDataList().size()
                            );

                            raftServerOutboxUtil.publishAppendEntryToDestination(
                                    currentServerMetaData.getServerName(),
                                    prevIndex,
                                    prevTerm,
                                    server.getServerName(),
                                    entries
                            );
                        } else {
                            log.info("Skipping sending the data to myself.");
                        }
                    } catch (Exception e) {
                        log.error("Error while updating follower " + server.getServerName() + ": " + e.getMessage(), e);
                    }
                }
            );
        } catch (AssertionError ae) {
            log.error("Attempted to update followers without being the leader: " + ae.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while updating followers: " + e.getMessage(), e);
        }
    }
    public void handleAppendEntryResponse(AppendEntryEventResponse eventResponse) {
        log.debug("Starting to handle AppendEntryResponse: {}", eventResponse);

        ServerMetadata serverMetadata = createServerMetadata(eventResponse.getSourceServer());
        log.debug("Created ServerMetadata for source server: {}", serverMetadata);

        if (eventResponse.isSuccess()) {
            log.info("AppendEntry was successful for the follower: {}", eventResponse.getSourceServer());
            int newNextIndex = eventResponse.getMatchIndex() + 1;
            log.debug("Updating next index for server {} to {}", serverMetadata, newNextIndex);
            followerNextIndex.put(serverMetadata, newNextIndex);
        } else {
            log.info("AppendEntry FAILED for the follower: {}, decreasing the index by 1", eventResponse.getSourceServer());
            final int currentFollowerNextIndex = followerNextIndex.get(serverMetadata);
            int newNextIndex = currentFollowerNextIndex - 1; // Ensure the index does not go below 1
            log.debug("Decreasing next index for server {} to {}", serverMetadata, newNextIndex);
            followerNextIndex.put(serverMetadata, newNextIndex);
        }

        // Additional logging to confirm the state after handling the response
        log.debug("Current next index for server {}: {}", serverMetadata, followerNextIndex.get(serverMetadata));
    }



    public void handleAppendEntries(AppendEntryEvent event) {
        log.info("Handling append entries request from {}: {}", event.getSourceServer(), event);

        boolean isSuccess = this.raftLog.appendEntries(
                event.getPreviousIndex(),
                event.getPrevTerm(),
                event.getNewCommands()
        );

        int matchIndex = isSuccess ? this.raftLog.getLogDataList().size() - 1 : -99;
        // Log the outcome of the appendEntries operation
        if (isSuccess) {
            log.info("Append entries successful. Notifying {} with success response and index {}",
                    event.getSourceServer(), matchIndex);
        } else {
            log.warn("Append entries failed. Notifying {} with failure response.", event.getSourceServer());
        }

        // Simplified call to publish response
        raftServerOutboxUtil.publishAppendEntryResponseToDestination(
                currentServerMetaData.getServerName(),
                event.getSourceServer(),
                this.currentTerm,
                matchIndex, // Use the calculated index based on success/failure
                isSuccess
        );
    }



    public void handlerLeaderHeartBeat(HeartBeatEvent heartBeatEvent) {

        if (this.isLeader()) {
            log.info("Sending heartbeat events to all the followers.");
            this.updateFollowers();
        } else {
            log.info("HeartBeatEvent is not applicable for followers.");
        }
    }

    public void showLog() {
        log.info("Showing the logs here");
        raftLog.printLog();
    }

    public void showState() {
        log.info("*********** Node State ***********");
        log.info(this);
        log.info("**********************************");

    }

    public void becomeLeader() {
        log.info("Making this server as leader");
        this.isLeader = true;
        log.info("Setting up the nextIndex");
        getAllServers(currentServerMetaData.getServerName())
                .forEach(s -> followerNextIndex.put(s, raftLog.getLogDataList().size()));

    }

    public void becomeFollower() {
        log.info("Making this server as follower");
        this.isLeader = false;
    }
}
