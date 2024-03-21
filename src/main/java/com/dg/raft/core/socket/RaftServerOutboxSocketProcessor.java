package com.dg.raft.core.socket;

import com.dg.raft.core.ServerConfig;
import com.dg.raft.core.models.events.Event;
import com.dg.raft.core.models.events.EventType;
import com.dg.raft.core.queue.RaftServerQueue;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

import static com.dg.raft.core.models.events.EventType.*;

@RequiredArgsConstructor
@Log4j2
public class RaftServerOutboxSocketProcessor implements Runnable {

    private static final List<EventType> ALLOWED_EVENT_TYPES = Arrays.asList(
            APPEND_ENTRY,
            APPEND_CLIENT_ENTRY,
            TICK
    );


    private final int port;
    private final RaftServerQueue outboxQueue;

    @Override
    public void run() {
        Gson gson = new Gson();
        try (DatagramSocket sendSocket = new DatagramSocket()) {
            log.info("UDP sender bound to port: " + sendSocket.getLocalPort());

            while (true) {
                Event event = outboxQueue.get(); // Get the event from the queue
                String destServerName = findDestinationServer(event); // Find the destination port
                int destinationPort = ServerConfig.SERVER_PORT_MAPPING.get(destServerName);
                String message = gson.toJson(event);

                // Publish the message using the socket
                publishMessage(sendSocket, message, destinationPort);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to find the destination port from the event
    private String findDestinationServer(Event event) {
        if (ALLOWED_EVENT_TYPES.contains(event.getEventType())) {
            return event.getDestServer();
        }
        throw new RuntimeException("Should not be here.");
    }


    // Method to publish the message using the socket
    private void publishMessage(DatagramSocket socket, String message, int destinationPort) throws IOException {
        InetAddress destinationAddress = InetAddress.getLocalHost();
        byte[] sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, destinationAddress, destinationPort);

        // Send the message
        socket.send(sendPacket);
        log.info("Sent message: " + message);
    }


}
