package me.koallann.p2ps.peer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import me.koallann.p2ps.command.Command;
import me.koallann.p2ps.command.Request;
import me.koallann.p2ps.command.StreamingCommand;
import me.koallann.p2ps.util.ByteUtils;

final class PeerViewerThread extends Thread {

    private final DatagramSocket streamingSocket;
    private final int dataMaxSize;
    private final PeerStreaming.OnReceiveDataListener onReceiveDataListener;

    public PeerViewerThread(
        DatagramSocket streamingSocket,
        int dataMaxSize,
        PeerStreaming.OnReceiveDataListener onReceiveDataListener
    ) {
        this.streamingSocket = streamingSocket;
        this.dataMaxSize = dataMaxSize;
        this.onReceiveDataListener = onReceiveDataListener;
    }

    @Override
    public void run() {
        super.run();
        while (!isInterrupted()) {
            try {
                byte[] receiveBytes = new byte[dataMaxSize];
                final DatagramPacket receivePacket = new DatagramPacket(receiveBytes, dataMaxSize);

                streamingSocket.receive(receivePacket);
                receiveBytes = resizeOnContentLength(receiveBytes);
                if (receiveBytes == null) return;

                final Request request = new Request(
                    receivePacket.getAddress(),
                    receiveBytes
                );
                onReceiveDataListener.onReceive(request);
            } catch (IOException e) {
                if (!isInterrupted()) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        streamingSocket.close();
    }

    private byte[] resizeOnContentLength(byte[] data) {
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)));
            int length = 0;

            String line = reader.readLine();
            if (line == null || !line.equals(Command.Type.STREAMING.name())) {
                reader.close();
                return null;
            }
            length += line.length() + 1; // plus 1 because the LF

            line = reader.readLine();
            if (line == null) {
                reader.close();
                return null;
            }
            length += line.length() + 1;

            final String[] keyValue = line.split(":");
            if (keyValue.length != 2 || !keyValue[0].trim().equals(StreamingCommand.PARAM_CONTENT_LENGTH)) {
                reader.close();
                return null;
            }

            final int contentLength = Integer.parseInt(keyValue[1].trim());
            if (contentLength < 1) {
                reader.close();
                return null;
            }
            length += contentLength + 1;
            reader.close();

            if (length < dataMaxSize) {
                return ByteUtils.resizeArray(data, length);
            }
            return data;
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

}
