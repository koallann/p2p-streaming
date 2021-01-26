package me.koallann.chat;

import me.koallann.p2ps.P2pManager;
import me.koallann.p2ps.command.StreamingCommand;

import java.io.IOException;

public class ChatConsole extends Console implements P2pManager.OnReceiveStreamingListener {

    private static final int OPTION_MENU_EXIT = 0;
    private static final int OPTION_MENU_REQUEST_CONNECT_ME = 1;
    private static final int OPTION_MENU_MAKE_STREAMING = 2;

    private final P2pManager p2pManager;

    public ChatConsole() {
        try {
            this.p2pManager = new P2pManager(9876, this);
        } catch (IOException e) {
            throw new IllegalStateException("Error initializing P2P manager", e);
        }
    }

    public void start() {
        this.p2pManager.start();
        menuLoop();
        this.p2pManager.stop();
    }

    private void menuLoop() {
        clear();
        println("#########\n" +
            "MENU\n" +
            "#########\n\n" +
            "1 - Send CONNECT_ME request\n" +
            "2 - Make STREAMING\n" +
            "0 - Exit\n"
        );
        print("Option: ");

        try {
            int option = Integer.parseInt(scanner.nextLine());
            println();

            if (!handleMenuChoose(option)) {
                holdOutput();
                return;
            }
            menuLoop();
        } catch (Exception e) {
            println("\nInvalid option!");
            holdOutput();
            menuLoop();
        }
    }

    private boolean handleMenuChoose(int option) {
        switch (option) {
            case OPTION_MENU_EXIT:
                println("Exiting...");
                return false;
            case OPTION_MENU_REQUEST_CONNECT_ME:
                onRequestConnectMe();
                return true;
            case OPTION_MENU_MAKE_STREAMING:
                onMakeStreaming();
                return true;
            default:
                println("Invalid option!");
                holdOutput();
                return true;
        }
    }

    private void onRequestConnectMe() {
        println("onRequestConnectMe");
    }

    private void onMakeStreaming() {
        println("onMakeStreaming");
    }

    @Override
    public void onReceiveStreaming(StreamingCommand cmd) {
        println("onReceiveStreaming");
    }

}
