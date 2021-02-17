package com.distributed.models;

public class PongMessage implements P2PMessage {
    private final String destAddress;
    private final int detPort;

    public PongMessage(String destAddress, int detPort) {
        this.destAddress = destAddress;
        this.detPort = detPort;
    }

    public String getDestAddress() {
        return destAddress;
    }

    public int getDetPort() {
        return detPort;
    }	
}
