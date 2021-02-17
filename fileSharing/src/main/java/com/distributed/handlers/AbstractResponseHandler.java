package com.distributed.handlers;

import com.distributed.models.ChannelMessage;

public interface AbstractResponseHandler extends AbstractMessageHandler {

    void handleResponse(ChannelMessage message);
}
