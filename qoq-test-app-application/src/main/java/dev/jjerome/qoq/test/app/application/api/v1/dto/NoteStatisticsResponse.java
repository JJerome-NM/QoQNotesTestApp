package dev.jjerome.qoq.test.app.application.api.v1.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class NoteStatisticsResponse {
    private String id;
    private Map<String, Integer> uniqueWordCounts;
}
