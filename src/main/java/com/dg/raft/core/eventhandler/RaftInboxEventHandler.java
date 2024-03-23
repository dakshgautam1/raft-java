package com.dg.raft.core.eventhandler;

import com.dg.raft.core.RaftServerLogic;
import com.dg.raft.core.models.events.*;
import com.dg.raft.core.queue.RaftServerQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class RaftInboxEventHandler implements Runnable {

    private static final Gson GSON = new Gson();
    private final RaftServerLogic raftServerLogic;
    private final RaftServerQueue<String> inboxQueue;
    private final Map<EventType, Consumer<String>> eventHandlers = new HashMap<>();

    public RaftInboxEventHandler(RaftServerLogic raftServerLogic, RaftServerQueue<String> inboxQueue) {
        this.raftServerLogic = raftServerLogic;
        this.inboxQueue = inboxQueue;
        registerEventHandlers();
    }

    private void registerEventHandlers() {
        eventHandlers.put(EventType.APPEND_ENTRY, eventAsString -> {
            //if (event instanceof AppendEntryEvent) {
                log.info("This is being calledd.....");
            raftServerLogic.handleAppendEntries(GSON.fromJson(eventAsString, AppendEntryEvent.class));
//            } else {
//                log.info("Unable to recognize this event as the instance");
//            }
        });

        eventHandlers.put(EventType.APPEND_ENTRY_RESPONSE, eventAsString -> {
            // if (event instanceof AppendEntryEventResponse) {
            raftServerLogic.handleAppendEntryResponse(GSON.fromJson(eventAsString, AppendEntryEventResponse.class));
            //}
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

        eventHandlers.put(EventType.ADD_NEW_COMMAND, eventAsString -> {
//            if (event instanceof AddNewCommandEvent) {
                raftServerLogic.applicationSubmitCommand(GSON.fromJson(eventAsString, AddNewCommandEvent.class));
            //}
        });

        eventHandlers.put(EventType.SEND_HEART_BEAT, event -> {
                raftServerLogic.updateFollowers();

        });
    }


    @Override
    public void run() {
        while (true) {
            String eventAsString = inboxQueue.get();
            log.info("Consuming the events from the inbox queue - {}", eventAsString);

            Event event = GSON.fromJson(eventAsString, Event.class);

            log.info("Event is here - {}", event);

            Consumer<String> handler = eventHandlers.get(event.getEventType());
            if (handler != null) {
                handler.accept(eventAsString);
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
