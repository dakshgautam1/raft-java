package com.dg.raft.core;

import com.dg.raft.core.models.RaftLogData;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Getter
@Log4j2
public class RaftLog {

    private final List<RaftLogData> logDataList;

    public RaftLog(int currentTerm) {
        this.logDataList = new ArrayList<>();
        logDataList.add(new RaftLogData(-1, ""));
        log.info("Initialized RaftLog with starting term: {}", currentTerm);
    }

    @Override
    public String toString() {
        // Method unchanged, logging is not typically added to toString
        StringBuilder builder = new StringBuilder("RaftLog{logDataList=[");
        for (RaftLogData logData : logDataList) {
            builder.append(logData.toString()).append(", ");
        }
        if (!logDataList.isEmpty()) {
            builder.setLength(builder.length() - 2);
        }
        builder.append("]}");
        return builder.toString();
    }

    public void appendNewEntry(int term, String cmd) {
        logDataList.add(new RaftLogData(term, cmd));
        log.info("Appended new entry with term: {}, command: '{}'", term, cmd);
    }

    public boolean appendEntries(
            final int previousLogIndex,
            final int previousLogTerm,
            final List<RaftLogData> newCommands
    ) {
        log.info("Attempting to append entries starting from index {} with term {}", previousLogIndex, previousLogTerm);

        if (previousLogIndex >= logDataList.size()) {
            log.warn("Previous log index {} is out of bounds (log size: {}). Append rejected.", previousLogIndex, logDataList.size());
            return false;
        }

        if (previousLogTerm != logDataList.get(previousLogIndex).getTerm()) {
            log.warn("Previous log term {} does not match the term at index {}. Append rejected.", previousLogTerm, previousLogIndex);
            return false;
        }

        int logIndex = previousLogIndex + 1, newCommandIndex = 0;

        for (; newCommandIndex < newCommands.size(); logIndex++, newCommandIndex++) {
            if (logIndex < this.logDataList.size() &&
                    this.logDataList.get(logIndex).getTerm() != newCommands.get(newCommandIndex).getTerm()) {
                this.logDataList.subList(logIndex, logDataList.size()).clear();
                log.info("Log conflict at index {}, clearing subsequent entries.", logIndex);
                break;
            }
        }

        if (previousLogIndex + 1 < logDataList.size()) {
            log.info("Replacing existing entries starting from index {}", previousLogIndex + 1);
            for (int i = 0; i < newCommands.size(); i++) {
                if (previousLogIndex + 1 + i < logDataList.size()) {
                    logDataList.set(previousLogIndex + 1 + i, newCommands.get(i));
                } else {
                    logDataList.add(newCommands.get(i));
                }
            }
        } else {
            logDataList.addAll(newCommands);
            log.info("Appended {} new entries to the log", newCommands.size());
        }

        return true;
    }

    public int getPrevLogIndex() {
        // This method is straightforward and usually does not need logging,
        // but you can add it if you need to trace how often it's called or the results.
        return logDataList.size() - 1;
    }

    public int getPreviousLogTerm() {
        // Similarly, logging here might be useful for tracing or debugging specific issues.
        return logDataList.get(logDataList.size() - 1).getTerm();
    }

    public void printLog() {
        log.info("Current Raft log: {}", this.toString());
    }
}
