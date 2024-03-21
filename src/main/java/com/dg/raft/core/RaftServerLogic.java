package com.dg.raft.core;


import com.dg.raft.core.models.RaftLogData;
import com.dg.raft.core.models.ServerMetadata;
import com.dg.raft.core.models.events.AddNewCommandEvent;
import com.dg.raft.core.models.events.AppendEntryEvent;
import com.dg.raft.core.models.events.AppendEntryEventResponse;
import com.dg.raft.core.queue.RaftServerOutboxUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                            List<RaftLogData> entries = raftLog.getLogDataList().subList(
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
        /*
            Will have to handle the response of the append entry from the followers
         */
        log.info("Received response with body: {}", eventResponse);


    }


    public void handleAppendEntries(AppendEntryEvent event) {
        /*
            - If you are a followed you will get the append entries from the leader.

        */

        log.info("Received the handle appened entries for {}", event);
        boolean isSuccess = this.raftLog.appendEntries(
                event.getPreviousIndex(),
                event.getPrevTerm(),
                event.getNewCommands()
        );

        raftServerOutboxUtil.publishAppendEntryResponseToDestination(
                currentServerMetaData.getServerName(),
                event.getSourceServer(),
                this.currentTerm,
                this.raftLog.getLogDataList().size(),
                isSuccess
        );
    }


    public void lightHeartBeat() {
        /*
            Send heart beats to your
         */
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
