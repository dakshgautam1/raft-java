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
        logDataList.add(new RaftLogData(currentTerm, ""));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("RaftLog{logDataList=[");
        for (RaftLogData logData : logDataList) {
            builder.append(logData.toString()).append(", ");
        }
        // Remove the last comma and space if the list is not empty
        if (!logDataList.isEmpty()) {
            builder.setLength(builder.length() - 2);
        }
        builder.append("]}");
        return builder.toString();
    }


    public void appendNewEntry(int term, String cmd) {
        logDataList.add(new RaftLogData(term, cmd));
    }

    public boolean appendEntries(
            final int previousLogIndex,
            final int previousLogTerm,
            final List<RaftLogData> newCommands
    ) {

        if (previousLogIndex >= logDataList.size()) {
            return false;
        }

        if (previousLogTerm != logDataList.get(previousLogIndex).getTerm()) {
            return false;
        }


        int logIndex = previousLogIndex + 1, newCommandIndex = 0;

        for (;newCommandIndex < newCommands.size(); logIndex++, newCommandIndex++) {
            if (logIndex < this.logDataList.size() &&
                    this.logDataList.get(logIndex).getTerm() != newCommands.get(newCommandIndex).getTerm()) {
                this.logDataList.subList(logIndex, logDataList.size()).clear();
                break;
            }
        }

        if (previousLogIndex + 1 < logDataList.size()) {
            for (int i = 0;i < newCommands.size(); i++) {
                if (previousLogIndex + 1 + i < logDataList.size()) {
                    logDataList.set(previousLogIndex + 1 + i, newCommands.get(i));
                } else {
                    logDataList.add(newCommands.get(i));
                }
            }
        } else {
            logDataList.addAll(newCommands);
        }

        return true;

    }

    public int getPrevLogIndex() {
        return logDataList.size() - 1;
    }

    public int getPreviousLogTerm() {
        return logDataList.get(logDataList.size() - 1).getTerm();
    }

    public void printLog() {
        log.info(logDataList.toString());
    }

}
