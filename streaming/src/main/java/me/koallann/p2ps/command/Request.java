package me.koallann.p2ps.command;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import me.koallann.p2ps.util.StringUtils;

public class Request {

    private static final char LINE_DELIMITER = '\n';
    private static final String KEY_VALUE_SEPARATOR = ": ";

    public final InetAddress src;
    public final Command.Type type;
    public final Map<String, String> params;
    public final byte[] content;

    public Request(InetAddress src, Command.Type type, Map<String, String> params, byte[] content) {
        this.src = src;
        this.type = type;
        this.params = params;
        this.content = content;
    }

    public byte[] encode() throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(type.name().getBytes());
        outputStream.write(LINE_DELIMITER);

        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                outputStream.write(entry.getKey().getBytes());
                outputStream.write(KEY_VALUE_SEPARATOR.getBytes());
                outputStream.write(entry.getValue().getBytes());
                outputStream.write(LINE_DELIMITER);
            }
        }

        if (content != null) {
            outputStream.write(LINE_DELIMITER);
            outputStream.write(content);
        }

        outputStream.close();
        return outputStream.toByteArray();
    }

    public static Request from(InetAddress src, byte[] bytes) throws IOException {
        final Scanner scanner = new Scanner(new ByteArrayInputStream(bytes));
        scanner.useDelimiter(Character.toString(LINE_DELIMITER));

        final String typeLine = scanner.nextLine();
        final Command.Type type = Command.Type.valueOf(typeLine);

        final Map<String, String> params = buildParams(scanner);
        final byte[] content = buildContent(scanner);

        scanner.close();
        return new Request(src, type, params, content);
    }

    private static Map<String, String> buildParams(Scanner scanner) throws IllegalArgumentException {
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

    private static byte[] buildContent(Scanner scanner) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        while (scanner.hasNextLine()) {
            final String line = scanner.nextLine();
            if (line.isEmpty()) {
                break;
            }
            outputStream.write(line.getBytes());

            if (scanner.hasNextLine()) {
                outputStream.write(LINE_DELIMITER);
            }
        }

        outputStream.close();
        return outputStream.toByteArray();
    }

}
