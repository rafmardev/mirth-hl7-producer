package org.example.event;

@FunctionalInterface
public interface ResponseListener {
    void onResponseReceived (ResponseEvent event);
}
