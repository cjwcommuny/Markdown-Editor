package server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class SessionCollection {
    private Map<Integer, Session> map = Collections.synchronizedMap(new HashMap<>());

    Session get(int id) {
        return map.get(id);
    }

    synchronized Session newSession(int id, String text, WriteSignal writeSignal) {
        Session session = map.put(id, new Session(text));
        session.addWriteSignal(writeSignal);
        return session;
    }
}