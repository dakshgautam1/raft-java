package com.dg.raft.core;

import com.dg.raft.core.models.ServerMetadata;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
public class ServerConfig {

    public static final String SERVER_1_KEY = "1";
    public static final String SERVER_2_KEY = "2";
    public static final String SERVER_3_KEY = "3";
    public static final String SERVER_4_KEY = "4";
    public static final String SERVER_5_KEY = "5";

    public static final int SERVER_1_PORT = 15000;
    public static final int SERVER_2_PORT = 16000;
    public static final int SERVER_3_PORT = 17000;
    public static final int SERVER_4_PORT = 18000;
    public static final int SERVER_5_PORT = 19000;

    public static final Map<String, Integer> SERVER_PORT_MAPPING;

    static {
        SERVER_PORT_MAPPING = new HashMap<>();
        SERVER_PORT_MAPPING.put(SERVER_1_KEY, SERVER_1_PORT);
        SERVER_PORT_MAPPING.put(SERVER_2_KEY, SERVER_2_PORT);
        SERVER_PORT_MAPPING.put(SERVER_3_KEY, SERVER_3_PORT);
        SERVER_PORT_MAPPING.put(SERVER_4_KEY, SERVER_4_PORT);
        SERVER_PORT_MAPPING.put(SERVER_5_KEY, SERVER_5_PORT);
    }

    private ServerConfig() {
        // Private constructor to prevent instantiation
    }

    public static String getServerName(int port) {
        for (Map.Entry<String, Integer> entry : SERVER_PORT_MAPPING.entrySet()) {
            if (entry.getValue() == port) {
                return entry.getKey();
            }
        }
        return null; // If the port is not found in the mapping
    }


    public static List<ServerMetadata> getAllServers(final String serverName) {
        return SERVER_PORT_MAPPING.entrySet()
                .stream()
                .filter(s -> !s.getKey().equals(serverName))
                .map(e -> new ServerMetadata(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public static ServerMetadata createServerMetadata(final String serverName) {
        return new ServerMetadata(serverName, SERVER_PORT_MAPPING.get(serverName));
    }
}
