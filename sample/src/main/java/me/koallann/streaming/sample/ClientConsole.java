package me.koallann.streaming.sample;

import me.koallann.p2ps.P2pManager;
import me.koallann.p2ps.command.StreamingCommand;
import me.koallann.p2ps.peer.Peer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientConsole extends Console implements P2pManager.OnReceiveStreamingListener {

    private static final int OPTION_MENU_EXIT = 0;
    private static final int OPTION_MENU_REQUEST_CONNECT_ME = 1;
    private static final int OPTION_MENU_MAKE_STREAMING = 2;
    private static final int OPTION_MENU_LIST_CONNECTIONS = 3;

    private final P2pManager p2pManager;

    public ClientConsole() {
        try {
            this.p2pManager = new P2pManager(9876, this);
        } catch (IOException e) {
            throw new IllegalStateException("Error initializing P2P manager", e);
        }
    }

    public void start() {
        this.p2pManager.start();
        menuLoop();
        this.p2pManager.stop();
    }

    private void menuLoop() {
        clear();
        println("###################\n" +
            "P2P STREAMING MENU\n" +
            "###################\n\n" +
            "1 - Send CONNECT_ME request\n" +
            "2 - Make STREAMING\n" +
            "3 - List established connections\n" +
            "0 - Exit\n"
        );
        print("Option: ");

        try {
            int option = Integer.parseInt(scanner.nextLine());
            println();

            if (!handleMenuChoose(option)) return;
            menuLoop();
        } catch (Exception e) {
            println("\nInvalid option!");
            holdOutput();
            menuLoop();
        }
    }

    private boolean handleMenuChoose(int option) {
        switch (option) {
            case OPTION_MENU_EXIT:
                println("Exiting...");
                return false;
            case OPTION_MENU_REQUEST_CONNECT_ME:
                onRequestConnectMe();
                return true;
            case OPTION_MENU_MAKE_STREAMING:
                onMakeStreaming();
                return true;
            case OPTION_MENU_LIST_CONNECTIONS:
                onListEstablishedConnections();
                return true;
            default:
                println("Invalid option!");
                holdOutput();
                return true;
        }
    }

    private void onRequestConnectMe() {
        try {
            print("Type host (x.x.x.x): ");
            final InetAddress address = InetAddress.getByName(scanner.nextLine());

            try {
                p2pManager.requestPeerToConnectMe(address.getHostAddress());
            } catch (IOException e) {
                throw new IllegalStateException("Error requesting peer to connect me", e);
            }
        } catch (UnknownHostException e) {
            println("Invalid host!");
            holdOutput();
            onRequestConnectMe();
        }
    }

    private void onMakeStreaming() {
        print("Type data: ");
        final String data = scanner.nextLine();

        p2pManager.makeStreaming(data.getBytes());
        holdOutput();
    }

    private void onListEstablishedConnections() {
        println("You are connected to these peers:");
        println("<peer_host>:<peer_port> (:<my_port_to_peer)>\n");

        p2pManager.getStreams().forEach(streaming -> {
            final Peer peer = streaming.getPeer();
            if (peer != null) {
                println(String.format("%s:%d (:%d)", peer.host, peer.viewerPort, streaming.getViewerPort()));
            }
        });

        holdOutput();
    }

    @Override
    public void onReceiveStreaming(StreamingCommand cmd) {
        println("onReceiveStreaming: " + new String(cmd.content));
    }

}
