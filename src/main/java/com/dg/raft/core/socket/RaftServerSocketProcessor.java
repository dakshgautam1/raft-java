package com.dg.raft.core.socket;

import com.dg.raft.core.queue.RaftServerQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

@RequiredArgsConstructor
@Log4j2
public class RaftServerSocketProcessor implements Runnable {

    private final int port;

    private final RaftServerQueue<String> inboxQueue;

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            log.info("UDP Server started and listening on port " + port);

            // Create a buffer to receive incoming data
            byte[] buffer = new byte[1024];

            while (true) {
                // Create a DatagramPacket to hold the incoming data
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                // Receive the incoming packet
                socket.receive(packet);


                // Convert the packet data to a string and print it
                String receivedData = new String(packet.getData(), 0, packet.getLength());

                log.info("Received from client: " + receivedData);
                inboxQueue.put(receivedData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
