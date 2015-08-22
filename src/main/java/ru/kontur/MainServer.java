package ru.kontur;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Dmitry
 * @since 18.08.2015
 */
public class MainServer {
    public static void main(String[] args) {
        //Путь к текстовому	файлу, содержащему словарь
        String dictPath = args[0];
        //Порт для прослушивания
        int port = Integer.parseInt(args[1]);

        final IWordSearcher wordSearcher = new WordSearcher(dictPath);
        //Пул для вополнения потоков
        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            //Создаем сервер
            ServerSocket server = new ServerSocket(port);
            System.out.println("Server started on port:" + port);
            System.out.println("Dictionary path:" + dictPath);
            //noinspection InfiniteLoopStatement
            while (true) {
                Socket clientSocket = server.accept();
                RequestHandler requestHandler = new RequestHandler(clientSocket, wordSearcher);
                executorService.execute(requestHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Server stopped");
        }
    }
}
