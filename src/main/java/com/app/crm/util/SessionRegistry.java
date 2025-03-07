package com.app.crm.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionRegistry {
    private static final Map<String, String> ACTIVE_SESSIONS = new ConcurrentHashMap<>();

    public static void addSession(String username, String sessionId) {
        ACTIVE_SESSIONS.put(username, sessionId);
    }

    public static void removeSession(String username) {
        ACTIVE_SESSIONS.remove(username);
    }

    public static String getSessionId(String username) {
        return ACTIVE_SESSIONS.get(username);
    }
}