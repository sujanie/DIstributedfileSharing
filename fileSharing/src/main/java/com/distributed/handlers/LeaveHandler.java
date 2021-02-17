package com.distributed.handlers;

import com.distributed.commons.Constants;
import com.distributed.models.ChannelMessage;
import com.distributed.core.Neighbour;
import com.distributed.models.RoutingTable;
import com.distributed.services.TimeoutManager;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class LeaveHandler implements AbstractRequestHandler {

    private RoutingTable routingTable;
    private BlockingQueue<ChannelMessage> channelOut;
    private static LeaveHandler leaveHandler;

    public synchronized static LeaveHandler getInstance() {
        if (leaveHandler == null){
            leaveHandler = new LeaveHandler();
        }
        return leaveHandler;
    }

    public void sendLeave () {
        String payload = String.format(Constants.LEAVE_FORMAT,
                this.routingTable.getAddress(),
                this.routingTable.getPort());
        String rawMessage = String.format(Constants.MSG_FORMAT, payload.length() + 5,payload);
        ArrayList<Neighbour> neighbours = routingTable.getNeighbours();
        for (Neighbour n: neighbours) {
            ChannelMessage message = new ChannelMessage(n.getAddress(), n.getPort(),rawMessage);
            sendRequest(message);
        }

    }

    @Override
    public void init(RoutingTable routingTable,
                     BlockingQueue<ChannelMessage> channelOut,
                     TimeoutManager timeoutManager) {
        this.routingTable = routingTable;
        this.channelOut = channelOut;
    }

    @Override
    public void sendRequest(ChannelMessage message) {
        try {
            channelOut.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
