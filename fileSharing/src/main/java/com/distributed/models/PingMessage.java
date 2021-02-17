package com.distributed.models;

public class PingMessage implements P2PMessage {
	private final String sourceAddress;
    private final int sourcePort;


    public PingMessage(String sourceAddress, int sourcePort) {
        this.sourceAddress = sourceAddress;
        this.sourcePort = sourcePort;

    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public int getSourcePort() {
        return sourcePort;
    }

}
