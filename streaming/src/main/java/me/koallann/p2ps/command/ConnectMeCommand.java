package me.koallann.p2ps.command;

import java.util.Map;

public class ConnectMeCommand extends Command {

    public static final String PARAM_PORT = "Port";

    final int port;

    ConnectMeCommand(int port) {
        super(Type.CONNECT_ME);
        this.port = port;
    }

    public static ConnectMeCommand from(Map<String, String> requestParams) throws IllegalArgumentException {
        final String portStr = requestParams.get(PARAM_PORT);
        if (portStr == null) {
            throw new IllegalArgumentException(String.format("Parameter \"%s\" not set", PARAM_PORT));
        }

        final int port;
        try {
            port = Integer.parseInt(portStr);
            if (port < 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("Parameter \"%s\" must be an integer greater than 0", PARAM_PORT));
        }

        return new ConnectMeCommand(port);
    }

}
