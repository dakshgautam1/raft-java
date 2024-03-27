package com.dg.raft.core.models.events;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter
@Log4j2
@Setter
@NoArgsConstructor(force = true)
public class HeartBeatEvent extends Event{

    public HeartBeatEvent(String sourceServer, String destServer)
    {
        super(EventType.SEND_HEART_BEAT, sourceServer, destServer);
    }

}
