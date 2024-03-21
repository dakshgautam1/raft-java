package com.dg.raft.core.models.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
@Log4j2
@Getter
@ToString
@Setter
@NoArgsConstructor(force = true)
public class AddNewCommandEvent extends Event {
    private final String cmd;
    public AddNewCommandEvent(String sourceServer,
                              String destServer,
                              String cmd) {
        super(EventType.ADD_NEW_COMMAND, sourceServer, destServer);
        this.cmd = cmd;
    }
}
