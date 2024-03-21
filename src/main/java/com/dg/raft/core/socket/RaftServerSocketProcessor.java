package com.dg.raft.core.socket;

import com.dg.raft.core.models.events.Event;
import com.dg.raft.core.queue.RaftServerQueue;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

@RequiredArgsConstructor
@Log4j2
public class RaftServerSocketProcessor implements Runnable {

    private final int port;

    private final RaftServerQueue inboxQueue;

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            log.info("UDP Server started and listening on port " + port);

            // Create a buffer to receive incoming data
            byte[] buffer = new byte[1024];

            Gson gson = new Gson();
            while (true) {
                // Create a DatagramPacket to hold the incoming data
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                // Receive the incoming packet
                socket.receive(packet);


                // Convert the packet data to a string and print it
                String receivedData = new String(packet.getData(), 0, packet.getLength());

                final Event event = gson.fromJson(receivedData, Event.class);
                inboxQueue.put(event);
                log.info("Received from client: " + receivedData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
