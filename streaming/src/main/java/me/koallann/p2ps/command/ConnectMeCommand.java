package me.koallann.p2ps.command;

import java.net.InetAddress;
import java.util.Locale;
import java.util.Map;

public class ConnectMeCommand extends Command {

    public static final String PARAM_PORT = "Port";

    public final String host;
    public final int port;

    ConnectMeCommand(String host, int port) {
        super(Type.CONNECT_ME);
        this.host = host;
        this.port = port;
    }

    public static ConnectMeCommand from(InetAddress address, Map<String, String> requestParams) throws IllegalArgumentException {
        final String portStr = CommandParser.readParamOrElseThrow(
            requestParams,
            PARAM_PORT,
            new IllegalArgumentException(String.format("Parameter \"%s\" not set", PARAM_PORT))
        );

        final int port;
        try {
            port = Integer.parseInt(portStr);
            if (port < 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("Parameter \"%s\" must be an integer greater than 0", PARAM_PORT));
        }

        return new ConnectMeCommand(address.getHostAddress(), port);
    }

    public static String buildRequest(int port) {
        return String.format(Locale.ENGLISH, "%s\nPort: %d\n", Type.CONNECT_ME.name(), port);
    }

}
