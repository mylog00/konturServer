package ru.kontur;

import java.util.List;

/**
 * @author Dmitry
 * @since 22.08.2015
 */
public interface IWordSearcher {
    /**
     * <p>
     * Метод возвращающий наиболее часто употребляемые слова начинающихся с {@code searchWord}
     * </p>
     *
     * @param searchWord строка с которой должны начинаться найденные слова
     * @return список наиболее часто употребляемых слов начинающихся с {@code searchWord}.
     */
    List<String> getMostFrequentlyUsedWords(String searchWord);
}
