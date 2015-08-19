package ru.kontur;

import java.io.*;
import java.net.Socket;

/**
 * @author Dmitry
 * @since 18.08.2015
 */
public class RequestHandler implements Runnable {
    private final Socket socket;
    private final WordSearcher wordSearcher;

    public RequestHandler(Socket socket, WordSearcher wordSearcher) {
        this.socket = socket;
        this.wordSearcher = wordSearcher;
    }

    @Override
    public void run() {
        try {
            //Поток для получения входных данных от клиента
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            //Поток для отправки ответа клиенту
            PrintWriter out = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                if (line.length() == 0)
                    break;
                System.out.println("Receive message:" + line);
            }

            //TODO add server response

            //Закрываем все соединения
            out.close();
            in.close();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
