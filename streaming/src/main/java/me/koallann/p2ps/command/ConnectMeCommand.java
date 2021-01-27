package me.koallann.p2ps.command;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import me.koallann.p2ps.server.Request;
import me.koallann.p2ps.util.StringUtils;

public final class ConnectMeCommand extends Command {

    private static final String PARAM_PORT = "Port";

    public final int port;

    public ConnectMeCommand(InetAddress src, int port) {
        super(Type.CONNECT_ME, src);
        this.port = port;
    }

    public Request buildRequest() {
        final Map<String, String> params = new HashMap<>();
        params.put(PARAM_PORT, Integer.toString(port));

        return new Request(src, type, params, null);
    }

    public static ConnectMeCommand from(Request request) throws IllegalArgumentException {
        if (request.type != Type.CONNECT_ME) {
            throw new IllegalArgumentException("Invalid request type");
        }

        final String portParam = request.params.get(PARAM_PORT);
        if (portParam == null) {
            throw new IllegalArgumentException(StringUtils.format("Parameter \"%s\" not set", PARAM_PORT));
        }

        final int port = Integer.parseInt(portParam);
        if (port < 1) {
            throw new IllegalArgumentException(StringUtils.format("Parameter \"%s\" must be an integer greater than 0", PARAM_PORT));
        }

        return new ConnectMeCommand(request.src, port);
    }

}
