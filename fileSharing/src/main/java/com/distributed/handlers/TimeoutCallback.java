package com.distributed.handlers;

public interface TimeoutCallback {
    void onTimeout(String messageId);
    void onResponse(String messageId);
}
