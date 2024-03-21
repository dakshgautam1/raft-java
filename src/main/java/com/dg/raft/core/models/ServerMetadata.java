package com.dg.raft.core.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@EqualsAndHashCode(of = "serverName")
@AllArgsConstructor
@Log4j2
public class ServerMetadata {

    private String serverName;

    private int serverPort;

    @Override
    public String toString() {
        return "Server Name: " + serverName + ", Server Port: " + serverPort;
    }
}
