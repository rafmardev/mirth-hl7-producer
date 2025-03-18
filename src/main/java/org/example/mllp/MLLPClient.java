package org.example.mllp;

import org.example.event.ResponseEvent;
import org.example.event.ResponseListener;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executors;

public class MLLPClient {
    private static final char START_BLOCK = 0X0B;
    private static final char END_BLOCK = 0x1C;
    private static final char CARRIAGE_RETURN = 0x0D;

    private final String host;
    private final int port;
    private ResponseListener responseListener;

    public MLLPClient (String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void setResponseListener (ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    public void sendMessage (String hl7Message) {
        Executors.newSingleThreadExecutor().execute(() -> {
            String response = executeSending(hl7Message);
            if (responseListener != null) {
                responseListener.onResponseReceived(new ResponseEvent(response));
            }
        });
    }

    private String executeSending(String hl7Message) {
        String mllpMessage = START_BLOCK + hl7Message + END_BLOCK + CARRIAGE_RETURN;
        StringBuilder response = new StringBuilder();

        try (Socket socket = new Socket(host, port)) {
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            outputStream.write(mllpMessage.getBytes());
            outputStream.flush();

            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
                if (line.contains(String.valueOf(END_BLOCK))) {
                    break;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response.toString();
    }
}
