package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.metadata;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// https://www.adam-bien.com/roller/abien/entry/how_to_pass_context_with
public class MetadataEnhancerContextHolder {

    private static final ThreadLocal<Map<String,Object>> THREAD_WITH_CONTEXT = new ThreadLocal<>();

    private MetadataEnhancerContextHolder() {}

    public static void put(final String key, final Object payload) {
        if(THREAD_WITH_CONTEXT.get() == null) {
            THREAD_WITH_CONTEXT.set(new HashMap<>());
        }
        THREAD_WITH_CONTEXT.get().put(key, payload);
    }

    public static Object get(final String key) {
        return Optional.ofNullable(THREAD_WITH_CONTEXT.get())
                .map(map -> map.get(key))
                .orElse(null);
    }

    public static void cleanupThread(){
        THREAD_WITH_CONTEXT.remove();
    }

}
