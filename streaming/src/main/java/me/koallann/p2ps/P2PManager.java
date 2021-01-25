package me.koallann.p2ps;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import me.koallann.p2ps.command.Command;
import me.koallann.p2ps.command.CommandParser;
import me.koallann.p2ps.command.ConnectMeCommand;
import me.koallann.p2ps.command.Request;
import me.koallann.p2ps.command.Response;
import me.koallann.p2ps.command.StreamingCommand;
import me.koallann.p2ps.peer.Peer;
import me.koallann.p2ps.peer.PeerStreaming;
import me.koallann.p2ps.server.PeerServer;
import me.koallann.p2ps.util.ByteUtils;

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

    public void requestPeerToConnectMe(String host) throws IOException {
        if (streams.containsKey(host)) {
            return;
        }

        final PeerStreaming streaming = new PeerStreaming(this::handleStreamingIncoming);
        if (connectMeRequests.containsKey(host)) {
            streaming.setPeer(connectMeRequests.get(host));
        }
        streams.put(host, streaming);
        streaming.start();

        makeConnectMeRequest(host, streaming.getViewerPort());
    }

    public void makeStreaming(byte[] data) {
        streams.values().forEach(streaming -> streaming.send(data));
    }

    private void makeConnectMeRequest(String host, int viewerPort) throws IOException {
        final Socket socket = new Socket(InetAddress.getByName(host), PEER_SERVER_PORT);

        final Request request = ConnectMeCommand.buildRequest(viewerPort);
        socket.getOutputStream().write(request.data);

        byte[] responseBytes = ByteUtils.read(socket.getInputStream(), 1024);
        final Response response = new Response(responseBytes);

        socket.close();
    }

    private synchronized byte[] handleServerIncoming(Request request) {
        final Command cmd = CommandParser.readCommand(request);

        if (cmd instanceof ConnectMeCommand) {
            return onConnectMeCommand((ConnectMeCommand) cmd);
        }
        return Response.respondError("Invalid command");
    }

    private synchronized void handleStreamingIncoming(Request request) {
        final Command cmd = CommandParser.readCommand(request);

        if (cmd instanceof StreamingCommand) {
            onStreamingCommand((StreamingCommand) cmd);
        }
    }

    private byte[] onConnectMeCommand(ConnectMeCommand cmd) {
        final Peer peer = new Peer(cmd.host, cmd.port);
        connectMeRequests.put(peer.host, peer);

        if (streams.containsKey(peer.host)) {
            streams.get(peer.host).setPeer(peer);
        }

        return Response.respondOK();
    }

    private void onStreamingCommand(StreamingCommand cmd) {
        // TODO: notify listener
    }

}
