package com.dg.raft.core.models.events;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

@Getter
@Log4j2
@ToString
@Setter
@NoArgsConstructor(force = true)
public class AppendEntryEventResponse extends Event {
    private int currentTerm;
    private int matchIndex;
    boolean isSuccess;

    public AppendEntryEventResponse(String sourceServer,
                                    String destServer,
                                    int currentTerm,
                                    int matchIndex,
                                    boolean isSuccess) {
        super(EventType.APPEND_ENTRY_RESPONSE, sourceServer, destServer);
        this.isSuccess = isSuccess;
        this.currentTerm = currentTerm;
        this.matchIndex = matchIndex;
    }
}
