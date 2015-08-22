package ru.kontur;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * @author Dmitry
 * @since 18.08.2015
 */
public class RequestHandler implements Runnable {
    private final Socket socket;
    private final IWordSearcher wordSearcher;

    /**
     * Создает объект для обработки запросов клиента
     *
     * @param socket       сокет для соеденения с клиентом
     * @param wordSearcher объект реализующий поиск наиболее часто употребляемых слов
     */
    public RequestHandler(Socket socket, IWordSearcher wordSearcher) {
        this.socket = socket;
        this.wordSearcher = wordSearcher;
    }

    /**
     * Метод отбрабатывающий запросы клиента в отдельном потоке
     */
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
                //Парсим запрос
                String[] response = line.split(" ");
                //Проверяем что запрос имеет вид "get prefix"
                if (response.length == 2 && response[0].equalsIgnoreCase("get")) {
                    String prefix = response[1];//получаем префикс
                    //получаем ответ
                    List<String> answer = this.wordSearcher.getMostFrequentlyUsedWords(prefix);
                    //отправляем ответ клиенту
                    answer.forEach(out::println);
                    out.println();
                    out.flush();
                }
            }
            //Закрываем все соединения
            out.close();
            in.close();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
