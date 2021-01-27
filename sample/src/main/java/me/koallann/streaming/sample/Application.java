package me.koallann.streaming.sample;

public class Application {

    public static void main(String[] args) {
        System.out.println("Application started\n");

        final ClientConsole console = new ClientConsole();
        console.start();

        System.out.println("\nApplication finished\n");
    }

}
