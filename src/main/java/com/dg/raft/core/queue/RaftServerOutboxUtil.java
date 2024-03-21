package com.dg.raft.core.queue;

import com.dg.raft.core.models.events.AppendEntryEvent;
import com.dg.raft.core.models.RaftLogData;
import com.dg.raft.core.models.events.AppendEntryEventResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@RequiredArgsConstructor
@Log4j2
public class RaftServerOutboxUtil {

    private final RaftServerQueue raftServerQueue;

    public void publishAppendEntryToDestination(String src, int prevIndex, int prevTerm, String dest, List<RaftLogData> entries) {
        // Log before publishing
        log.info("Publishing AppendEntryEvent from {} to {}: prevIndex={}, prevTerm={}, entriesSize={}", src, dest, prevIndex, prevTerm, entries.size());

        raftServerQueue.put(
                new AppendEntryEvent(src, dest, prevIndex, prevTerm, entries)
        );

        // Log after successful publishing
        log.info("Successfully published AppendEntryEvent from {} to {}", src, dest);
    }

    public void publishAppendEntryResponseToDestination(
            String src,
            String dest,
            int currentTerm,
            int logSize,
            boolean isAppendSuccessful) {

        // Log before publishing
        log.info("Publishing AppendEntryResponse: Source: {}, Destination: {}, CurrentTerm: {}, LogSize: {}, IsAppendSuccessful: {}",
                src, dest, currentTerm, logSize, isAppendSuccessful);

        final AppendEntryEventResponse appendEntryEventResponse =
                new AppendEntryEventResponse(
                        src,
                        dest,
                        currentTerm,
                        logSize,
                        isAppendSuccessful
                );

        raftServerQueue.put(appendEntryEventResponse);

        // Log after successfully putting the event into the queue
        log.info("Successfully published AppendEntryResponse to queue for Destination: {}", dest);
    }
}
