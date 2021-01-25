package me.koallann.p2ps;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import me.koallann.p2ps.command.Command;
import me.koallann.p2ps.command.CommandParser;
import me.koallann.p2ps.command.ConnectMeCommand;
import me.koallann.p2ps.command.StreamCommand;
import me.koallann.p2ps.peer.Peer;
import me.koallann.p2ps.peer.PeerStreaming;
import me.koallann.p2ps.server.PeerServer;

public final class P2PManager {

    private static final int PEER_SERVER_PORT = 8000;

    private final PeerServer server;
    private final Map<String, PeerStreaming> streams;
    private final Map<String, Peer> connectMeRequests;

    public P2PManager() throws IOException {
        this.server = new PeerServer(PEER_SERVER_PORT, this::handleServerIncoming);
        this.streams = new HashMap<>();
        this.connectMeRequests = new HashMap<>();
    }

    public void start() {
        server.listen();
    }

    public void stop() {
        server.stop();
        streams.values().forEach(PeerStreaming::stop);
        streams.clear();
        connectMeRequests.clear();
    }

    public void makeConnectMeRequest(String host) throws IOException {
        if (streams.containsKey(host)) {
            return;
        }

        final PeerStreaming streaming = new PeerStreaming(this::handleStreamingIncoming);
        if (connectMeRequests.containsKey(host)) {
            streaming.setPeer(connectMeRequests.get(host));
        }
        streams.put(host, streaming);
        streaming.start();

        final String request = ConnectMeCommand.buildRequest(streaming.getViewerPort());
        final Socket socket = new Socket(InetAddress.getByName(host), PEER_SERVER_PORT);
        final DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
        final BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        outToServer.writeBytes(request);
        inFromServer.readLine(); // TODO: handle response
        socket.close();
    }

    public void makeStreamRequest(byte[] data) {
        streams.values().forEach(streaming -> streaming.send(data));
    }

    private synchronized String handleServerIncoming(InetAddress address, InputStream request) {
        final Command cmd = CommandParser.readCommand(address, request);

        if (cmd instanceof ConnectMeCommand) {
            return onConnectMeCommand((ConnectMeCommand) cmd);
        }
        return Command.RESPONSE_ERROR;
    }

    private synchronized void handleStreamingIncoming(InetAddress address, InputStream request) {
        final Command cmd = CommandParser.readCommand(address, request);

        if (cmd instanceof StreamCommand) {
            onStreamCommand((StreamCommand) cmd);
        }
    }

    private String onConnectMeCommand(ConnectMeCommand cmd) {
        final Peer peer = new Peer(cmd.host, cmd.port);
        connectMeRequests.put(peer.host, peer);

        if (streams.containsKey(peer.host)) {
            streams.get(peer.host).setPeer(peer);
        }

        return Command.RESPONSE_OK;
    }

    private void onStreamCommand(StreamCommand cmd) {
        // TODO: notify listener
    }

}
