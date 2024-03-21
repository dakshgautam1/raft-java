package com.dg.raft.core.models;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Getter
@RequiredArgsConstructor
@Log4j2
@EqualsAndHashCode
public class RaftLogData {

    private final int term;
    private final String command;

    @Override
    public String toString() {
        return String.format("[Term: %d, command: %s]", term, command);
    }
}
