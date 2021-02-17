package com.distributed.handlers;

import com.distributed.models.ChannelMessage;
import com.distributed.models.RoutingTable;
import com.distributed.services.TimeoutManager;

import java.util.concurrent.BlockingQueue;

interface AbstractMessageHandler {
    void init (
            RoutingTable routingTable,
            BlockingQueue<ChannelMessage> channelOut,
            TimeoutManager timeoutManager);

}
