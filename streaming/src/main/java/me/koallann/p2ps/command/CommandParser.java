package me.koallann.p2ps.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CommandParser {

    private static final String KEY_VALUE_SEPARATOR = ":";

    public Command readCommand(BufferedReader request) {
        try {
            final String cmdTypeLine = request.readLine();
            final String cmdTypeId = cmdTypeLine.substring(0, cmdTypeLine.indexOf(" "));

            final Map<String, String> requestParams = buildParams(request);

            switch (Command.Type.valueOf(cmdTypeId)) {
                case CONNECT_ME:
                    return ConnectMeCommand.create(requestParams);
                default:
                    return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Map<String, String> buildParams(BufferedReader request) throws IOException {
        final Map<String, String> params = new HashMap<>();

        String line;
        while (!(line = request.readLine()).trim().isEmpty()) {
            final String[] keyValue = line.split(KEY_VALUE_SEPARATOR);
            if (keyValue.length != 2) {
                continue;
            }
            params.put(keyValue[0], keyValue[1]);
        }
        return params;
    }

}
