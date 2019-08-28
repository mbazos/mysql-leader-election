package com.bazos;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleEventBroadcaster<T> implements EventBroadcaster<T> {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleEventBroadcaster.class);

    private final List<EventListener<T>> listeners = new ArrayList<>();

    @Override
    public void addListener(EventListener<T> listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(EventListener<T> listener) {
        synchronized (listeners) {
            listeners.removeIf(listener::equals);
        }
    }

    @Override
    public void broadcast(T payload) {
        List<EventListener<T>> copyOfListeners;
        synchronized (listeners) {
            copyOfListeners = new ArrayList<>(listeners);
        }

        for (EventListener<T> listener : copyOfListeners) {
            try {
                LOG.info("broadcast() Invoking listener: {} with payload: {}...", listener, payload);
                listener.onEvent(payload);
                LOG.info("broadcast() Invoked listener: {}", listener);
            } catch (Exception ex) {
                LOG.error("broadcast() Error invoking listener: {}", listener, ex);
            }
        }
    }
}