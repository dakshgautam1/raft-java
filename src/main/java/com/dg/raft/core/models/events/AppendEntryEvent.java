package com.dg.raft.core.models.events;

import com.dg.raft.core.models.RaftLogData;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.util.List;


@Getter
@Log4j2
@Setter
@NoArgsConstructor(force = true)
public class AppendEntryEvent extends Event {
    private final int previousIndex;
    private final int prevTerm;
    private final List<RaftLogData> newCommands;

    public AppendEntryEvent(
            final String sourceServer,
            final String destServer,
            int previousIndex,
            int prevTerm,
            final List<RaftLogData> newCommands
    ) {

        super(EventType.APPEND_ENTRY, sourceServer, destServer);
        this.previousIndex = previousIndex;
        this.prevTerm = prevTerm;
        this.newCommands = newCommands;
    }
}
