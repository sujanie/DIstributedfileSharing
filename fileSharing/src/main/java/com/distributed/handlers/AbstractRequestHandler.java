package com.distributed.handlers;

import com.distributed.models.ChannelMessage;

public interface AbstractRequestHandler extends AbstractMessageHandler {

    void sendRequest(ChannelMessage message);
}
