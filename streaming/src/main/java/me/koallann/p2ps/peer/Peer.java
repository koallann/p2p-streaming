package me.koallann.p2ps.peer;

import java.net.InetAddress;

public class Peer {

    public static final int PORT_VIEWER_NOT_BOUND = 0;

    private final InetAddress address;

    private int viewerPort = PORT_VIEWER_NOT_BOUND;

    public Peer(InetAddress address) {
        this.address = address;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getViewerPort() {
        return viewerPort;
    }

    public void setViewerPort(int viewerPort) {
        this.viewerPort = viewerPort;
    }
}
