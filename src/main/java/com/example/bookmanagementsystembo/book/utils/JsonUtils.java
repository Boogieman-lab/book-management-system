package com.example.bookmanagementsystembo.book.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("[JsonUtils.toJson] JSON 직렬화 실패 value={}", obj, e);
            return null;
        }
    }
    public static List<String> toList(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("[JsonUtils.toList] JSON 역직렬화 실패 json={}", json, e);
            return List.of();
        }
    }
}
