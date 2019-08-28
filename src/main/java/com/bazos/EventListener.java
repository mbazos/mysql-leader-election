package com.bazos;

public interface EventListener<T> {

    void onEvent(T payload);

}

