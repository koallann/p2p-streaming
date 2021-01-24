package me.koallann.p2ps.command;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public final class CommandParser {

    private static final String LINE_DELIMITER = "\n";
    private static final String KEY_VALUE_SEPARATOR = ":";
    private static final int BODY_MAX_SIZE = 1024;

    public Command readCommand(InputStream request) {
        final Scanner scanner = new Scanner(request);
        scanner.useDelimiter(LINE_DELIMITER);

        final String cmdTypeLine = scanner.nextLine();
        final String cmdTypeId = cmdTypeLine.substring(0, cmdTypeLine.indexOf(" "));

        final Map<String, String> requestParams = buildParams(scanner);
        final byte[] requestBody = buildBody(scanner);

        switch (Command.Type.valueOf(cmdTypeId)) {
            case CONNECT_ME:
                return ConnectMeCommand.from(requestParams);
            case STREAM:
                return StreamCommand.from(requestBody);
            default:
                return null;
        }
    }

    private Map<String, String> buildParams(Scanner scanner) throws IllegalArgumentException {
        final Map<String, String> params = new HashMap<>();

        while (scanner.hasNext()) {
            final String line = scanner.nextLine();
            if (line.isEmpty()) {
                break;
            }

            final String[] keyValue = line.split(KEY_VALUE_SEPARATOR);
            if (keyValue.length != 2) {
                throw new IllegalArgumentException("Invalid parameter format");
            }

            params.put(keyValue[0], keyValue[1]);
        }
        return params;
    }

    private byte[] buildBody(Scanner scanner) {
        byte[] data = new byte[BODY_MAX_SIZE];
        int size = 0;

        while (scanner.hasNextByte() && size < BODY_MAX_SIZE) {
            data[size++] = scanner.nextByte();
        }
        if (size < BODY_MAX_SIZE) {
            byte[] newData = new byte[size];
            System.arraycopy(data, 0, newData, 0, size);
            data = newData;
        }
        return data;
    }

}
