package me.koallann.streaming.sample;

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
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void holdOutput() {
        println("\nPress enter to continue...");
        scanner.nextLine();
    }

}
