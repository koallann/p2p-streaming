package me.koallann.p2ps.command;

import java.net.InetAddress;

public abstract class Command {

    public final Type type;
    public final InetAddress from;

    public Command(Type type, InetAddress from) {
        this.type = type;
        this.from = from;
    }

    public enum Type {
        CONNECT_ME, STREAMING
    }

}
