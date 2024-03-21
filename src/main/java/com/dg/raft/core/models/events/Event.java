package com.dg.raft.core.models.events;

import com.dg.raft.core.models.ServerMetadata;
import lombok.*;
import lombok.extern.log4j.Log4j2;

@AllArgsConstructor
@Getter
@Log4j2
@ToString
@NoArgsConstructor(force = true)
public class Event {
    private final EventType eventType;
    private final String sourceServer;
    private final String destServer;
}
