package com.dg.raft.core.queue;

import com.dg.raft.core.models.events.Event;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
@Log4j2
public class RaftServerQueue<T> {

    private final BlockingQueue<T> blockingQueue;

    public RaftServerQueue() {
        this.blockingQueue = new LinkedBlockingQueue<>();
    }

    public void put(T event) {
        try {
            blockingQueue.put(event);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public T get() {
        try {
            return blockingQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null; // Or handle interruption as needed
        }
    }

}
