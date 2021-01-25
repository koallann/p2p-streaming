package me.koallann.p2ps.command;

public abstract class Command {

    public static final String RESPONSE_OK = "OK\n";
    public static final String RESPONSE_ERROR = "ERR\n";

    public final Type type;

    public Command(Type type) {
        this.type = type;
    }

    public enum Type {
        CONNECT_ME, STREAM
    }

}
