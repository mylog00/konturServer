package ru.kontur;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Класс для поиска наиболее часто употребляемых слов
 *
 * @author Dmitry
 * @since 02.08.2015
 */
public class WordSearcher implements IWordSearcher {
    //Максимальная длинна списка
    private static final int MAX_ANSWER_NUMBER = 10;
    //Сортированный список всех солов
    private final List<String> sortedWordsList;
    //Частота повторения слова
    private final Map<String, Integer> wordsFrequency;
    //Кэш. Хранит результаты поисковых запросов
    private final Map<String, List<String>> resultCache;

    /**
     * Создает экземпляр объекта для поиска поиска наиболее часто употребляемых слов
     *
     * @param dictPath путь к файлу содержащему словарь наиболее часто встречающихся слов
     */
    public WordSearcher(String dictPath) {
        this(getWordsFrequencyFromFile(dictPath));
    }

    /**
     * Создает экземпляр объекта для поиска поиска наиболее часто употребляемых слов
     *
     * @param wordsFrequency Мап сопоставления слов с частой их повторения.
     *                       {@code Кey} - слово, {@code value} - частота повторения слова
     */
    public WordSearcher(Map<String, Integer> wordsFrequency) {
        this.sortedWordsList = new ArrayList<>(wordsFrequency.keySet());
        Collections.sort(this.sortedWordsList);
        this.wordsFrequency = wordsFrequency;
        this.resultCache = new HashMap<>(this.sortedWordsList.size() / 2);
    }

    /**
     * <p>
     * Метод возвращающий наиболее часто употребляемые слова начинающихся с {@code searchWord}
     * в порядке убывания частоты. В случае совпадения частот слова сортируются по алфавиту.
     * Длинна выходного списка не превышает 10 слов.
     * </p>
     *
     * @param searchWord строка с которой должны начинаться найденные слова
     * @return список наиболее часто употребляемых слов начинающихся с {@code searchWord}.<br/>
     * Длинна списка не может быть больше 10.
     */
    @Override
    public List<String> getMostFrequentlyUsedWords(String searchWord) {
        //Поиск результата в кэше
        if (this.resultCache.containsKey(searchWord)) {
            //Если для этого слова уже есть результат, возвращаем его
            return this.resultCache.get(searchWord);
        }

        //Поиск индекса первого слова начинающегося с указанной строки
        final int firstElementPos = findFirstBinarySearch(this.sortedWordsList, searchWord);
        List<String> result = Collections.<String>emptyList();
        //Если подходящих слов нет, то результатом будет пустая строка.
        if (firstElementPos >= 0) {
            //Поиск индекса последнего слова начинающегося с указанной строки
            final int lastElementPos = findLastBinarySearch(this.sortedWordsList, searchWord);
            //Составляем подсписок из найденных слов
            List<String> matchedWords = new ArrayList<>(this.sortedWordsList.subList(firstElementPos, lastElementPos + 1));
            //Сортируем в соответсвии с правилами
            Collections.sort(matchedWords, (s1, s2) -> {
                Integer f1 = this.wordsFrequency.get(s1);
                Integer f2 = this.wordsFrequency.get(s2);
                //сначала пытаемся отсортировать по частоте (сначала самые частые)
                int res = Integer.compare(f2, f1);
                if (res == 0) {
                    //если частоты одинаковые то, то по алфавиту
                    return s1.compareTo(s2);
                }
                return res;
            });
            //ограничиваем результат первыми десятью значениями
            result = new ArrayList<>(matchedWords.subList(0, Math.min(matchedWords.size(), MAX_ANSWER_NUMBER)));
        }
        //добавляем результаты поиска для строки в кэш
        this.resultCache.put(searchWord, result);
        //возвращаем результат
        return result;
    }

    /**
     * Бинарный поиск первого слова начинающегося с искомой строки
     *
     * @param l   Список в котором производится поиск
     * @param key строка с которой должно начинаться искомое слово
     * @return индекс первого слова начинающегося с {@code key}.
     * Если элемент не найден возвращает отрицательное число
     */
    private int findFirstBinarySearch(List<String> l, String key) {
        int low = 0;
        int high = l.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            String midVal = l.get(mid);
            int cmp = midVal.substring(0, Math.min(key.length(), midVal.length())).compareTo(key);

            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else if (low != mid) //Equal but range is not fully scanned
                high = mid; //Set upper bound to current number and rescan
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found
    }

    /**
     * Бинарный поиск последнего слова начинающегося с искомой строки
     *
     * @param l   Список в котором производится поиск
     * @param key строка с которой должно начинаться искомое слово
     * @return индекс последнего слова начинающегося с {@code key}
     * Если элемент не найден возвращает отрицательное число
     */
    private int findLastBinarySearch(List<String> l, String key) {
        int low = 0;
        int high = l.size() - 1;

        while (low <= high) {
            int mid = (low + high + 1) >>> 1;
            String midVal = l.get(mid);
            int cmp = midVal.substring(0, Math.min(key.length(), midVal.length())).compareTo(key);

            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else if (high != mid) //Equal but range is not fully scanned
                low = mid; //Set lower bound to current number and rescan
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found
    }

    /**
     * Метод для получения словаря наиболее часто встречающихся слов из файла
     *
     * @param filePath путь к файлу содержащему словарь наиболее часто встречающихся слов
     * @return Мап сопоставления слов с частой их повторения.
     * {@code Кey} - слово, {@code value} - частота повторения слова
     * @throws IllegalStateException если не удается получить словарь из файла
     */
    private static Map<String, Integer> getWordsFrequencyFromFile(String filePath) {
        File file = new File(filePath);
        Scanner in;
        try {
            in = new Scanner(file);
            int wordNumber = in.nextInt();//количество слов в файле
            Map<String, Integer> wordsFrequency = new HashMap<>(wordNumber);//Частота повторения слова
            while (wordNumber > 0) {
                String word = in.next();//слово
                Integer frequency = in.nextInt();//частота его повторения
                wordsFrequency.put(word, frequency);
                wordNumber--;
            }
            in.close();
            return wordsFrequency;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Can`t load dictionary from file");
    }
}
