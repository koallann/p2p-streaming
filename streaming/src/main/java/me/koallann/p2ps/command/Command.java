package me.koallann.p2ps.command;

public abstract class Command {

    public final Type type;

    public Command(Type type) {
        this.type = type;
    }

    public enum Type {
        CONNECT_ME, STREAMING
    }

}
