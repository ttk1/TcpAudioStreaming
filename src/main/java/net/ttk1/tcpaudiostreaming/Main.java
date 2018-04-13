package net.ttk1.tcpaudiostreaming;

public class Main {
    public static void main(String[] args) {
        // audio streaming test
        // server
        AudioStreamingServer asServer = new AudioStreamingServer(4567);
        Thread serverThread = new Thread(asServer);
        serverThread.start();

        // client
        AudioStreamingClient asClient = new AudioStreamingClient("127.0.0.1", 4567);
        Thread clientThread = new Thread(asClient);
        clientThread.start();
    }
}
