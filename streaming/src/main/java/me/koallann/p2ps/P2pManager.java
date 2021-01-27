package me.koallann.p2ps;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public final class P2pManager {

    private static final int SERVER_PACKET_MAX_SIZE = 64;
    private static final int STREAMING_PACKET_MAX_SIZE = 1024;

    private final int serverPort;
    private final PeerServer server;
    private final Map<String, PeerStreaming> streams;
    private final Map<String, Peer> connectMeRequests;

    private final OnReceiveStreamingListener onReceiveStreamingListener;

    public P2pManager(
        int serverPort,
        OnReceiveStreamingListener onReceiveStreamingListener
    ) throws IOException {
        this.serverPort = serverPort;
        this.server = new PeerServer(serverPort, SERVER_PACKET_MAX_SIZE, this::handleServerIncoming);
        this.streams = new HashMap<>();
        this.connectMeRequests = new HashMap<>();
        this.onReceiveStreamingListener = onReceiveStreamingListener;
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

    public List<PeerStreaming> getStreams() {
        return new ArrayList<>(streams.values());
    }

    public List<Peer> getConnectMeRequests() {
        return new ArrayList<>(connectMeRequests.values());
    }

    public void requestPeerToConnectMe(String host) throws IOException {
        if (streams.containsKey(host)) {
            return;
        }

        final PeerStreaming streaming = new PeerStreaming(STREAMING_PACKET_MAX_SIZE, this::handleStreamingIncoming);
        streams.put(host, streaming);

        if (connectMeRequests.containsKey(host)) {
            streaming.setPeer(connectMeRequests.get(host));
        }

        streaming.start();

        new Thread(() -> {
            try {
                makeConnectMeRequest(host, streaming.getViewerPort());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void makeStreaming(byte[] data) {
        try {
            final Request request = StreamingCommand.buildRequest(data);
            streams.values().forEach(streaming -> streaming.send(request.data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeConnectMeRequest(String host, int viewerPort) throws IOException {
        final Socket socket = new Socket(InetAddress.getByName(host), serverPort);

        final Request request = ConnectMeCommand.buildRequest(viewerPort);
        socket.getOutputStream().write(request.data);

        byte[] responseBytes = ByteUtils.read(socket.getInputStream(), SERVER_PACKET_MAX_SIZE);
        final Response response = new Response(responseBytes);

        socket.close();
    }

    private synchronized byte[] handleServerIncoming(Request request) {
        try {
            final Command cmd = CommandParser.readCommand(request);

            if (cmd instanceof ConnectMeCommand) {
                return onConnectMeCommand((ConnectMeCommand) cmd);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Response.respondError("Invalid command");
    }

    private synchronized void handleStreamingIncoming(Request request) {
        try {
            final Command cmd = CommandParser.readCommand(request);

            if (cmd instanceof StreamingCommand) {
                onStreamingCommand((StreamingCommand) cmd);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] onConnectMeCommand(ConnectMeCommand cmd) {
        final Peer peer = new Peer(cmd.from.getHostAddress(), cmd.port);
        connectMeRequests.put(peer.host, peer);

        if (streams.containsKey(peer.host)) {
            streams.get(peer.host).setPeer(peer);
        }

        return Response.respondOK();
    }

    private void onStreamingCommand(StreamingCommand cmd) {
        onReceiveStreamingListener.onReceiveStreaming(cmd);
    }

    @FunctionalInterface
    public interface OnReceiveStreamingListener {
        void onReceiveStreaming(StreamingCommand cmd);
    }

}
