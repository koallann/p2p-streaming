package me.koallann.p2ps.command;

import java.net.InetAddress;

public abstract class Command {

    public enum Type {
        CONNECT_ME, STREAMING
    }

    public final Type type;
    public final InetAddress src;

    public Command(Type type, InetAddress src) {
        this.type = type;
        this.src = src;
    }

}
