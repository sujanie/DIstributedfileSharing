package com.distributed.handlers;

import com.distributed.core.MessageInitiator;

import java.util.logging.Logger;

public class ResponseHandlerFactory {

    private static final Logger LOG = Logger.getLogger(ResponseHandlerFactory.class.getName());

    public static AbstractResponseHandler getResponseHandler(String keyword,
                                                             MessageInitiator MessageInitiator){
        switch (keyword){
            case "PING":
                AbstractResponseHandler pingHandler = PingHandler.getInstance();
                pingHandler.init(
                        MessageInitiator.getRoutingTable(),
                        MessageInitiator.getChannelOut(),
                        MessageInitiator.getTimeoutManager()
                );
                return pingHandler;

            case "BPING":
                AbstractResponseHandler bPingHandler = PingHandler.getInstance();
                bPingHandler.init(
                        MessageInitiator.getRoutingTable(),
                        MessageInitiator.getChannelOut(),
                        MessageInitiator.getTimeoutManager()
                );
                return bPingHandler;

            case "PONG":
                AbstractResponseHandler pongHandler = PongHandler.getInstance();
                pongHandler.init(
                        MessageInitiator.getRoutingTable(),
                        MessageInitiator.getChannelOut(),
                        MessageInitiator.getTimeoutManager()
                );
                return pongHandler;

            case "BPONG":
                AbstractResponseHandler bpongHandler = PongHandler.getInstance();
                bpongHandler.init(
                        MessageInitiator.getRoutingTable(),
                        MessageInitiator.getChannelOut(),
                        MessageInitiator.getTimeoutManager()
                );
                return bpongHandler;

            case "SER":
                AbstractResponseHandler searchQueryHandler = SearchQueryHandler.getInstance();
                searchQueryHandler.init(MessageInitiator.getRoutingTable(),
                        MessageInitiator.getChannelOut(),
                        MessageInitiator.getTimeoutManager());
                return searchQueryHandler;

            case "SEROK":
                AbstractResponseHandler queryHitHandler = QueryHitHandler.getInstance();
                queryHitHandler.init(MessageInitiator.getRoutingTable(),
                        MessageInitiator.getChannelOut(),
                        MessageInitiator.getTimeoutManager());
                return queryHitHandler;

            case "LEAVE":
                AbstractResponseHandler leaveHandler = PingHandler.getInstance();
                leaveHandler.init(
                        MessageInitiator.getRoutingTable(),
                        MessageInitiator.getChannelOut(),
                        MessageInitiator.getTimeoutManager()
                );
                return leaveHandler;
            default:
                LOG.severe("Unknown keyword received in Response Handler : " + keyword);
                return null;
        }
    }
}
