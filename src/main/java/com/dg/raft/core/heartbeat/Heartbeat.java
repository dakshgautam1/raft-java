package com.dg.raft.core.heartbeat;

import com.dg.raft.core.models.events.HeartBeatEvent;
import com.dg.raft.core.queue.RaftServerQueue;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Heartbeat implements Runnable {

    private static final Gson GSON = new Gson();

    private final RaftServerQueue<String> inboxQueue;
    private final String sourceServer;
    public Heartbeat(final RaftServerQueue<String> inboxQueue, String sourceServer) {
        this.inboxQueue = inboxQueue;
        this.sourceServer = sourceServer;
    }

    @Override
    public void run() {
        log.info("Starting the heartbeat thread on the server..");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // Creating a new heartbeat event
                HeartBeatEvent heartBeatEvent = new HeartBeatEvent(sourceServer, sourceServer);

                inboxQueue.put(GSON.toJson(heartBeatEvent));
                log.info("Heartbeat event published: {}", heartBeatEvent);

                // Wait for 5 seconds before sending the next heartbeat
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.error("Heartbeat thread interrupted", e);
                // Restore the interrupted status
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("Error publishing heartbeat event", e);
            }
        }
    }
}

