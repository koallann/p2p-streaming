package me.koallann.p2ps.command;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

import me.koallann.p2ps.util.MapUtils;
import me.koallann.p2ps.util.StringUtils;

public final class ConnectMeCommand extends Command {

    private static final String PARAM_PORT = "Port";

    public final String host;
    public final int port;

    ConnectMeCommand(String host, int port) {
        super(Type.CONNECT_ME);
        this.host = host;
        this.port = port;
    }

    public static ConnectMeCommand from(InetAddress address, Map<String, String> requestParams) throws IllegalArgumentException {
        final String portString = MapUtils.readOrElseThrow(
            requestParams,
            PARAM_PORT,
            new IllegalArgumentException(StringUtils.format("Parameter \"%s\" not set", PARAM_PORT))
        );

        final int port;
        try {
            port = Integer.parseInt(portString);
            if (port < 1) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(StringUtils.format("Parameter \"%s\" must be an integer greater than 0", PARAM_PORT));
        }

        return new ConnectMeCommand(address.getHostAddress(), port);
    }

    public static Request buildRequest(int port) throws IOException {
        return new Request(
            InetAddress.getLocalHost(),
            StringUtils.format("%s\nPort: %d", Type.CONNECT_ME.name(), port).getBytes()
        );
    }

}
