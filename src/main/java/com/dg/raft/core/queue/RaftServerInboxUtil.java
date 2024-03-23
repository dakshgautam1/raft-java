package com.dg.raft.core.queue;


import com.dg.raft.core.models.events.AddNewCommandEvent;
import com.dg.raft.core.models.events.Event;
import com.dg.raft.core.models.events.EventType;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RaftServerInboxUtil {
    private final Gson GSON = new Gson();

    private final RaftServerQueue<String> inboxQueue;

    private final String serverName;

    public void showLog() {
        publishEvent(new Event(EventType.SHOW_LOG, serverName, serverName));
    }

    public void showState() {
        publishEvent(new Event(EventType.SHOW_STATE, serverName, serverName));
    }

    public void sendCommand(String command) {
        publishEvent(
                new AddNewCommandEvent(
                    serverName, serverName,
                        command
                )
        );
    }

    public void sendHeartBeat() {
        publishEvent(new Event(EventType.SEND_HEART_BEAT, serverName, serverName));
    }

    public void becomeLeader() {
        publishEvent(new Event(EventType.BECOME_LEADER, serverName, serverName));
    }

    public void becomeFollower() {
        publishEvent(new Event(EventType.BECOME_FOLLOWER, serverName, serverName));
    }

    private void publishEvent(Event event) {
        inboxQueue.put(GSON.toJson(event)); // Assuming publishMessage accepts RaftEvent type
    }
}
