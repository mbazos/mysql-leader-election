package com.bazos;

public interface EventBroadcaster<T> {

    void addListener(EventListener<T> listener);

    void removeListener(EventListener<T> listener);

    void broadcast(T payload);
}