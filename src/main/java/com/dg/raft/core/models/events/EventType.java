package com.dg.raft.core.models.events;

public enum EventType {

    SHOW_LOG,
    SHOW_STATE,
    ADD_NEW_COMMAND,
    SEND_HEART_BEAT,

    BECOME_LEADER,
    BECOME_FOLLOWER,

    APPEND_ENTRY,
    APPEND_ENTRY_RESPONSE,

    APPEND_CLIENT_ENTRY,
    TICK
}
