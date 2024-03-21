package com.dg.raft.core.eventhandler;

import com.dg.raft.core.RaftServerLogic;
import com.dg.raft.core.models.events.*;
import com.dg.raft.core.queue.RaftServerQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class RaftInboxEventHandler implements Runnable {

    private final RaftServerLogic raftServerLogic;
    private final RaftServerQueue inboxQueue;
    private final Map<EventType, Consumer<Event>> eventHandlers = new HashMap<>();

    public RaftInboxEventHandler(RaftServerLogic raftServerLogic, RaftServerQueue inboxQueue) {
        this.raftServerLogic = raftServerLogic;
        this.inboxQueue = inboxQueue;
        registerEventHandlers();
    }

    private void registerEventHandlers() {
        eventHandlers.put(EventType.APPEND_ENTRY, event -> {
            if (event instanceof AppendEntryEvent) {
                log.info("This is being calledd.....");
                raftServerLogic.handleAppendEntries((AppendEntryEvent) event);
            } else {
                log.info("Unable to recognize this event as the instance");
            }
        });

        eventHandlers.put(EventType.APPEND_ENTRY_RESPONSE, event -> {
            if (event instanceof AppendEntryEventResponse) {
                raftServerLogic.handleAppendEntryResponse((AppendEntryEventResponse) event);
            }
        });

        // For event types without additional data or without a specific subclass
        eventHandlers.put(EventType.SHOW_LOG, event -> {
                raftServerLogic.showLog();

        });

        eventHandlers.put(EventType.BECOME_FOLLOWER, event -> {
                raftServerLogic.becomeFollower();

        });

        eventHandlers.put(EventType.BECOME_LEADER, event -> {
                raftServerLogic.becomeLeader();

        });

        eventHandlers.put(EventType.SHOW_STATE, event -> {
                raftServerLogic.showState();

        });

        eventHandlers.put(EventType.ADD_NEW_COMMAND, event -> {
            if (event instanceof AddNewCommandEvent) {
                raftServerLogic.applicationSubmitCommand((AddNewCommandEvent) event);
            }
        });

        eventHandlers.put(EventType.SEND_HEART_BEAT, event -> {
                raftServerLogic.updateFollowers();

        });
    }


    @Override
    public void run() {
        while (true) {
            Event event = inboxQueue.get();
            log.info("Consuming the events from the inbox queue - {}", event);
            Consumer<Event> handler = eventHandlers.get(event.getEventType());
            if (handler != null) {
                handler.accept(event);
            } else {
                System.err.println("Unhandled event type: " + event.getEventType());
            }
        }
    }

    // Example of an additional handler method
    // private void handleSomeOtherEvent(Event event) {
    //     // Specific logic for SOME_OTHER_EVENT
    // }
}
