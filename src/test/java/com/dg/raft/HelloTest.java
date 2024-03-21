package com.dg.raft;

import com.dg.raft.core.RaftLog;
import com.dg.raft.core.models.RaftLogData;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HelloTest {

    @Test
    public void hello() {
        RaftLog raftLog = new RaftLog(1);
        raftLog.appendNewEntry(1, "x");
        raftLog.appendNewEntry(1, "y");
//        raftLog.appendNewEntry(2, "x1");
//        raftLog.appendNewEntry(2, "x2");
//        raftLog.appendNewEntry(3, "x3");
//        raftLog.appendNewEntry(3, "x4");
//        raftLog.appendNewEntry(4, "x5");


        System.out.println(raftLog.getLogDataList());

        final List<RaftLogData> newCommands = Arrays.asList(
                new RaftLogData(1, "z"),
                new RaftLogData(2, "x1")
        );
        assertTrue(raftLog.appendEntries(2, 1, newCommands));

        System.out.println(raftLog.getLogDataList());

        final List<RaftLogData> newCommands1 = Arrays.asList(
                new RaftLogData(1, "z"),
                new RaftLogData(2, "x1"),
                new RaftLogData(2, "x6")

        );
        assertTrue(raftLog.appendEntries(2, 1, newCommands1));
        System.out.println(raftLog.getLogDataList());

        final List<RaftLogData> newCommands4 = Arrays.asList(
                new RaftLogData(1, "z"),
                new RaftLogData(3, "zzz_newCommands4")
        );
        assertTrue(raftLog.appendEntries(2, 1, newCommands4));
        System.out.println(raftLog.getLogDataList());

    }

}
