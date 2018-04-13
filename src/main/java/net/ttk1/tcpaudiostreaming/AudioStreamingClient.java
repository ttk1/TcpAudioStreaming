package net.ttk1.tcpaudiostreaming;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.io.InputStream;
import java.net.Socket;

public class AudioStreamingClient implements Runnable {
    private final String addr;
    private final int port;

    public AudioStreamingClient(String addr, int port) {
        this.addr = addr;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(addr, port);

            //PCM_SIGNED 44100.0 Hz, 16 bit, stereo, 4 bytes/frame, little-endian
            //前もって調べておく
            AudioFormat af = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2,4,44100.0F,false);

            InputStream is = socket.getInputStream();

            DataLine.Info dataineInfo = new DataLine.Info(SourceDataLine.class, af);
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataineInfo);
            sourceDataLine.open();
            sourceDataLine.start();

            byte[] data = new byte[100];
            while (true) {
                int size = is.read(data);
                if (size == -1) {
                    break;
                } else {
                    sourceDataLine.write(data, 0, size);
                }
            }
            sourceDataLine.drain();
            sourceDataLine.stop();
            sourceDataLine.close();

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
