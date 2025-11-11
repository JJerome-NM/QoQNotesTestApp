package dev.jjerome.qoq.test.app.application.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@UtilityClass
public class NoteStatisticsUtils {
    private static final Pattern WORD_EXTRACTOR = Pattern.compile("[\\p{L}\\p{Nd}]+");

    public static Map<String, Integer> calculateWordFrequencies(String text) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptyMap();
        }

        Map<String, Long> freq = WORD_EXTRACTOR.matcher(text)
                .results()
                .map(MatchResult::group)
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(groupingBy(Function.identity(), Collectors.counting()));

        return freq.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()).thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().intValue(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }
}
