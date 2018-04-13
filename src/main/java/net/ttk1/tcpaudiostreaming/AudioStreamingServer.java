package net.ttk1.tcpaudiostreaming;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

public class AudioStreamingServer implements Runnable {
    private final int port;
    private ServerSocket serverSocket;

    // WAV音声ファイルのパスを入れる
    private final String audioFilePath = "src\\main\\resources\\test.wav";
    private File audioFile;

    public AudioStreamingServer(int port) {
        this.port = port;
        audioFile = new File(audioFilePath);
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                AudioStreamer streamer = new AudioStreamer(socket, audioFile);
                Thread streamingThread = new Thread(streamer);
                streamingThread.start();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private class AudioStreamer implements Runnable {
        private Socket socket;
        private AudioInputStream ais;

        public AudioStreamer(Socket socket, File audioFile) {
            this.socket = socket;
            try {
                this.ais = AudioSystem.getAudioInputStream(audioFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                OutputStream os = socket.getOutputStream();
                byte[] data = new byte[100];

                System.out.println("start streaming " + LocalDateTime.now().toString());

                while (true) {
                    int size = ais.read(data);
                    if (size == -1) {
                        break;
                    } else {
                        os.write(data, 0, size);
                    }
                }

                System.out.println("finish streaming " + LocalDateTime.now().toString());

                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
