package me.koallann.chat;

public class Application {

    public static void main(String[] args) {
        System.out.println("Application started\n");

        final ChatConsole console = new ChatConsole();
        console.start();

        System.out.println("Application finished");
    }

}
