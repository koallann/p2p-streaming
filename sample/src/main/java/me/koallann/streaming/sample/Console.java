package me.koallann.streaming.sample;

import java.io.IOException;
import java.util.Scanner;

public class Console {

    protected final Scanner scanner;

    public Console() {
        this.scanner = new Scanner(System.in);
    }

    public void print(String s) {
        System.out.print(s);
    }

    public void println(String s) {
        System.out.println(s);
    }

    public void println() {
        System.out.println();
    }

    public void clear() {
        try {
            Runtime.getRuntime().exec("clear");
        } catch (IOException ignored) {
        }
    }

    public void holdOutput() {
        println("\nPress enter to continue...");
        scanner.nextLine();
    }

}
