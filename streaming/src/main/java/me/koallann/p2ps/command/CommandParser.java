package me.koallann.p2ps.command;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import me.koallann.p2ps.util.ByteUtils;
import me.koallann.p2ps.util.StringUtils;

public final class CommandParser {

    private static final String LINE_DELIMITER = "\n";
    private static final String KEY_VALUE_SEPARATOR = ":";

    private CommandParser() {
        // This is a pure static class
    }

    public static Command readCommand(Request request, int bodyMaxSize) {
        final Scanner scanner = new Scanner(new ByteArrayInputStream(request.data));
        scanner.useDelimiter(LINE_DELIMITER);

        final String commandTypeLine = scanner.nextLine();
        final Map<String, String> requestParams = buildRequestParams(scanner);
        final byte[] requestBody = buildRequestBody(scanner, bodyMaxSize);

        switch (Command.Type.valueOf(commandTypeLine)) {
            case CONNECT_ME:
                return ConnectMeCommand.from(request.from, requestParams);
            case STREAMING:
                return StreamingCommand.from(requestBody);
            default:
                return null;
        }
    }

    private static Map<String, String> buildRequestParams(Scanner scanner) throws IllegalArgumentException {
        final Map<String, String> params = new HashMap<>();

        while (scanner.hasNext()) {
            final String line = scanner.nextLine();
            if (line.isEmpty()) {
                break;
            }

            final String[] keyValue = line.split(KEY_VALUE_SEPARATOR);
            if (keyValue.length != 2) {
                throw new IllegalArgumentException(StringUtils.format("Invalid parameter format: %s", line));
            }

            params.put(keyValue[0], keyValue[1]);
        }
        return params;
    }

    private static byte[] buildRequestBody(Scanner scanner, int bodyMaxSize) {
        byte[] body = new byte[bodyMaxSize];
        int read = 0;

        while (scanner.hasNextByte() && read < bodyMaxSize) {
            body[read++] = scanner.nextByte();
        }
        if (read < bodyMaxSize) {
            body = ByteUtils.resizeArray(body, read);
        }

        return body;
    }

}
