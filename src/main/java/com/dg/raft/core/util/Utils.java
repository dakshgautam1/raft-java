package com.dg.raft.core.util;

import com.dg.raft.core.models.events.AppendEntryEvent;
import com.dg.raft.core.models.events.AppendEntryEventResponse;
import com.dg.raft.core.models.events.Event;
import com.dg.raft.core.models.events.EventType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

public class Utils {


    public static Gson getGsonWithRuntimeTypeAdapterFactory() {
        RuntimeTypeAdapterFactory<Event> runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
                .of(Event.class, "eventType") // The "type" field in your JSON determines the subclass
                .registerSubtype(AppendEntryEvent.class, EventType.APPEND_ENTRY.toString().toLowerCase())
                .registerSubtype(AppendEntryEventResponse.class, EventType.APPEND_ENTRY_RESPONSE.toString().toLowerCase());

        return new GsonBuilder()
                .registerTypeAdapterFactory(runtimeTypeAdapterFactory)
                .create();
    }
}
